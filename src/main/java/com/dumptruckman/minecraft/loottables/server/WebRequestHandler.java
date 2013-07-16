package com.dumptruckman.minecraft.loottables.server;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class WebRequestHandler extends AbstractHandler {

    private static class YamlFileFilter implements FilenameFilter {
        @Override
        public boolean accept(final File dir, final String name) {
            return name.endsWith(".yml");
        }
    }
    private static final YamlFileFilter YAML_FILE_FILTER = new YamlFileFilter();

    private final File lootTableFolder;

    WebRequestHandler(final File lootTableFolder) {
        this.lootTableFolder = lootTableFolder;
    }

    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        String path = "";//LootTablesApplet.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        response.getWriter().write("<script src=\"http://java.com/js/deployJava.js\"></script>\n" +
                "<script>\n" +
                "    var attributes = {codebase:'.',\n" +
                "                      code:'com.dumptruckman.minecraft.loottables.server.LootTablesApplet.class',\n" +
                "                      archive:'." + path.substring(0) + "',\n" +
                "                      width:710, height:540} ;\n" +
                "    var parameters = {fontSize:16} ;\n" +
                "    var version = '1.6' ;\n" +
                "    deployJava.runApplet(attributes, parameters, version);\n" +
                "</script>");
    }
}
