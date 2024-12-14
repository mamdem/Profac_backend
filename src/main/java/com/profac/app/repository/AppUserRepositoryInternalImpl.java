package com.profac.app.repository;

import com.profac.app.domain.AppUser;
import com.profac.app.repository.rowmapper.AppUserRowMapper;
import com.profac.app.repository.rowmapper.CompanyRowMapper;
import com.profac.app.repository.rowmapper.ImageRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the AppUser entity.
 */
@SuppressWarnings("unused")
class AppUserRepositoryInternalImpl extends SimpleR2dbcRepository<AppUser, Long> implements AppUserRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ImageRowMapper imageMapper;
    private final CompanyRowMapper companyMapper;
    private final AppUserRowMapper appuserMapper;

    private static final Table entityTable = Table.aliased("app_user", EntityManager.ENTITY_ALIAS);
    private static final Table avatarTable = Table.aliased("image", "avatar");
    private static final Table companyTable = Table.aliased("company", "company");

    public AppUserRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ImageRowMapper imageMapper,
        CompanyRowMapper companyMapper,
        AppUserRowMapper appuserMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(AppUser.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.imageMapper = imageMapper;
        this.companyMapper = companyMapper;
        this.appuserMapper = appuserMapper;
    }

    @Override
    public Flux<AppUser> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<AppUser> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = AppUserSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ImageSqlHelper.getColumns(avatarTable, "avatar"));
        columns.addAll(CompanySqlHelper.getColumns(companyTable, "company"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(avatarTable)
            .on(Column.create("avatar_id", entityTable))
            .equals(Column.create("id", avatarTable))
            .leftOuterJoin(companyTable)
            .on(Column.create("company_id", entityTable))
            .equals(Column.create("id", companyTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, AppUser.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<AppUser> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<AppUser> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private AppUser process(Row row, RowMetadata metadata) {
        AppUser entity = appuserMapper.apply(row, "e");
        entity.setAvatar(imageMapper.apply(row, "avatar"));
        entity.setCompany(companyMapper.apply(row, "company"));
        return entity;
    }

    @Override
    public <S extends AppUser> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
