package com.dumptruckman.minecraft.loottables.editor;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.ToolTipManager;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;
import java.util.regex.PatternSyntaxException;

class EnchantTableModel extends DefaultTableModel {

    @Override
    public boolean isCellEditable(final int row, final int column) {
        return false;
    }

    static class TextFieldRegexFilter extends RowFilter<EnchantTableModel, Integer> {

        private final JTextField textField;
        private final String initialToolTip;

        TextFieldRegexFilter(final JTextField textField) {
            this.textField = textField;
            this.initialToolTip = textField.getToolTipText();
        }

        @Override
        public boolean include(final Entry<? extends EnchantTableModel, ? extends Integer> entry) {
            String text = textField.getText();
            if (text.length() == 0) {
                return true;
            } else {
                try {
                    if (RowFilter.regexFilter("(?i)" + text, 0).include(entry)) {
                        textField.setToolTipText(initialToolTip);
                        return true;
                    }
                    Enchantment enchant = (Enchantment) entry.getModel().getValueAt(entry.getIdentifier(), 0);
                    if (enchant != null && String.valueOf(enchant.getId()).startsWith(text)) {
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
