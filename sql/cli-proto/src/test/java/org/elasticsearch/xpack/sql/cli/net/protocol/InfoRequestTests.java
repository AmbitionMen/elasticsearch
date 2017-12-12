/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.sql.cli.net.protocol;

import org.elasticsearch.test.ESTestCase;

import java.io.IOException;

import static org.elasticsearch.xpack.sql.cli.net.protocol.CliRoundTripTestUtils.assertRoundTripCurrentVersion;

public class InfoRequestTests extends ESTestCase {
    static InfoRequest randomInfoRequest() {
        return new InfoRequest(randomAlphaOfLength(5), randomAlphaOfLength(5), randomAlphaOfLength(5),
                randomAlphaOfLength(5), randomAlphaOfLength(5));
    }

    public void testRoundTrip() throws IOException {
        assertRoundTripCurrentVersion(randomInfoRequest());
    }

    public void testToString() {
        assertEquals("InfoRequest<jvm=[version=[1.8.0_131] vendor=[testvendor] classPath=[testcp]] os=[name=[Mac OS X] version=[10.12.5]]>",
                new InfoRequest("1.8.0_131", "testvendor", "testcp", "Mac OS X", "10.12.5").toString());
    }
}
