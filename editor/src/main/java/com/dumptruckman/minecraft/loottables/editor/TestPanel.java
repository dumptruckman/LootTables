package com.dumptruckman.minecraft.loottables.editor;

import javax.swing.*;
import java.awt.*;

public class TestPanel extends JPanel {

    public TestPanel() {
        setOpaque(true);
    }

    @Override
    public void paintAll(final Graphics g) {

    }

    @Override
    protected void paintComponent(Graphics g) {
        int x = 34;
        int y = 34;
        int w = getWidth() - 68;
        int h = getHeight() - 68;
        int arc = 30;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRoundRect(x, y, w, h, arc, arc);

        g2.setStroke(new BasicStroke(3f));
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(x, y, w, h, arc, arc);

        g2.dispose();
    }
}