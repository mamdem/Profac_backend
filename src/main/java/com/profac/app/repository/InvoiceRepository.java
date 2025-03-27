package com.profac.app.repository;

import com.profac.app.domain.Invoice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Invoice entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InvoiceRepository extends ReactiveCrudRepository<Invoice, Long>, InvoiceRepositoryInternal {
    Flux<Invoice> findAllBy(Pageable pageable);

    @Query("SELECT * FROM invoice entity WHERE entity.company_id = :id  LIMIT :limit OFFSET :offset")
    Flux<Invoice> findByCompany(Long id, int limit, int offset);
    @Query("SELECT COUNT(*) FROM invoice WHERE company_id = :companyId")
    Mono<Long> countByCompanyId(Long companyId);
    Flux<Invoice> findByCompanyId(Long id, Pageable pageable);

    @Query("SELECT * FROM invoice entity WHERE entity.company_id IS NULL")
    Flux<Invoice> findAllWhereCompanyIsNull();

    @Override
    <S extends Invoice> Mono<S> save(S entity);

    @Override
    Flux<Invoice> findAll();

    @Override
    Mono<Invoice> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);

    Mono<Invoice> findByInvoiceNumber(Long invoiceNumber);

}

interface InvoiceRepositoryInternal {
    <S extends Invoice> Mono<S> save(S entity);

    Flux<Invoice> findAllBy(Pageable pageable);

    Flux<Invoice> findAll();

    Mono<Invoice> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Invoice> findAllBy(Pageable pageable, Criteria criteria);
}
