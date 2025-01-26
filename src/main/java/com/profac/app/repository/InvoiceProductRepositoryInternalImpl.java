package com.profac.app.repository;

import com.profac.app.domain.InvoiceProduct;
import com.profac.app.repository.rowmapper.InvoiceProductRowMapper;
import com.profac.app.repository.rowmapper.InvoiceRowMapper;
import com.profac.app.repository.rowmapper.ProductRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the InvoiceProduct entity.
 */
@SuppressWarnings("unused")
class InvoiceProductRepositoryInternalImpl extends SimpleR2dbcRepository<InvoiceProduct, Long> implements InvoiceProductRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final InvoiceRowMapper invoiceMapper;
    private final ProductRowMapper productMapper;
    private final InvoiceProductRowMapper invoiceproductMapper;

    private static final Table entityTable = Table.aliased("invoice_product", EntityManager.ENTITY_ALIAS);
    private static final Table invoiceTable = Table.aliased("invoice", "invoice");
    private static final Table productTable = Table.aliased("product", "product");

    public InvoiceProductRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        InvoiceRowMapper invoiceMapper,
        ProductRowMapper productMapper,
        InvoiceProductRowMapper invoiceproductMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(InvoiceProduct.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.invoiceMapper = invoiceMapper;
        this.productMapper = productMapper;
        this.invoiceproductMapper = invoiceproductMapper;
    }

    @Override
    public Flux<InvoiceProduct> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<InvoiceProduct> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = InvoiceProductSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(InvoiceSqlHelper.getColumns(invoiceTable, "invoice"));
        columns.addAll(ProductSqlHelper.getColumns(productTable, "product"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(invoiceTable)
            .on(Column.create("invoice_id", entityTable))
            .equals(Column.create("id", invoiceTable))
            .leftOuterJoin(productTable)
            .on(Column.create("product_id", entityTable))
            .equals(Column.create("id", productTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, InvoiceProduct.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<InvoiceProduct> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<InvoiceProduct> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private InvoiceProduct process(Row row, RowMetadata metadata) {
        InvoiceProduct entity = invoiceproductMapper.apply(row, "e");
        entity.setInvoice(invoiceMapper.apply(row, "invoice"));
        entity.setProduct(productMapper.apply(row, "product"));
        return entity;
    }

    @Override
    public <S extends InvoiceProduct> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
