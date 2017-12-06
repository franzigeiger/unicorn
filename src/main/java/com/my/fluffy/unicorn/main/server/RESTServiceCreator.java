package com.my.fluffy.unicorn.main.server;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ext.ContextResolver;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class RESTServiceCreator {

    private static URI BASE_URI = URI.create("http://localhost:8080/rest/");
    public void create() throws IOException, InterruptedException {
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
                BASE_URI, createApp(), false);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                server.shutdownNow();
            }
        }));
        server.start();

        System.out.println(String.format("\nGrizzly-HTTP-Server gestartet mit der URL: %s\n"
                        + "Stoppen des Grizzly-HTTP-Servers mit:      Strg+C\n",
                BASE_URI));

        Thread.currentThread().join();
    }

    private static ResourceConfig createApp() {
        // create a resource config that scans for JAX-RS resources and providers
        return new ResourceConfig()
                .packages("com/my/fluffy/unicorn/server/rest");
    }

}
