package com.profac.app.service;

import com.profac.app.service.dto.CompanyDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.profac.app.domain.Company}.
 */
public interface CompanyService {
    /**
     * Save a company.
     *
     * @param companyDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<CompanyDTO> save(CompanyDTO companyDTO);

    /**
     * Updates a company.
     *
     * @param companyDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<CompanyDTO> update(CompanyDTO companyDTO);

    /**
     * Partially updates a company.
     *
     * @param companyDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<CompanyDTO> partialUpdate(CompanyDTO companyDTO);

    /**
     * Get all the companies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CompanyDTO> findAll(Pageable pageable);

    /**
     * Returns the number of companies available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" company.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<CompanyDTO> findOne(Long id);

    /**
     * Delete the "id" company.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
