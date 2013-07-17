package com.dumptruckman.minecraft.loottables.editor;

import com.dumptruckman.minecraft.loottables.LootSection;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class LootSectionTreeNode extends DefaultMutableTreeNode {

    public LootSectionTreeNode(final LootSection userObject) {
        super(userObject);
    }

    @Override
    public LootSectionTreeNode getParent() {
        return (LootSectionTreeNode) super.getParent();
    }

    @Override
    public LootSectionTreeNode getChildAt(final int index) {
        return (LootSectionTreeNode) super.getChildAt(index);
    }

    @Override
    public void setUserObject(final Object userObject) {
        super.setUserObject(userObject);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public LootSection getUserObject() {
        return (LootSection) super.getUserObject();
    }

    @Override
    public LootSectionTreeNode getSharedAncestor(final DefaultMutableTreeNode aNode) {
        return (LootSectionTreeNode) super.getSharedAncestor(aNode);
    }

    @Override
    public LootSectionTreeNode getRoot() {
        return (LootSectionTreeNode) super.getRoot();
    }

    @Override
    public LootSectionTreeNode getNextNode() {
        return (LootSectionTreeNode) super.getNextNode();
    }

    @Override
    public LootSectionTreeNode getPreviousNode() {
        return (LootSectionTreeNode) super.getPreviousNode();
    }

    @Override
    public LootSectionTreeNode getChildAfter(final TreeNode aChild) {
        return (LootSectionTreeNode) super.getChildAfter(aChild);
    }

    @Override
    public LootSectionTreeNode getChildBefore(final TreeNode aChild) {
        return (LootSectionTreeNode) super.getChildBefore(aChild);
    }

    @Override
    public LootSectionTreeNode getFirstChild() {
        return (LootSectionTreeNode) super.getFirstChild();
    }

    @Override
    public LootSectionTreeNode getLastChild() {
        return (LootSectionTreeNode) super.getLastChild();
    }

    @Override
    public LootSectionTreeNode getNextSibling() {
        return (LootSectionTreeNode) super.getNextSibling();
    }

    @Override
    public LootSectionTreeNode getPreviousSibling() {
        return (LootSectionTreeNode) super.getPreviousSibling();
    }

    @Override
    public LootSectionTreeNode getFirstLeaf() {
        return (LootSectionTreeNode) super.getFirstLeaf();
    }

    @Override
    public LootSectionTreeNode getNextLeaf() {
        return (LootSectionTreeNode) super.getNextLeaf();
    }

    @Override
    public LootSectionTreeNode getLastLeaf() {
        return (LootSectionTreeNode) super.getLastLeaf();
    }

    @Override
    public LootSectionTreeNode getPreviousLeaf() {
        return (LootSectionTreeNode) super.getPreviousLeaf();
    }
}
