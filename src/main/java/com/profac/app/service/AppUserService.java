package com.profac.app.service;

import com.profac.app.domain.Company;
import com.profac.app.service.dto.AppUserDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.profac.app.domain.AppUser}.
 */
public interface AppUserService {
    /**
     * Save a appUser.
     *
     * @param appUserDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<AppUserDTO> save(AppUserDTO appUserDTO);

    /**
     * Updates a appUser.
     *
     * @param appUserDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<AppUserDTO> update(AppUserDTO appUserDTO);

    /**
     * Partially updates a appUser.
     *
     * @param appUserDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<AppUserDTO> partialUpdate(AppUserDTO appUserDTO);

    /**
     * Get all the appUsers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<AppUserDTO> findAll(Pageable pageable);

    /**
     * Returns the number of appUsers available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" appUser.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<AppUserDTO> findOne(Long id);

    @Transactional(readOnly = true)
    Mono<AppUserDTO> findByPhoneNumber(String phone);

    /**
     * Delete the "id" appUser.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    @Transactional(readOnly = true)
    Flux<AppUserDTO> findByCompany(Company company, Pageable pageable);
}
