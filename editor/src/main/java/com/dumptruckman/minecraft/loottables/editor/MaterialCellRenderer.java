package com.dumptruckman.minecraft.loottables.editor;

import org.bukkit.Material;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

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
            setBackground(javax.swing.UIManager.getDefaults().getColor("List[Selected].textBackground"));
            setForeground(javax.swing.UIManager.getDefaults().getColor("List[Selected].textForeground"));
            setOpaque(true);
        } else {
            setBackground(javax.swing.UIManager.getDefaults().getColor("List.background"));
            setForeground(javax.swing.UIManager.getDefaults().getColor("List.foreground"));
            setOpaque(false);
        }
        setFont(table.getFont());
        return this;
    }
}
