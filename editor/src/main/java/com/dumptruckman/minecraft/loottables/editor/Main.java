package com.dumptruckman.minecraft.loottables.editor;

import org.bukkit.enchantments.Enchantment;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.lang.reflect.Field;

public class Main {

    public static void main(String[] args) {
        Enchantments.initializeEnchantments();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        EditorFrame gui = new EditorFrame();
        gui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gui.setVisible(true);
    }
}
