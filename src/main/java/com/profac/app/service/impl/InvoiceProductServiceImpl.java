package com.profac.app.service.impl;

import com.profac.app.repository.InvoiceProductRepository;
import com.profac.app.service.InvoiceProductService;
import com.profac.app.service.dto.InvoiceProductDTO;
import com.profac.app.service.mapper.InvoiceProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.profac.app.domain.InvoiceProduct}.
 */
@Service
@Transactional
public class InvoiceProductServiceImpl implements InvoiceProductService {

    private final Logger log = LoggerFactory.getLogger(InvoiceProductServiceImpl.class);

    private final InvoiceProductRepository invoiceProductRepository;

    private final InvoiceProductMapper invoiceProductMapper;

    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository, InvoiceProductMapper invoiceProductMapper) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.invoiceProductMapper = invoiceProductMapper;
    }

    @Override
    public Mono<InvoiceProductDTO> save(InvoiceProductDTO invoiceProductDTO) {
        log.debug("Request to save InvoiceProduct : {}", invoiceProductDTO);
        return invoiceProductRepository.save(invoiceProductMapper.toEntity(invoiceProductDTO)).map(invoiceProductMapper::toDto);
    }

    @Override
    public Mono<InvoiceProductDTO> update(InvoiceProductDTO invoiceProductDTO) {
        log.debug("Request to update InvoiceProduct : {}", invoiceProductDTO);
        return invoiceProductRepository.save(invoiceProductMapper.toEntity(invoiceProductDTO)).map(invoiceProductMapper::toDto);
    }

    @Override
    public Mono<InvoiceProductDTO> partialUpdate(InvoiceProductDTO invoiceProductDTO) {
        log.debug("Request to partially update InvoiceProduct : {}", invoiceProductDTO);

        return invoiceProductRepository
            .findById(invoiceProductDTO.getId())
            .map(existingInvoiceProduct -> {
                invoiceProductMapper.partialUpdate(existingInvoiceProduct, invoiceProductDTO);

                return existingInvoiceProduct;
            })
            .flatMap(invoiceProductRepository::save)
            .map(invoiceProductMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<InvoiceProductDTO> findAll(Pageable pageable) {
        log.debug("Request to get all InvoiceProducts");
        return invoiceProductRepository.findAllBy(pageable).map(invoiceProductMapper::toDto);
    }

    public Mono<Long> countAll() {
        return invoiceProductRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<InvoiceProductDTO> findOne(Long id) {
        log.debug("Request to get InvoiceProduct : {}", id);
        return invoiceProductRepository.findById(id).map(invoiceProductMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete InvoiceProduct : {}", id);
        return invoiceProductRepository.deleteById(id);
    }
}
