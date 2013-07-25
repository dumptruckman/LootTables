package com.dumptruckman.minecraft.loottables.editor;

import com.dumptruckman.minecraft.loottables.LootSection;
import net.miginfocom.layout.CC;
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
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class EditorFrame extends JFrame implements WindowListener {

    private final JPanel panelMain = new JPanel(new MigLayout("", "[][grow]", "[][grow 200][]"));

    private final JFormattedTextField textFieldTableName;
    private final JTree treeLootTable;

    private final JButton buttonAddSection;
    private final JButton buttonEditSection;
    private final JButton buttonRemoveSection;

    private final JTextPane textPaneDebug;
    private PrintStream oldOut;
    private PrintStream oldErr;

    private LootTreeModel lootTreeModel;

    public EditorFrame() {

        lootTreeModel = LootTreeModel.generateBlankModel();

        setTitle("LootTables Editor");
        setSize(600, 600);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
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

        // Set up file menu
        JMenu helpMenu = new JMenu("Help");
        fileMenu.setMnemonic('H');
        menuBar.add(helpMenu);

        final JMenuItem debugMenuItem = new JMenuItem("Enable Debug Output");
        debugMenuItem.setMnemonic('D');
        debugMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
        textPaneDebug = new JTextPane();
        textPaneDebug.setEditable(false);
        final JScrollPane debugScroll = new JScrollPane(textPaneDebug);
        debugMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (debugMenuItem.getText().equals("Enable Debug Output")) {
                    debugMenuItem.setText("Disable Debug Output");
                    redirectSystemStreamsToDebug();
                    debugScroll.setVisible(true);
                    panelMain.add(debugScroll, "span 2,grow,height 100:300:600");
                    debugMenuItem.revalidate();
                    panelMain.revalidate();
                } else {
                    debugMenuItem.setText("Enable Debug Output");
                    redirectSystemStreamsToConsole();
                    debugScroll.setVisible(false);
                    panelMain.remove(debugScroll);
                    debugMenuItem.revalidate();
                    panelMain.revalidate();
                }
            }
        });
        helpMenu.add(debugMenuItem);
        setJMenuBar(menuBar);

        // Begin layout
        textFieldTableName = new JFormattedTextField(new FileNameFormatter());
        textFieldTableName.setInputVerifier(new FileNameInputVerifier());

        JLabel label = new JLabel("Name:");
        label.setLabelFor(textFieldTableName);
        panelMain.add(label);
        panelMain.add(textFieldTableName, "grow,span 2,wrap");

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
                LootSectionEditDialog editDialog = new LootSectionEditDialog(EditorFrame.this, null);
                editDialog.showDialog();
            }
        });
        buttonEditSection = new JButton("Edit");
        buttonRemoveSection = new JButton("Remove");
        panel.add(buttonAddSection, "growx");
        panel.add(buttonEditSection, "growx");
        panel.add(buttonRemoveSection, "growx");
        panelMain.add(panel, "span 2,grow,wrap");

        add(panelMain);
    }

    private void updateDebugTextPane(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Document doc = textPaneDebug.getDocument();
                try {
                    doc.insertString(doc.getLength(), text, null);
                } catch (BadLocationException e) {
                    throw new RuntimeException(e);
                }
                textPaneDebug.setCaretPosition(doc.getLength() - 1);
            }
        });
    }

    private void redirectSystemStreamsToDebug() {
        oldOut = System.out;
        oldErr = System.err;
        OutputStream out = new OutputStream() {

            @Override
            public void write(final int b) throws IOException {
                updateDebugTextPane(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                updateDebugTextPane(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    private void redirectSystemStreamsToConsole() {
        System.setOut(oldOut);
        System.setErr(oldErr);
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
