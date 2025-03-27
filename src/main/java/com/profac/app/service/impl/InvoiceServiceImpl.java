package com.profac.app.service.impl;

import com.profac.app.domain.Invoice;
import com.profac.app.domain.InvoiceProduct;
import com.profac.app.domain.enumeration.InvoiceStatus;
import com.profac.app.repository.InvoiceProductRepository;
import com.profac.app.repository.InvoiceRepository;
import com.profac.app.service.*;
import com.profac.app.service.dto.*;
import com.profac.app.service.mapper.InvoiceMapper;
import com.profac.app.service.mapper.ProductMapper;
import com.profac.app.utils.exception.BusinessNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link com.profac.app.domain.Invoice}.
 */
@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);

    private final InvoiceRepository invoiceRepository;

    private final InvoiceMapper invoiceMapper;
    private final InvoiceProductRepository invoiceProductRepository;
    private final ProductService productService;
    private final CompanyService companyService;
    private final ProductMapper productMapper;
    private final StockService stockService;
    @Value("${initial-number.invoice}") Integer initialInvoiceNumber;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              InvoiceMapper invoiceMapper,
                              InvoiceProductRepository invoiceProductRepository,
                              ProductService productService, CompanyService companyService,
                              ProductMapper productMapper
        , StockService stockService) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
        this.invoiceProductRepository = invoiceProductRepository;
        this.productService = productService;
        this.companyService = companyService;
        this.productMapper = productMapper;
        this.stockService = stockService;
    }

    @Override
    @Transactional
    public Mono<InvoiceDTO> save(InvoiceDTO invoiceDTO) {
        log.debug("Request to save Invoice : {}", invoiceDTO);

        return invoiceDTO.initAuditFields()
            .then(invoiceRepository.count())
            .flatMap(count -> {
                invoiceDTO.setInvoiceNumber(initialInvoiceNumber + count);
                invoiceDTO.setStatus(InvoiceStatus.PENDING);
                return companyService.findByPhoneNumber()
                    .flatMap(company -> {
                        Invoice invoice = invoiceMapper.toEntity(invoiceDTO);
                        invoice.setCompany(company);
                        return invoiceRepository.save(invoice)
                            .flatMap(savedInvoice -> processInvoiceProducts(invoiceDTO, savedInvoice))
                            .flatMap(savedInvoice -> invoiceRepository.findById(savedInvoice.getId()))
                            .map(invoiceMapper::toDto);
                    });
            });
    }

    private Mono<Invoice> processInvoiceProducts(InvoiceDTO invoiceDTO, Invoice savedInvoice) {
        return Flux.fromIterable(invoiceDTO.getProducts())
            .flatMap(productDTO ->
                productService.findByProductNumber(productDTO.getProductNumber())
                    .flatMap(product -> stockService.updateStock(product, productDTO.getQuantity())
                        .then(Mono.fromCallable(() -> {
                            int quantity = productDTO.getQuantity();
                            BigDecimal totalAmount = product.getAmount().multiply(BigDecimal.valueOf(quantity));
                            InvoiceProduct invoiceProduct = new InvoiceProduct();
                            invoiceProduct.setProduct(product);
                            invoiceProduct.setQuantity(quantity);
                            invoiceProduct.setTotalAmount(totalAmount);
                            invoiceProduct.setInvoice(savedInvoice);
                            return invoiceProduct;
                        })))
            )
            .collectList()
            .flatMap(invoiceProducts -> invoiceProductRepository.saveAll(invoiceProducts).collectList()) // Save them all
            .thenReturn(savedInvoice);
    }

    @Override
    public Mono<InvoiceDTO> update(InvoiceDTO invoiceDTO) {
        log.debug("Request to update Invoice : {}", invoiceDTO);
        return invoiceRepository.save(invoiceMapper.toEntity(invoiceDTO)).map(invoiceMapper::toDto);
    }

    @Override
    public Mono<InvoiceDTO> partialUpdate(InvoiceDTO invoiceDTO) {
        log.debug("Request to partially update Invoice : {}", invoiceDTO);

        return invoiceRepository
            .findById(invoiceDTO.getId())
            .map(existingInvoice -> {
                invoiceMapper.partialUpdate(existingInvoice, invoiceDTO);

                return existingInvoice;
            })
            .flatMap(invoiceRepository::save)
            .map(invoiceMapper::toDto);
    }
    public Flux<InvoiceDTO> findAllWithEagerRelationships(Pageable pageable) {
        return invoiceRepository.findAllBy(pageable).map(invoiceMapper::toDto);
    }

    public Mono<Long> countAll() {
        return invoiceRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<InvoiceDTO> findOne(Long id) {
        log.debug("Request to get Invoice : {}", id);
        return invoiceRepository.findById(id).map(invoiceMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Invoice : {}", id);
        return invoiceRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Page<InvoiceResponseDTO>> findAll(int page, int size) {
        int offset = page * size;
        return companyService.findByPhoneNumber()
            .flatMap(company -> invoiceRepository.countByCompanyId(company.getId())
                .zipWith(invoiceRepository.findByCompany(company.getId(), size, offset).collectList())
                .flatMap(tuple -> mapInvoicesToDTO(tuple.getT2())
                    .map(invoices -> new PageImpl<>(invoices, PageRequest.of(page, size), tuple.getT1()))
                )
            );
    }

    @Override
    public Mono<InvoiceResponseDTO> findByInvoiceNumber(Long invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
            .flatMap(this::mapInvoiceToDTO)
            .switchIfEmpty(Mono.error(new BusinessNotFoundException("Invoice not found with number: " + invoiceNumber)));
    }

    private Mono<List<InvoiceResponseDTO>> mapInvoicesToDTO(List<Invoice> invoices) {
        return Flux.fromIterable(invoices)
            .flatMap(this::mapInvoiceToDTO)
            .collectList();
    }

    private Mono<InvoiceResponseDTO> mapInvoiceToDTO(Invoice invoice) {
        return findAllInvoiceProductByInvoice(invoice)
            .collectList()
            .flatMap(invoiceProducts ->
                mapProductDetails(invoiceProducts)
                    .publishOn(Schedulers.boundedElastic())
                    .map(productQuantitySet -> {
                        BigDecimal totalAmount = invoiceProducts.stream()
                            .map(InvoiceProduct::getTotalAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                        return this.mapInvoiceResponseDTO(invoice, productQuantitySet, totalAmount);
                    })
            );
    }

    private Mono<Set<Map.Entry<ProductDTO, Integer>>> mapProductDetails(List<InvoiceProduct> invoiceProducts) {
        return Flux.fromIterable(invoiceProducts)
            .flatMap(invoiceProduct -> Mono.justOrEmpty(invoiceProduct.getProductId())
                .flatMap(productService::findOne)
                .map(product -> Map.entry(product, invoiceProduct.getQuantity()))
            )
            .collect(Collectors.toSet());
    }


    public Flux<InvoiceProduct> findAllInvoiceProductByInvoice(Invoice invoice) {
        return invoiceProductRepository.findWithInvoiceAndProductByInvoiceId(invoice.getId());
    }
    public InvoiceResponseDTO mapInvoiceResponseDTO(Invoice invoice, Set<Map.Entry<ProductDTO, Integer>> productQuantitySet, BigDecimal amount) {
        InvoiceResponseDTO invoiceResponseDTO = new InvoiceResponseDTO();
        invoiceResponseDTO.setInvoiceNumber(invoice.getInvoiceNumber());
        invoiceResponseDTO.setAmount(amount);
        invoiceResponseDTO.setCustomer(invoice.getCustomer());
        invoiceResponseDTO.setStatus(invoice.getStatus());
        invoiceResponseDTO.setProducts(mapProductResponseDTO(productQuantitySet));
        return invoiceResponseDTO;
    }

    public Set<ProductResponseDTO> mapProductResponseDTO(Set<Map.Entry<ProductDTO, Integer>> productQuantitySet) {
        if (productQuantitySet == null) {
            return null;
        }
        Set<ProductResponseDTO> res = new HashSet<>();
        productQuantitySet.forEach(entry -> {
            ProductDTO product = entry.getKey();
            Integer quantity = entry.getValue();
            ProductResponseDTO productResponseDTO = new ProductResponseDTO();
            productResponseDTO.setId(product.getId());
            productResponseDTO.setProductNumber(product.getProductNumber());
            productResponseDTO.setAmount(new BigDecimal(product.getAmount().stripTrailingZeros().toPlainString()));
            productResponseDTO.setCategory(product.getCategory());
            productResponseDTO.setName(product.getName());
            productResponseDTO.setStatus(product.getStatus());
            productResponseDTO.setQuantity(quantity);
            productResponseDTO.setDescription(product.getDescription());
            res.add(productResponseDTO);
        });
        return res;
    }


}
