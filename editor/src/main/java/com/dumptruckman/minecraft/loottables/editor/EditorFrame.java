package com.dumptruckman.minecraft.loottables.editor;

import com.dumptruckman.minecraft.loottables.LootSection;
import net.miginfocom.swing.MigLayout;
import org.bukkit.Material;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Logger;

public class EditorFrame extends JFrame implements WindowListener {

    private final JPanel panelMain = new JPanel(new MigLayout("", "[][grow,40%][][grow][]", "[][grow][]"));

    private final JFormattedTextField textFieldTableName;
    //private final JTextField textFieldFileName;
    private final JTree treeLootTable;
    private final JTable tableMaterial;
    private final JTextField textFieldMaterialFilter;

    //private final JButton buttonChangeSaveLocation;
    private final JButton buttonAddSection;
    private final JButton buttonEditSection;
    private final JButton buttonRemoveSection;
    private final JButton buttonAddMaterial;

    private LootTreeModel lootTreeModel;

    public EditorFrame() {

        lootTreeModel = LootTreeModel.generateBlankModel();

        setTitle("LootTables Editor");
        setSize(700, 500);
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignore) { /* Use the default look and feel */ }

        // Set up menu bar
        JMenuBar menuBar = new JMenuBar();

        // Set up file menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        menuBar.add(fileMenu);

        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.setMnemonic('N');
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        newMenuItem.setToolTipText("Opens a new blank LootTable for editing.");
        fileMenu.add(newMenuItem);

        JMenuItem loadMenuItem = new JMenuItem("Open");
        loadMenuItem.setMnemonic('O');
        loadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        loadMenuItem.setToolTipText("Opens a previously created LootTable for editing.");
        fileMenu.add(loadMenuItem);

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setMnemonic('S');
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        saveMenuItem.setToolTipText("Saves the currently opened LootTable for later use.");
        fileMenu.add(saveMenuItem);

        JMenuItem saveAsMenuItem = new JMenuItem("Save As...");
        saveAsMenuItem.setMnemonic('A');
        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke("control alt S"));
        saveAsMenuItem.setToolTipText("Saves the currently opened LootTable for later use with a specified file name.");
        fileMenu.add(saveAsMenuItem);

        JMenuItem quitMenuItem = new JMenuItem("Exit");
        quitMenuItem.setMnemonic('x');
        quitMenuItem.setToolTipText("Exits the LootTable Editor application.");
        quitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dispatchEvent(new WindowEvent(EditorFrame.this, WindowEvent.WINDOW_CLOSING));
            }
        });
        fileMenu.add(quitMenuItem);

        setJMenuBar(menuBar);

        // Begin layout
        textFieldTableName = new JFormattedTextField(new FileNameFormatter());
        textFieldTableName.setInputVerifier(new FileNameInputVerifier());

        JLabel label = new JLabel("Name:");
        label.setLabelFor(textFieldTableName);
        panelMain.add(label);
        panelMain.add(textFieldTableName, "grow,span 3,wrap");

        /*
        textFieldFileName = new JTextField();
        textFieldFileName.setEditable(false);
        textFieldFileName.setFocusable(false);
        label = new JLabel("File:");
        label.setLabelFor(textFieldFileName);
        panelMain.add(label);
        panelMain.add(textFieldFileName, "grow");

        buttonChangeSaveLocation = new JButton("Change");
        panelMain.add(buttonChangeSaveLocation, "wrap");
        */

        JPanel panel = new JPanel(new MigLayout("fill", "[50%][50%][50%]", "[grow][]"));
        treeLootTable = new JTree(lootTreeModel);
        treeLootTable.setCellRenderer(new LootSectionTreeCellRenderer());
        textFieldTableName.getDocument().addDocumentListener(new DocumentListener() {

            private void updateTree() {
                lootTreeModel.getRoot().getUserObject().setName(textFieldTableName.getText());
                treeLootTable.updateUI();
            }

            @Override
            public void insertUpdate(DocumentEvent evt) {
                updateTree();
            }

            @Override
            public void removeUpdate(DocumentEvent evt) {
                updateTree();
            }

            @Override
            public void changedUpdate(DocumentEvent evt) {
                updateTree();
            }
        });
        JScrollPane scrollPane = new JScrollPane(treeLootTable);
        panel.add(scrollPane, "span 3,grow,wrap");
        buttonAddSection = new JButton("Add");
        buttonAddSection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                // TODO Add editor dialog
            }
        });
        buttonEditSection = new JButton("Edit");
        buttonRemoveSection = new JButton("Remove");
        panel.add(buttonAddSection, "growx");
        panel.add(buttonEditSection, "growx");
        panel.add(buttonRemoveSection, "growx");
        panelMain.add(panel, "span 3,grow");

        panel = new JPanel(new MigLayout("fill", "[][grow]", "[][grow][]"));
        textFieldMaterialFilter = new JTextField("");
        label = new JLabel("Filter:");
        panel.add(label);
        label.setLabelFor(textFieldMaterialFilter);
        textFieldMaterialFilter.setToolTipText("Enter a filter to easily find the material you are looking for.  This field accepts regex strings.");
        panel.add(textFieldMaterialFilter, "growx,wrap");

        MaterialTableModel tableModel = new MaterialTableModel();
        tableModel.addColumn("Material", Material.values());
        tableMaterial = new JTable() {
            private final MaterialCellRenderer renderer = new MaterialCellRenderer();
            public MaterialCellRenderer getCellRenderer(int row, int column) {
                return renderer;
            }
        };
        tableMaterial.setModel(tableModel);
        final TableRowSorter<MaterialTableModel> sorter = new TableRowSorter<MaterialTableModel>(tableModel);
        final MaterialTableModel.TextFieldRegexFilter filter = new MaterialTableModel.TextFieldRegexFilter(textFieldMaterialFilter);
        tableMaterial.setRowSorter(sorter);
        textFieldMaterialFilter.getDocument().addDocumentListener(new DocumentListener() {

            private void searchFieldChangedUpdate(DocumentEvent evt) {
                sorter.setRowFilter(filter);
            }

            @Override
            public void insertUpdate(DocumentEvent evt) {
                searchFieldChangedUpdate(evt);
            }

            @Override
            public void removeUpdate(DocumentEvent evt) {
                searchFieldChangedUpdate(evt);
            }

            @Override
            public void changedUpdate(DocumentEvent evt) {
                searchFieldChangedUpdate(evt);
            }
        });
        scrollPane = new JScrollPane(tableMaterial);
        panel.add(scrollPane, "grow,span 2,wrap");
        buttonAddMaterial = new JButton("Add & Customize");
        panel.add(buttonAddMaterial, "span 2,growx");
        panelMain.add(panel, "span 2,grow");

        add(panelMain);
    }

    @Override
    public void windowOpened(final WindowEvent e) { }

    @Override
    public void windowClosing(final WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void windowClosed(final WindowEvent e) { }

    @Override
    public void windowIconified(final WindowEvent e) { }

    @Override
    public void windowDeiconified(final WindowEvent e) { }

    @Override
    public void windowActivated(final WindowEvent e) { }

    @Override
    public void windowDeactivated(final WindowEvent e) { }
}
