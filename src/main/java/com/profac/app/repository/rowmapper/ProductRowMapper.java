package com.profac.app.repository.rowmapper;

import com.profac.app.domain.Product;
import com.profac.app.domain.enumeration.ProductStatus;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Product}, with proper type conversions.
 */
@Service
public class ProductRowMapper implements BiFunction<Row, String, Product> {

    private final ColumnConverter converter;

    public ProductRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Product} stored in the database.
     */
    @Override
    public Product apply(Row row, String prefix) {
        Product entity = new Product();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setProductNumber(converter.fromRow(row, prefix + "_product_number", Integer.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setAmount(converter.fromRow(row, prefix + "_amount", BigDecimal.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", ProductStatus.class));
        entity.setCategoryId(converter.fromRow(row, prefix + "_category_id", Long.class));
        return entity;
    }
}
