package com.profac.app.repository;

import com.profac.app.domain.Image;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Image entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ImageRepository extends ReactiveCrudRepository<Image, Long>, ImageRepositoryInternal {
    Flux<Image> findAllBy(Pageable pageable);

    @Query("SELECT * FROM image entity WHERE entity.id not in (select app_user_id from app_user)")
    Flux<Image> findAllWhereAppUserIsNull();

    @Query("SELECT * FROM image entity WHERE entity.product_id = :id")
    Flux<Image> findByProduct(Long id);

    @Query("SELECT * FROM image entity WHERE entity.product_id IS NULL")
    Flux<Image> findAllWhereProductIsNull();

    @Override
    <S extends Image> Mono<S> save(S entity);

    @Override
    Flux<Image> findAll();

    @Override
    Mono<Image> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ImageRepositoryInternal {
    <S extends Image> Mono<S> save(S entity);

    Flux<Image> findAllBy(Pageable pageable);

    Flux<Image> findAll();

    Mono<Image> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Image> findAllBy(Pageable pageable, Criteria criteria);
}
