package com.profac.app.service.impl;

import com.profac.app.domain.Product;
import com.profac.app.domain.Stock;
import com.profac.app.domain.enumeration.StockStatus;
import com.profac.app.repository.StockRepository;
import com.profac.app.security.SecurityUtils;
import com.profac.app.service.CompanyService;
import com.profac.app.service.ImageService;
import com.profac.app.service.ProductService;
import com.profac.app.service.StockService;
import com.profac.app.service.dto.CompanyDTO;
import com.profac.app.service.dto.ImageDTO;
import com.profac.app.service.dto.ProductDTO;
import com.profac.app.service.dto.StockDTO;
import com.profac.app.service.mapper.StockMapper;
import com.profac.app.utils.encoder.Base64Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Service Implementation for managing {@link com.profac.app.domain.Stock}.
 */
@Service
@Transactional
public class StockServiceImpl implements StockService {

    private final Logger log = LoggerFactory.getLogger(StockServiceImpl.class);

    private final StockRepository stockRepository;

    private final StockMapper stockMapper;
    private final ProductService productService;
    private final ImageService imageService;
private final CompanyService companyService;
    public StockServiceImpl(StockRepository stockRepository, StockMapper stockMapper, ProductService productService, ImageService imageService, CompanyService companyService) {
        this.stockRepository = stockRepository;
        this.stockMapper = stockMapper;
        this.productService = productService;
        this.imageService = imageService;
        this.companyService = companyService;
    }

    @Override
    @Transactional
    public Mono<StockDTO> save(StockDTO stockDTO, FilePart image) {
        log.debug("Request to save Stock : {}", stockDTO);
        ProductDTO product = stockDTO.getProduct();
        stockDTO.setRemainingQuantity(stockDTO.getInitialQuantity());
        stockDTO.setStatus(StockStatus.ACTIVE);
        stockDTO.setTotalAmount(product.getAmount().multiply(BigDecimal.valueOf(stockDTO.getInitialQuantity())));
        stockDTO.setTotalAmountSold(BigDecimal.ZERO);
        return stockDTO.initAuditFields()
            .then(productService.save(product))
            .doOnNext(s -> log.error("saved: {}", s))
            .flatMap(productDTO -> {
                ImageDTO imageDTO = new ImageDTO();
                return Base64Encoder.encodeFileToBase64Binary(image)
                    .map(encodedImage -> {
                        imageDTO.setUrl(encodedImage);
                        imageDTO.setProduct(productDTO);
                        return imageDTO;
                    })
                    .flatMap(imageService::save)
                    .thenReturn(productDTO);
            })
            .flatMap(savedProduct -> {
                stockDTO.setProduct(savedProduct);
                Stock stock = stockMapper.toEntity(stockDTO);
                return SecurityUtils.getCurrentUserLogin()
                    .flatMap(login -> companyService.findByPhoneNumber())
                    .flatMap(company -> {
                        stock.setCompany(company);
                        return stockRepository.save(stock);
                    })
                    .map(stockMapper::toDto);
            });
    }


    @Override
    public Mono<StockDTO> update(StockDTO stockDTO) {
        log.debug("Request to update Stock : {}", stockDTO);
        return stockRepository.save(stockMapper.toEntity(stockDTO)).map(stockMapper::toDto);
    }

    @Override
    public Mono<StockDTO> partialUpdate(StockDTO stockDTO) {
        log.debug("Request to partially update Stock : {}", stockDTO);

        return stockRepository
            .findById(stockDTO.getId())
            .map(existingStock -> {
                stockMapper.partialUpdate(existingStock, stockDTO);

                return existingStock;
            })
            .flatMap(stockRepository::save)
            .map(stockMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Flux<StockDTO> findAll(int page, int size) {
        int offset = page * size;
        log.debug("Request to get all Stocks");
        return companyService.findByPhoneNumber()
            .flatMapMany(c -> stockRepository
                .findAllByCompanyId(c.getId(), size, offset)
                .flatMap(stock ->
                    Mono.zip(
                        Mono.just(stock),
                        companyService.findOne(stock.getCompanyId()),
                        productService.findOne(stock.getProductId())
                    )
                )
                .map(tuple -> {
                    Stock stock = tuple.getT1();
                    CompanyDTO company = tuple.getT2();
                    ProductDTO product = tuple.getT3();

                    StockDTO stockDTO = stockMapper.toDto(stock);
                    stockDTO.setCompany(company);
                    stockDTO.setProduct(product);

                    return stockDTO;
                }));
    }


    public Mono<Long> countAll() {
        return stockRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<StockDTO> findOne(Long id) {
        log.debug("Request to get Stock : {}", id);
        return stockRepository.findById(id).map(stockMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Stock : {}", id);
        return stockRepository.deleteById(id);
    }
    @Override
    public Mono<Void> updateStock(Product product, int quantity) {
        return stockRepository.findByProductIdAndStatus(product.getId(), StockStatus.ACTIVE)
                .flatMap(stock -> {
                    BigDecimal invoiceTotalAmount = product.getAmount().multiply(BigDecimal.valueOf(quantity));
                    Integer remainingQuantity = stock.getRemainingQuantity();
                    stock.setRemainingQuantity(remainingQuantity - quantity);
                    stock.setTotalAmountSold(stock.getTotalAmountSold().add(invoiceTotalAmount));
                    return partialUpdate(stockMapper.toDto(stock));
                })
            .then();
    }

}
