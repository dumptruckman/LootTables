package com.dumptruckman.minecraft.loottables.editor;

import com.dumptruckman.minecraft.loottables.ItemSection;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.util.logging.Logger;

public class LootTreeModel extends DefaultTreeModel {

    public static LootTreeModel generateBlankModel() {
        return new LootTreeModel(new LootSectionTreeNode(new ItemSection("", Logger.getLogger("LootTableLogger"))));
    }

    LootTreeModel(final TreeNode root) {
        super(root);
    }

    LootTreeModel(final TreeNode root, final boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
    }

    @Override
    public LootSectionTreeNode getRoot() {
        return (LootSectionTreeNode) super.getRoot();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
