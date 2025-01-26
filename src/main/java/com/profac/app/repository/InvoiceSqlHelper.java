package com.profac.app.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class InvoiceSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("invoice_number", table, columnPrefix + "_invoice_number"));
        columns.add(Column.aliased("customer", table, columnPrefix + "_customer"));
        columns.add(Column.aliased("invoice_date", table, columnPrefix + "_invoice_date"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("company_id", table, columnPrefix + "_company_id"));
        return columns;
    }
}
