package com.my.fluffy.unicorn.main.server;

import com.my.fluffy.unicorn.main.server.rest.serviceQ;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.ext.ContextResolver;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class RESTServiceCreator implements ServletContextListener {
    private static URI BASE_URI = URI.create("http://localhost:8081/rest/");
    private  HttpServer server;
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        System.out.println("ServletContextListener destroyed");
        server.shutdown();
    }

    //Run this before web application is started
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        System.out.println("ServletContextListener started");

        server= GrizzlyHttpServerFactory.createHttpServer(
                BASE_URI, createApp(), false);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                server.shutdownNow();
            }
        }));
        try {
            server.start();

        System.out.println(String.format("\nGrizzly-HTTP-Server gestartet mit der URL: %s\n"
                        + "Stoppen des Grizzly-HTTP-Servers mit:      Strg+C\n",
                BASE_URI));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ResourceConfig createApp() {
        // create a resource config that scans for JAX-RS resources and providers
        return new ResourceConfig(serviceQ.class);
    }

}
