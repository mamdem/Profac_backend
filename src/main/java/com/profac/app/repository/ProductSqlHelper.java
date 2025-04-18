package com.profac.app.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ProductSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("product_number", table, columnPrefix + "_product_number"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("amount", table, columnPrefix + "_amount"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));

        columns.add(Column.aliased("category_id", table, columnPrefix + "_category_id"));
        return columns;
    }
}
