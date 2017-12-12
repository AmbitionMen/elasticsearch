/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.sql.expression.function;

import org.elasticsearch.xpack.sql.session.Configuration;

import java.util.Collection;

public interface FunctionRegistry {

    Function resolveFunction(UnresolvedFunction ur, Configuration settings);

    String concreteFunctionName(String alias);

    boolean functionExists(String name);

    Collection<FunctionDefinition> listFunctions();

    Collection<FunctionDefinition> listFunctions(String pattern);

}
