package com.dumptruckman.minecraft.loottables.editor;

import org.bukkit.Material;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.PatternSyntaxException;

class MaterialTableModel extends DefaultTableModel {

    @Override
    public boolean isCellEditable(final int row, final int column) {
        return false;
    }

    private static class NoRowFilter extends RowFilter<MaterialTableModel, Integer> {
        @Override
        public boolean include(final Entry<? extends MaterialTableModel, ? extends Integer> entry) {
            return true;
        }
    }

    static final NoRowFilter NO_ROW_FILTER = new NoRowFilter();

    static class TextFieldRegexFilter extends RowFilter<MaterialTableModel, Integer> {

        private final JTextField textField;

        TextFieldRegexFilter(final JTextField textField) {
            this.textField = textField;
        }

        @Override
        public boolean include(final Entry<? extends MaterialTableModel, ? extends Integer> entry) {
            String text = textField.getText();
            if (text.length() == 0) {
                return true;
            } else {
                try {
                    if (RowFilter.regexFilter("(?i)" + text, 0).include(entry)) {
                        return true;
                    }
                    Material material = (Material) entry.getModel().getValueAt(entry.getIdentifier(), 0);
                    return material != null && String.valueOf(material.getId()).startsWith(text);
                } catch (PatternSyntaxException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }
    }
}
