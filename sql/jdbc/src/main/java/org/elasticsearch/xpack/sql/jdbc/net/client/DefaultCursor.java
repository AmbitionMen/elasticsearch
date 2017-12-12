/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.sql.jdbc.net.client;

import org.elasticsearch.xpack.sql.jdbc.net.protocol.ColumnInfo;
import org.elasticsearch.xpack.sql.jdbc.net.protocol.Page;

import java.sql.SQLException;
import java.util.List;

class DefaultCursor implements Cursor {

    private final JdbcHttpClient client;
    private final RequestMeta meta;

    private final Page page;
    private int row = -1;
    private String cursor;

    DefaultCursor(JdbcHttpClient client, String cursor, Page page, RequestMeta meta) {
        this.client = client;
        this.meta = meta;
        this.cursor = cursor;
        this.page = page;
    }

    @Override
    public List<ColumnInfo> columns() {
        return page.columnInfo();
    }

    @Override
    public boolean next() throws SQLException {
        if (row < page.rows() - 1) {
            row++;
            return true;
        }
        else {
            if (cursor.isEmpty() == false) {
                cursor = client.nextPage(cursor, page, meta);
                row = -1;
                return next();
            }
            return false;
        }
    }

    @Override
    public Object column(int column) {
        return page.entry(row, column);
    }

    @Override
    public int batchSize() {
        return page.rows();
    }

    @Override
    public void close() throws SQLException {
        if (cursor.isEmpty() == false) {
            client.queryClose(cursor);
        }
    }
}
