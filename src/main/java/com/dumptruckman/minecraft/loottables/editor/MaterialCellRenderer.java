package com.dumptruckman.minecraft.loottables.editor;

import org.bukkit.Material;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.regex.Pattern;

class MaterialCellRenderer extends JLabel implements TableCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        if (value instanceof Material) {
            final Material mat = (Material) value;
            String[] words = mat.toString().split("_");
            StringBuilder buffer = new StringBuilder();
            buffer.append(mat.getId()).append(" - ");
            for (int i = 0; i < words.length; i++) {
                String word = words[i].toLowerCase();
                buffer.append(word.substring(0, 1).toUpperCase());
                buffer.append(word.substring(1));
                if (i != words.length - 1) {
                    buffer.append(" ");
                }
            }
            return showItem(table, buffer.toString(), isSelected);
        }
        return this;
    }

    private Component showItem(final JTable table, final String name, boolean isSelected) {
        setText(name);
        if (isSelected) {
            setBackground(javax.swing.UIManager.getDefaults().getColor("Table[Selected].textBackground"));
            setForeground(javax.swing.UIManager.getDefaults().getColor("Table[Selected].textForeground"));
            setOpaque(true);
        } else {
            setBackground(javax.swing.UIManager.getDefaults().getColor("Table.background"));
            setForeground(javax.swing.UIManager.getDefaults().getColor("Table.foreground"));
            setOpaque(false);
        }
        setFont(table.getFont());
        return this;
    }
}
