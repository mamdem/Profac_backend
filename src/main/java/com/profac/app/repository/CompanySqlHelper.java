package com.profac.app.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class CompanySqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("valid_until", table, columnPrefix + "_valid_until"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("password", table, columnPrefix + "_password"));
        columns.add(Column.aliased("phone_number", table, columnPrefix + "_phone_number"));

        return columns;
    }
}
