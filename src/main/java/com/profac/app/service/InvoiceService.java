package com.profac.app.service;

import com.profac.app.service.dto.InvoiceDTO;
import com.profac.app.service.dto.InvoiceResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.profac.app.domain.Invoice}.
 */
public interface InvoiceService {
    /**
     * Save a invoice.
     *
     * @param invoiceDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<InvoiceDTO> save(InvoiceDTO invoiceDTO);

    /**
     * Updates a invoice.
     *
     * @param invoiceDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<InvoiceDTO> update(InvoiceDTO invoiceDTO);

    /**
     * Partially updates a invoice.
     *
     * @param invoiceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<InvoiceDTO> partialUpdate(InvoiceDTO invoiceDTO);

    /**
     * Get all the invoices with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<InvoiceDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of invoices available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" invoice.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<InvoiceDTO> findOne(Long id);

    /**
     * Delete the "id" invoice.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    Mono<Page<InvoiceResponseDTO>> findAll(int page, int size);

    Mono<InvoiceResponseDTO> findByInvoiceNumber(Long invoiceNumber);
}
