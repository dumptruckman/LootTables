package com.dumptruckman.minecraft.loottables.server;

import org.eclipse.jetty.server.HttpConfiguration.ConnectionFactory;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebServer extends Thread {

    private final Logger logger;
    private final Server server;

    public WebServer(@NotNull final Logger logger, final int port, @NotNull final File lootTableFolder) {
        this.logger = logger;
        server = new Server();
        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory());
        connector.setPort(port);
        connector.setIdleTimeout(30000);
        server.addConnector(connector);

        ContextHandler contextHandler = new ContextHandler();
        contextHandler.setContextPath("/");
        //contextHandler.setContextPath(".");
        contextHandler.setResourceBase(new File(LootTablesApplet.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent());
        contextHandler.setClassLoader(Thread.currentThread().getContextClassLoader());
        contextHandler.setHandler(new WebRequestHandler(lootTableFolder));
        server.setHandler(contextHandler);
    }

    public void run() {
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not start LootTables web server!", e);
        }
    }

    public void stopServer() {
        try {
            server.stop();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not stop LootTables web server!", e);
        }
    }
}
