package com.profac.app.repository;

import com.profac.app.domain.Company;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Company entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompanyRepository extends ReactiveCrudRepository<Company, Long>, CompanyRepositoryInternal {
    Flux<Company> findAllBy(Pageable pageable);

    @Override
    <S extends Company> Mono<S> save(S entity);

    @Override
    Flux<Company> findAll();

    @Override
    Mono<Company> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
    Mono<Company> findByPhoneNumber(String phoneNumber);
}

interface CompanyRepositoryInternal {
    <S extends Company> Mono<S> save(S entity);

    Flux<Company> findAllBy(Pageable pageable);

    Flux<Company> findAll();

    Mono<Company> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Company> findAllBy(Pageable pageable, Criteria criteria);
}
