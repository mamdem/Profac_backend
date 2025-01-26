package com.profac.app.repository;

import com.profac.app.domain.Invoice;
import com.profac.app.repository.rowmapper.CompanyRowMapper;
import com.profac.app.repository.rowmapper.InvoiceRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Invoice entity.
 */
@SuppressWarnings("unused")
class InvoiceRepositoryInternalImpl extends SimpleR2dbcRepository<Invoice, Long> implements InvoiceRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CompanyRowMapper companyMapper;
    private final InvoiceRowMapper invoiceMapper;

    private static final Table entityTable = Table.aliased("invoice", EntityManager.ENTITY_ALIAS);
    private static final Table companyTable = Table.aliased("company", "company");

    public InvoiceRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CompanyRowMapper companyMapper,
        InvoiceRowMapper invoiceMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Invoice.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.companyMapper = companyMapper;
        this.invoiceMapper = invoiceMapper;
    }

    @Override
    public Flux<Invoice> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Invoice> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = InvoiceSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CompanySqlHelper.getColumns(companyTable, "company"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(companyTable)
            .on(Column.create("company_id", entityTable))
            .equals(Column.create("id", companyTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Invoice.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Invoice> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Invoice> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    public Mono<Invoice> findOneWithEagerRelationships(Long id) {
        return null;
    }

    public Flux<Invoice> findAllWithEagerRelationships() {
        return null;
    }

    public Flux<Invoice> findAllWithEagerRelationships(Pageable page) {
        return null;
    }

    private Invoice process(Row row, RowMetadata metadata) {
        Invoice entity = invoiceMapper.apply(row, "e");
        entity.setCompany(companyMapper.apply(row, "company"));
        return entity;
    }

    @Override
    public <S extends Invoice> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
