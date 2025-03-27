package com.profac.app.repository;

import com.profac.app.domain.InvoiceProduct;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the InvoiceProduct entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InvoiceProductRepository extends ReactiveCrudRepository<InvoiceProduct, Long>, InvoiceProductRepositoryInternal {
    Flux<InvoiceProduct> findAllBy(Pageable pageable);

    @Query("""
    SELECT ip.*,
           i.*,
           p.*
    FROM invoice_product ip
    JOIN invoice i ON ip.invoice_id = i.id
    JOIN product p ON ip.product_id = p.id
    WHERE ip.invoice_id = :invoiceId
""")
    Flux<InvoiceProduct> findWithInvoiceAndProductByInvoiceId(@Param("invoiceId") Long invoiceId);


    @Query("SELECT * FROM invoice_product entity WHERE entity.invoice_id IS NULL")
    Flux<InvoiceProduct> findAllWhereInvoiceIsNull();

    @Query("SELECT * FROM invoice_product entity WHERE entity.product_id = :id")
    Flux<InvoiceProduct> findByProduct(Long id);

    @Query("SELECT * FROM invoice_product entity WHERE entity.product_id IS NULL")
    Flux<InvoiceProduct> findAllWhereProductIsNull();

    @Override
    <S extends InvoiceProduct> Mono<S> save(S entity);

    @Override
    Flux<InvoiceProduct> findAll();

    @Override
    Mono<InvoiceProduct> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface InvoiceProductRepositoryInternal {
    <S extends InvoiceProduct> Mono<S> save(S entity);

    Flux<InvoiceProduct> findAllBy(Pageable pageable);

    Flux<InvoiceProduct> findAll();

    Mono<InvoiceProduct> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<InvoiceProduct> findAllBy(Pageable pageable, Criteria criteria);
}
