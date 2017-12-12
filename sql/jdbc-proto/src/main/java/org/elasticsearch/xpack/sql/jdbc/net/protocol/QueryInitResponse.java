/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.sql.jdbc.net.protocol;

import org.elasticsearch.xpack.sql.jdbc.net.protocol.Proto.RequestType;
import org.elasticsearch.xpack.sql.jdbc.net.protocol.Proto.ResponseType;
import org.elasticsearch.xpack.sql.protocol.shared.AbstractQueryResponse;
import org.elasticsearch.xpack.sql.protocol.shared.Request;
import org.elasticsearch.xpack.sql.protocol.shared.SqlDataInput;
import org.elasticsearch.xpack.sql.protocol.shared.SqlDataOutput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.unmodifiableList;

public class QueryInitResponse extends AbstractQueryResponse {
    public final List<ColumnInfo> columns;
    public final Payload data;

    public QueryInitResponse(long tookNanos, String cursor, List<ColumnInfo> columns, Payload data) {
        super(tookNanos, cursor);
        this.columns = columns;
        this.data = data;
    }

    QueryInitResponse(Request request, SqlDataInput in) throws IOException {
        super(request, in);
        int size = in.readInt();
        List<ColumnInfo> columns = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            columns.add(new ColumnInfo(in));
        }
        this.columns = unmodifiableList(columns);
        // TODO -  Page is a client class, it shouldn't leak here
        Page data = new Page(columns);
        data.readFrom(in);
        this.data = data;
    }

    @Override
    public void writeTo(SqlDataOutput out) throws IOException {
        super.writeTo(out);
        out.writeInt(columns.size());
        for (ColumnInfo c : columns) {
            c.writeTo(out);
        }
        data.writeTo(out);
    }

    @Override
    protected String toStringBody() {
        return super.toStringBody()
                + " columns=" + columns
                + " data=[\n" + data + "]";
    }

    @Override
    public RequestType requestType() {
        return RequestType.QUERY_INIT;
    }

    @Override
    public ResponseType responseType() {
        return ResponseType.QUERY_INIT;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), columns, data);
    }
    

    @Override
    public boolean equals(Object obj) {
        if (false == super.equals(obj)) {
            return false;
        }
        QueryInitResponse other = (QueryInitResponse) obj;
        return Objects.equals(columns, other.columns)
                && Objects.equals(data, other.data);
    }
}