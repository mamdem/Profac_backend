package com.profac.app.repository;

import com.profac.app.domain.Stock;
import com.profac.app.domain.enumeration.StockStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Stock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StockRepository extends ReactiveCrudRepository<Stock, Long>, StockRepositoryInternal {
    Flux<Stock> findAllBy(Pageable pageable);

    @Query("SELECT * FROM stock entity WHERE entity.product_id = :id")
    Flux<Stock> findByProduct(Long id);

    @Query("SELECT * FROM stock entity WHERE entity.product_id IS NULL")
    Flux<Stock> findAllWhereProductIsNull();
    @Query("SELECT s.*, c.*, p.* FROM Stock s " +
        "JOIN Company c ON s.company_id = c.id " +
        "JOIN Product p ON s.product_id = p.id " +
        "WHERE c.id = :companyId LIMIT :limit OFFSET :offset")
    Flux<Stock> findAllByCompanyId(Long companyId, int limit, int offset);
    @Query("SELECT s.id AS stockId, s.quantity, c.name AS companyName, p.name AS productName " +
        "FROM Stock s " +
        "JOIN Company c ON s.company_id = c.id " +
        "JOIN Product p ON s.product_id = p.id " +
        "WHERE c.id = :companyId AND s.id = :id")
    Mono<Stock> findAllByCompanyIdAndId(Long companyId, Long id);

    Flux<Stock> findAllByCompanyId(Long id);

    @Override
    <S extends Stock> Mono<S> save(S entity);

    @Override
    Flux<Stock> findAll();

    @Override
    Mono<Stock> findById(Long id);
    Mono<Stock> findByProductIdAndStatus(Long productId, StockStatus status);

    @Override
    Mono<Void> deleteById(Long id);
}

interface StockRepositoryInternal {
    <S extends Stock> Mono<S> save(S entity);

    Flux<Stock> findAllBy(Pageable pageable);

    Flux<Stock> findAll();

    Mono<Stock> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Stock> findAllBy(Pageable pageable, Criteria criteria);
}
