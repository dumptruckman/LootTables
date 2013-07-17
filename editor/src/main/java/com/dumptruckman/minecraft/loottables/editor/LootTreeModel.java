package com.dumptruckman.minecraft.loottables.editor;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class LootTreeModel extends DefaultTreeModel {

    public LootTreeModel(final TreeNode root) {
        super(root);
    }

    public LootTreeModel(final TreeNode root, final boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
    }
}
