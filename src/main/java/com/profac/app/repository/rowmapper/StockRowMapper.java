package com.profac.app.repository.rowmapper;

import com.profac.app.domain.Stock;
import com.profac.app.domain.enumeration.StockStatus;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Stock}, with proper type conversions.
 */
@Service
public class StockRowMapper implements BiFunction<Row, String, Stock> {

    private final ColumnConverter converter;

    public StockRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Stock} stored in the database.
     */
    @Override
    public Stock apply(Row row, String prefix) {
        Stock entity = new Stock();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTotalAmount(converter.fromRow(row, prefix + "_total_amount", BigDecimal.class));
        entity.setTotalAmountSold(converter.fromRow(row, prefix + "_total_amount_sold", BigDecimal.class));
        entity.setInitialQuantity(converter.fromRow(row, prefix + "_initial_quantity", Integer.class));
        entity.setRemainingQuantity(converter.fromRow(row, prefix + "_remaining_quantity", Integer.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", StockStatus.class));
        entity.setCompanyId(converter.fromRow(row, prefix + "_company_id", Long.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        return entity;
    }
}
