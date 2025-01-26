package com.profac.app.service;

import com.profac.app.service.dto.InvoiceProductDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.profac.app.domain.InvoiceProduct}.
 */
public interface InvoiceProductService {
    /**
     * Save a invoiceProduct.
     *
     * @param invoiceProductDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<InvoiceProductDTO> save(InvoiceProductDTO invoiceProductDTO);

    /**
     * Updates a invoiceProduct.
     *
     * @param invoiceProductDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<InvoiceProductDTO> update(InvoiceProductDTO invoiceProductDTO);

    /**
     * Partially updates a invoiceProduct.
     *
     * @param invoiceProductDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<InvoiceProductDTO> partialUpdate(InvoiceProductDTO invoiceProductDTO);

    /**
     * Get all the invoiceProducts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<InvoiceProductDTO> findAll(Pageable pageable);

    /**
     * Returns the number of invoiceProducts available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" invoiceProduct.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<InvoiceProductDTO> findOne(Long id);

    /**
     * Delete the "id" invoiceProduct.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
