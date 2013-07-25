package com.dumptruckman.minecraft.loottables.editor;

import com.dumptruckman.minecraft.loottables.LootSection;
import net.miginfocom.swing.MigLayout;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EnchantSelectDialog extends JDialog {

    private static final String ENCHANTMENT_COLUMN = "Enchantment";

    private Enchantment selection = null;

    public EnchantSelectDialog(final Component parent) {
        super((Frame) null, true);
        setSize(300, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setModalityType(ModalityType.APPLICATION_MODAL);

        final JPanel mainPanel = new JPanel(new MigLayout("fill", "[][grow]", "[][grow][]"));
        add(mainPanel);

        final JTextField textFieldMaterialFilter = new JTextField("");
        JLabel label = new JLabel("Filter:");
        mainPanel.add(label);
        label.setLabelFor(textFieldMaterialFilter);
        textFieldMaterialFilter.setToolTipText("Enter a filter to easily find the material you are looking for.  This field accepts regex strings.");
        mainPanel.add(textFieldMaterialFilter, "growx,wrap");

        final EnchantTableModel tableModel = new EnchantTableModel();
        tableModel.addColumn(ENCHANTMENT_COLUMN, Enchantments.getEnchantments());
        final JTable tableMaterial = new JTable() {
            private final EnchantCellRenderer renderer = new EnchantCellRenderer();
            public EnchantCellRenderer getCellRenderer(int row, int column) {
                return renderer;
            }
        };
        tableMaterial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableMaterial.setModel(tableModel);
        final TableRowSorter<EnchantTableModel> sorter = new TableRowSorter<EnchantTableModel>(tableModel);
        final EnchantTableModel.TextFieldRegexFilter filter = new EnchantTableModel.TextFieldRegexFilter(textFieldMaterialFilter);
        tableMaterial.setRowSorter(sorter);
        textFieldMaterialFilter.getDocument().addDocumentListener(new DocumentListener() {

            private void searchFieldChangedUpdate(final DocumentEvent evt) {
                sorter.setRowFilter(filter);
            }

            @Override
            public void insertUpdate(final DocumentEvent evt) {
                searchFieldChangedUpdate(evt);
            }

            @Override
            public void removeUpdate(final DocumentEvent evt) {
                searchFieldChangedUpdate(evt);
            }

            @Override
            public void changedUpdate(final DocumentEvent evt) {
                searchFieldChangedUpdate(evt);
            }
        });
        JScrollPane scrollPane = new JScrollPane(tableMaterial);
        mainPanel.add(scrollPane, "grow,span 2,wrap");

        JButton button = new JButton("Select");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    selection = (Enchantment) tableMaterial.getModel().getValueAt(tableMaterial.getSelectedRow(), tableMaterial.getSelectedColumn());
                } catch (ArrayIndexOutOfBoundsException ignore) {
                    selection = null;
                }
                setVisible(false);
                dispose();
            }
        });
        mainPanel.add(button, "span 2,grow,wrap");

        button = new JButton("Cancel");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                selection = null;
                setVisible(false);
                dispose();
            }
        });
        mainPanel.add(button, "span 2,grow");
    }

    public Enchantment showDialog() {
        setVisible(true);
        return selection;
    }
}