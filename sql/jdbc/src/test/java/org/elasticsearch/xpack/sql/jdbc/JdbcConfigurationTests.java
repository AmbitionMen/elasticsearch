/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.sql.jdbc;

import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.xpack.sql.jdbc.jdbc.JdbcConfiguration;

import java.sql.SQLException;

import static org.hamcrest.Matchers.is;

public class JdbcConfigurationTests extends ESTestCase {

    private JdbcConfiguration ci(String url) throws SQLException {
        return JdbcConfiguration.create(url, null);
    }

    public void testJustThePrefix() throws Exception {
       Exception e = expectThrows(JdbcSQLException.class, () -> ci("jdbc:es:"));
       assertEquals("Expected [jdbc:es://] url, received [jdbc:es:]", e.getMessage());
    }

    public void testJustTheHost() throws Exception {
        assertThat(ci("jdbc:es://localhost").baseUri().toString(), is("http://localhost:9200/"));
    }

    public void testHostAndPort() throws Exception {
        assertThat(ci("jdbc:es://localhost:1234").baseUri().toString(), is("http://localhost:1234/"));
    }

    public void testTrailingSlashForHost() throws Exception {
        assertThat(ci("jdbc:es://localhost:1234/").baseUri().toString(), is("http://localhost:1234/"));
    }

    public void testMultiPathSuffix() throws Exception {
        assertThat(ci("jdbc:es://a:1/foo/bar/tar").baseUri().toString(), is("http://a:1/foo/bar/tar"));
    }

    public void testV6Localhost() throws Exception {
        assertThat(ci("jdbc:es://[::1]:54161/foo/bar").baseUri().toString(), is("http://[::1]:54161/foo/bar"));
    }

    public void testDebug() throws Exception {
        JdbcConfiguration ci = ci("jdbc:es://a:1/?debug=true");
        assertThat(ci.baseUri().toString(), is("http://a:1/"));
        assertThat(ci.debug(), is(true));
        assertThat(ci.debugOut(), is("err"));
    }

    public void testDebugOut() throws Exception {
        JdbcConfiguration ci = ci("jdbc:es://a:1/?debug=true&debug.output=jdbc.out");
        assertThat(ci.baseUri().toString(), is("http://a:1/"));
        assertThat(ci.debug(), is(true));
        assertThat(ci.debugOut(), is("jdbc.out"));
    }

    public void testTypeInParam() throws Exception {
        Exception e = expectThrows(JdbcSQLException.class, () -> ci("jdbc:es://a:1/foo/bar/tar?debug=true&debug.out=jdbc.out"));
        assertEquals("Unknown parameter [debug.out] ; did you mean [debug.output]", e.getMessage());
    }

    public void testDebugOutWithSuffix() throws Exception {
        JdbcConfiguration ci = ci("jdbc:es://a:1/foo/bar/tar?debug=true&debug.output=jdbc.out");
        assertThat(ci.baseUri().toString(), is("http://a:1/foo/bar/tar"));
        assertThat(ci.debug(), is(true));
        assertThat(ci.debugOut(), is("jdbc.out"));
    }

    public void testHttpWithSSLEnabled() throws Exception {
        JdbcConfiguration ci = ci("jdbc:es://test?ssl=true");
        assertThat(ci.baseUri().toString(), is("https://test:9200/"));
    }

    public void testHttpWithSSLDisabled() throws Exception {
        JdbcConfiguration ci = ci("jdbc:es://test?ssl=false");
        assertThat(ci.baseUri().toString(), is("http://test:9200/"));
    }

}
