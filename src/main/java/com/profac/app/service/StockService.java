package com.profac.app.service;

import com.profac.app.domain.Product;
import com.profac.app.service.dto.ProductDTO;
import com.profac.app.service.dto.StockDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * Service Interface for managing {@link com.profac.app.domain.Stock}.
 */
public interface StockService {
    /**
     * Save a stock.
     *
     * @param stockDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<StockDTO> save(StockDTO stockDTO, FilePart image);

    /**
     * Updates a stock.
     *
     * @param stockDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<StockDTO> update(StockDTO stockDTO);

    /**
     * Partially updates a stock.
     *
     * @param stockDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<StockDTO> partialUpdate(StockDTO stockDTO);

    Flux<StockDTO> findAll(int page, int size);

    /**
     * Returns the number of stocks available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" stock.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<StockDTO> findOne(Long id);

    /**
     * Delete the "id" stock.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    Mono<Void> updateStock(Product product, int quantity);
}
