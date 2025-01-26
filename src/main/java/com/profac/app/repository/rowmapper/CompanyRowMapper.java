package com.profac.app.repository.rowmapper;

import com.profac.app.domain.Company;
import com.profac.app.domain.enumeration.CompanyStatus;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Company}, with proper type conversions.
 */
@Service
public class CompanyRowMapper implements BiFunction<Row, String, Company> {

    private final ColumnConverter converter;

    public CompanyRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Company} stored in the database.
     */
    @Override
    public Company apply(Row row, String prefix) {
        Company entity = new Company();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setValidUntil(converter.fromRow(row, prefix + "_valid_until", Instant.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", CompanyStatus.class));
        entity.setPassword(converter.fromRow(row, prefix + "_password", String.class));
        entity.setPhoneNumber(converter.fromRow(row, prefix + "_phone_number", String.class));
        return entity;
    }
}
