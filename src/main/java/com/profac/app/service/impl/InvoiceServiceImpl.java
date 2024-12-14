package com.profac.app.service.impl;

import com.profac.app.repository.InvoiceRepository;
import com.profac.app.service.InvoiceService;
import com.profac.app.service.dto.InvoiceDTO;
import com.profac.app.service.mapper.InvoiceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.profac.app.domain.Invoice}.
 */
@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);

    private final InvoiceRepository invoiceRepository;

    private final InvoiceMapper invoiceMapper;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, InvoiceMapper invoiceMapper) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
    }

    @Override
    public Mono<InvoiceDTO> save(InvoiceDTO invoiceDTO) {
        log.debug("Request to save Invoice : {}", invoiceDTO);
        return invoiceRepository.save(invoiceMapper.toEntity(invoiceDTO)).map(invoiceMapper::toDto);
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

    @Override
    @Transactional(readOnly = true)
    public Flux<InvoiceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Invoices");
        return invoiceRepository.findAllBy(pageable).map(invoiceMapper::toDto);
    }

    public Flux<InvoiceDTO> findAllWithEagerRelationships(Pageable pageable) {
        return invoiceRepository.findAllWithEagerRelationships(pageable).map(invoiceMapper::toDto);
    }

    public Mono<Long> countAll() {
        return invoiceRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<InvoiceDTO> findOne(Long id) {
        log.debug("Request to get Invoice : {}", id);
        return invoiceRepository.findOneWithEagerRelationships(id).map(invoiceMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Invoice : {}", id);
        return invoiceRepository.deleteById(id);
    }
}
