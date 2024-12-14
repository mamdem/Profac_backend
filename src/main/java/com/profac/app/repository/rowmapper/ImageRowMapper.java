package com.profac.app.repository.rowmapper;

import com.profac.app.domain.Image;
import com.profac.app.domain.enumeration.ImageStatus;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Image}, with proper type conversions.
 */
@Service
public class ImageRowMapper implements BiFunction<Row, String, Image> {

    private final ColumnConverter converter;

    public ImageRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Image} stored in the database.
     */
    @Override
    public Image apply(Row row, String prefix) {
        Image entity = new Image();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUrl(converter.fromRow(row, prefix + "_url", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", ImageStatus.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        return entity;
    }
}
