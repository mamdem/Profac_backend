package com.profac.app.repository.rowmapper;

import com.profac.app.domain.InvoiceProduct;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link InvoiceProduct}, with proper type conversions.
 */
@Service
public class InvoiceProductRowMapper implements BiFunction<Row, String, InvoiceProduct> {

    private final ColumnConverter converter;

    public InvoiceProductRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link InvoiceProduct} stored in the database.
     */
    @Override
    public InvoiceProduct apply(Row row, String prefix) {
        InvoiceProduct entity = new InvoiceProduct();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setQuantity(converter.fromRow(row, prefix + "_quantity", Integer.class));
        entity.setTotalAmount(converter.fromRow(row, prefix + "_total_amount", BigDecimal.class));
        entity.setInvoiceId(converter.fromRow(row, prefix + "_invoice_id", Long.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        return entity;
    }
}
