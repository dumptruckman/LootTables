package com.dumptruckman.minecraft.loottables.editor;

import net.miginfocom.swing.MigLayout;
import org.bukkit.Material;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.PatternSyntaxException;


public class EditorGui extends JFrame implements WindowListener {

    private final JPanel panelMain = new JPanel(new MigLayout("", "[][grow,40%][][grow]", "[][grow][]"));

    private final JFormattedTextField textFieldTableName;
    private final JTextField textFieldFileName;
    private final JTree treeLootTable;
    private final JTable tableMaterial;
    private final JTextField textFieldMaterialFilter;

    public EditorGui() {
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
                dispatchEvent(new WindowEvent(EditorGui.this, WindowEvent.WINDOW_CLOSING));
            }
        });
        fileMenu.add(quitMenuItem);

        setJMenuBar(menuBar);

        // Begin layout
        textFieldTableName = new JFormattedTextField(new AbstractFormatter() {
            @Override
            public Object stringToValue(final String text) throws ParseException {
                for (int i = 0; i < text.length(); i++) {
                    char c = text.charAt(i);
                    if (c < 32
                            || c == '\\'
                            || c == '/'
                            || c == ':'
                            || c == '*'
                            || c == '?'
                            || c == '"'
                            || c == '<'
                            || c == '>'
                            || c == '|') {
                        throw new ParseException("Unnacceptable input '" + c + "' in text", i);
                    }
                }
                return text;
            }

            @Override
            public String valueToString(final Object value) throws ParseException {
                return value == null ? "" : value.toString();
            }
        });
        textFieldTableName.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(final JComponent input) {
                if (input instanceof JFormattedTextField) {
                    JFormattedTextField ftf = (JFormattedTextField)input;
                    AbstractFormatter formatter = ftf.getFormatter();
                    if (formatter != null) {
                        String text = ftf.getText();
                        try {
                            formatter.stringToValue(text);
                            return true;
                        } catch (ParseException pe) {
                            return false;
                        }
                    }
                }
                return true;
            }
        });
        JLabel label = new JLabel("Name:");
        label.setLabelFor(textFieldTableName);
        panelMain.add(label);
        panelMain.add(textFieldTableName, "grow");

        textFieldFileName = new JTextField();
        textFieldFileName.setEditable(false);
        textFieldFileName.setFocusable(false);
        label = new JLabel("File:");
        label.setLabelFor(textFieldFileName);
        panelMain.add(label);
        panelMain.add(textFieldFileName, "grow,wrap");

        JPanel panel = new JPanel(new MigLayout("fill"));
        treeLootTable = new JTree();
        JScrollPane scrollPane = new JScrollPane(treeLootTable);
        panel.add(scrollPane, "grow");
        panelMain.add(panel, "span 3,grow");

        panel = new JPanel(new MigLayout("fill", "[][grow]", "[][grow]"));
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
        panel.add(scrollPane, "grow,span 2");
        panelMain.add(panel, "grow");

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
