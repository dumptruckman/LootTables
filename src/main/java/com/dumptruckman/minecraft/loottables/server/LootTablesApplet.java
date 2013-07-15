package com.dumptruckman.minecraft.loottables.server;

import java.applet.Applet;
import java.awt.*;

public class LootTablesApplet extends Applet {

    @Override
    public void init() {
        add(new Label("Distance of trip in light years"));
    }

    @Override
    public void start() {
        add(new Label("Test"));
    }
}
