package org.vada;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

import static org.vada.Settings.BASE_URI;

public class Main {
    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig().packages("org.vada.services.reports");

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws Exception {
//        final HttpServer server = startServer();
        CamundaClient.run();
//        System.out.println(String.format("Jersey app started at %s%s", BASE_URI, "message"));
//        System.out.println("Hit enter to stop it...");
//        System.in.read();
//        server.shutdownNow();
    }
}