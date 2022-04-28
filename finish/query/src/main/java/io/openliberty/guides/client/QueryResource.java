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
package io.openliberty.guides.query;

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.openliberty.guides.graphql.models.SystemInfo;
import io.openliberty.guides.graphql.models.SystemLoad;
import io.openliberty.guides.graphql.models.NoteInfo;
import io.openliberty.guides.query.client.GraphQlClient;
import io.smallrye.graphql.client.typesafe.api.GraphQlClientBuilder;

@ApplicationScoped
@Path("query")
public class QueryResource {

    // tag::clientBuilder[]
    private GraphQlClient gc = GraphQlClientBuilder.newBuilder()
                                                   .build(GraphQlClient.class);
    // end::clientBuilder[]

    @GET
    @Path("system/{hostname}")
    @Produces(MediaType.APPLICATION_JSON)
    public SystemInfo querySystem(@PathParam("hostname") String hostname) {
        // tag::clientUsed1[]
        return gc.system(hostname);
        // end::clientUsed1[]
    }

    @GET
    @Path("systemLoad/{hostnames}")
    @Produces(MediaType.APPLICATION_JSON)
    public SystemLoad[] querySystemLoad(@PathParam("hostnames") String hostnames) {
        String[] hostnameArray = hostnames.split(",");
        // tag::clientUsed2[]
        return gc.systemLoad(hostnameArray);
        // end::clientUsed2[]
    }

    @POST
    @Path("mutation/system/note")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editNote(NoteInfo text) {
        // tag::clientUsed3[]
        if (gc.editNote(text.getHostname(), text.getText())) {
        // end::clientUsed3[]
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }
}
