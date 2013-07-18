package com.dumptruckman.minecraft.loottables.editor;

import com.dumptruckman.minecraft.loottables.LootSection;
import com.dumptruckman.minecraft.loottables.LootTable;

import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;

public class LootSectionTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(final JTree tree, final Object val, final boolean selected, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
        if (leaf)
            setIcon(getLeafIcon());
        else if (expanded)
            setIcon(getOpenIcon());
        else
            setIcon(getClosedIcon());

        if (val instanceof LootSectionTreeNode) {
            final LootSection loot = ((LootSectionTreeNode) val).getUserObject();
            //setText(loot.getSectionName());
            if (loot instanceof LootTable) {
                setIcon(getOpenIcon());
            }
        }
        setText(val.toString());
        this.selected = selected;
        this.hasFocus = hasFocus;
        setHorizontalAlignment(LEFT);
        setOpaque(false);
        setVerticalAlignment(CENTER);
        setEnabled(true);
        super.setFont(UIManager.getFont("Tree.font"));

        if (selected) {
            super.setBackground(getBackgroundSelectionColor());
            setForeground(getTextSelectionColor());

            if (hasFocus)
                setBorderSelectionColor(UIManager.getLookAndFeelDefaults().
                        getColor("Tree.selectionBorderColor"));
            else
                setBorderSelectionColor(null);
        } else {
            super.setBackground(getBackgroundNonSelectionColor());
            setForeground(getTextNonSelectionColor());
            setBorderSelectionColor(null);
        }

        return this;
    }
}
