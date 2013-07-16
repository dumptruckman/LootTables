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
        //for (File file : lootTableFolder.listFiles(YAML_FILE_FILTER)) {
        //    response.getWriter().println(file.getName().substring(0, file.getName().length() - 4));
        //}
        System.out.println(LootTablesApplet.class.getProtectionDomain().getCodeSource().getLocation().getPath());
/*
        response.getWriter().write("<object type=\"application/x-java-applet\" height=\"300\" width=\"550\">"
                //+ "<param name=\"classid\" value=\"com.dumptruckman.minecraft.loottables.server.LootTablesApplet\" />"
                + "<param name=\"code\" value=\"com.dumptruckman.minecraft.loottables.server.LootTablesApplet.class\" />"
                + "<PARAM name=\"archive\" value=\" + LootTablesApplet.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "\">"
                + "Applet failed to run.  No Java plug-in was found.""
                + "</object>");
        */
        String path = LootTablesApplet.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        response.getWriter().write("<script src=\"http://java.com/js/deployJava.js\"></script>\n" +
                "<script>\n" +
                "    var attributes = {codebase:'.',\n" +
                "                      code:'com.dumptruckman.minecraft.loottables.server.LootTablesApplet.class',\n" +
                //"                      archive:'." + path.substring(path.substring(0, path.length() - 1).lastIndexOf("/")) + "',\n" +
                "                      archive:'." + path.substring(0) + "',\n" +
                "                      width:710, height:540} ;\n" +
                "    var parameters = {fontSize:16} ;\n" +
                "    var version = '1.6' ;\n" +
                "    deployJava.runApplet(attributes, parameters, version);\n" +
                "</script>");


        //response.getWriter().println("<applet code=\"com.dumptruckman.minecraft.loottables.server.LootTablesApplet\" archive=\"" + LootTablesApplet.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "\" width=\"800\" height=\"600\"></applet>");
    }
}