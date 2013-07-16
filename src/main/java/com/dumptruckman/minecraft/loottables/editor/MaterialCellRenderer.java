package com.dumptruckman.minecraft.loottables.editor;

import org.bukkit.Material;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;

public class MaterialCellRenderer extends JLabel implements ListCellRenderer<Material> {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList list, Material value, int index, boolean isSelected, boolean cellHasFocus) {
        String[] words = value.toString().split("_");
        StringBuilder buffer = new StringBuilder();
        buffer.append(value.getId()).append(" - ");
        for (int i = 0; i < words.length; i++) {
            String word = words[i].toLowerCase();
            buffer.append(word.substring(0, 1).toUpperCase());
            buffer.append(word.substring(1));
            if (i != words.length - 1) {
                buffer.append(" ");
            }
        }
        setText(buffer.toString());
        if (isSelected) {
            setBackground(javax.swing.UIManager.getDefaults().getColor("List[Selected].textBackground"));
            setForeground(javax.swing.UIManager.getDefaults().getColor("List[Selected].textForeground"));
            setOpaque(true);
        } else {
            setBackground(javax.swing.UIManager.getDefaults().getColor("List.background"));
            setForeground(javax.swing.UIManager.getDefaults().getColor("List.foreground"));
            setOpaque(false);
        }
        setFont(list.getFont());
        return this;
    }
}
