package com.dumptruckman.minecraft.loottables.editor;

import org.bukkit.Material;

import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.ToolTipManager;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;
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
        private final String initialToolTip;

        TextFieldRegexFilter(final JTextField textField) {
            this.textField = textField;
            this.initialToolTip = textField.getToolTipText();
        }

        @Override
        public boolean include(final Entry<? extends MaterialTableModel, ? extends Integer> entry) {
            String text = textField.getText();
            if (text.length() == 0) {
                return true;
            } else {
                try {
                    if (RowFilter.regexFilter("(?i)" + text, 0).include(entry)) {
                        textField.setToolTipText(initialToolTip);
                        return true;
                    }
                    Material material = (Material) entry.getModel().getValueAt(entry.getIdentifier(), 0);
                    if (material != null && String.valueOf(material.getId()).startsWith(text)) {
                        textField.setToolTipText(initialToolTip);
                        return true;
                    }
                } catch (PatternSyntaxException e) {
                    textField.setToolTipText(e.getMessage());
                    MouseEvent phantom = new MouseEvent(textField, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 10, 10, 0, false);
                    ToolTipManager.sharedInstance().mouseMoved(phantom);
                }
                textField.setToolTipText(initialToolTip);
                return false;
            }
        }
    }
}
