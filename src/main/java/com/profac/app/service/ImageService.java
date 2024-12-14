package com.profac.app.service;

import com.profac.app.service.dto.ImageDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.profac.app.domain.Image}.
 */
public interface ImageService {
    /**
     * Save a image.
     *
     * @param imageDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ImageDTO> save(ImageDTO imageDTO);

    /**
     * Updates a image.
     *
     * @param imageDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ImageDTO> update(ImageDTO imageDTO);

    /**
     * Partially updates a image.
     *
     * @param imageDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ImageDTO> partialUpdate(ImageDTO imageDTO);

    /**
     * Get all the images.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ImageDTO> findAll(Pageable pageable);

    /**
     * Get all the ImageDTO where AppUser is {@code null}.
     *
     * @return the {@link Flux} of entities.
     */
    Flux<ImageDTO> findAllWhereAppUserIsNull();

    /**
     * Returns the number of images available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" image.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ImageDTO> findOne(Long id);

    /**
     * Delete the "id" image.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
