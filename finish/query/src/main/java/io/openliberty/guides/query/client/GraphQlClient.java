// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.query.client;

import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;

import io.openliberty.guides.graphql.models.SystemInfo;
import io.openliberty.guides.graphql.models.SystemLoad;
import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;

// tag::clientApi[]
@GraphQLClientApi
// end::clientApi[]
public interface GraphQlClient {
    // tag::querySystemTag[]
    @Query("system")
    // end::querySystemTag[]
    // tag::systemInfo[]
    SystemInfo getSystemInfo(@Name("hostname") String host);
    // end::systemInfo[]

    // tag::querySystemLoadTag[]
    @Query("systemLoad")
    // end::querySystemLoadTag[]
    // tag::systemLoad[]
    SystemLoad[] getSystemLoad(@Name("hostnames") String[] hosts);
    // end::systemLoad[]

    // tag::mutationTag[]
    @Mutation("editNote")
    // end::mutationTag[]
    // tag::editNote[]
    boolean setNote(@Name("hostname") String host, @Name("note") String note);
    // end::editNote[]

}
