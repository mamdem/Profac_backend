package com.profac.app.repository.rowmapper;

import com.profac.app.domain.AppUser;
import com.profac.app.domain.enumeration.AppUserStatus;
import com.profac.app.domain.enumeration.UserType;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link AppUser}, with proper type conversions.
 */
@Service
public class AppUserRowMapper implements BiFunction<Row, String, AppUser> {

    private final ColumnConverter converter;

    public AppUserRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link AppUser} stored in the database.
     */
    @Override
    public AppUser apply(Row row, String prefix) {
        AppUser entity = new AppUser();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setPassword(converter.fromRow(row, prefix + "_password", String.class));
        entity.setPhoneNumber(converter.fromRow(row, prefix + "_phone_number", String.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        entity.setUserType(converter.fromRow(row, prefix + "_user_type", UserType.class));
        entity.setAvatarId(converter.fromRow(row, prefix + "_avatar_id", Long.class));
        entity.setCompanyId(converter.fromRow(row, prefix + "_company_id", Long.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", AppUserStatus.class));
        return entity;
    }
}
