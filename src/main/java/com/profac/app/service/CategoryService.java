package com.profac.app.service;

import com.profac.app.domain.Category;
import com.profac.app.service.dto.CategoryDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.profac.app.domain.Category}.
 */
public interface CategoryService {
    /**
     * Save a category.
     *
     * @param categoryDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<CategoryDTO> save(Category categoryDTO);

    /**
     * Updates a category.
     *
     * @param categoryDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<CategoryDTO> update(CategoryDTO categoryDTO);

    /**
     * Partially updates a category.
     *
     * @param categoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<CategoryDTO> partialUpdate(CategoryDTO categoryDTO);

    /**
     * Get all the categories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CategoryDTO> findAll(Pageable pageable);

    @Transactional(readOnly = true)
    Flux<CategoryDTO> findAllByCreatedBy(String name);

    /**
     * Returns the number of categories available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" category.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<CategoryDTO> findOne(Long id);

    Mono<CategoryDTO> findByName(String name);

    /**
     * Delete the "id" category.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
