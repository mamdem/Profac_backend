package com.profac.app.repository.rowmapper;

import com.profac.app.domain.Invoice;
import com.profac.app.domain.enumeration.InvoiceStatus;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Invoice}, with proper type conversions.
 */
@Service
public class InvoiceRowMapper implements BiFunction<Row, String, Invoice> {

    private final ColumnConverter converter;

    public InvoiceRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Invoice} stored in the database.
     */
    @Override
    public Invoice apply(Row row, String prefix) {
        Invoice entity = new Invoice();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setInvoiceNumber(converter.fromRow(row, prefix + "_invoice_number", Long.class));
        entity.setCustomer(converter.fromRow(row, prefix + "_customer", String.class));
        entity.setInvoiceDate(converter.fromRow(row, prefix + "_invoice_date", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", InvoiceStatus.class));
        entity.setCompanyId(converter.fromRow(row, prefix + "_company_id", Long.class));
        return entity;
    }
}
