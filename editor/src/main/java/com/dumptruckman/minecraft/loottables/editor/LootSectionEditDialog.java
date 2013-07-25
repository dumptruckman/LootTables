package com.dumptruckman.minecraft.loottables.editor;

import com.dumptruckman.minecraft.loottables.LootSection;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class LootSectionEditDialog extends JDialog {

    public LootSectionEditDialog(final Component parent, @Nullable final LootSection lootSection) {
        super((Frame) null, true);
        setSize(200, 200);
        setResizable(false);
        setLocationRelativeTo(parent);
        setModalityType(ModalityType.APPLICATION_MODAL);

        final JPanel mainPanel = new JPanel(new MigLayout("fill", "[][grow]", "[][grow]"));
        add(mainPanel);

        JLabel label = new JLabel("Name:");
        final JFormattedTextField nameField = new JFormattedTextField(new FileNameFormatter());
        nameField.setText(lootSection != null ? lootSection.getSectionName() : "");
        nameField.setInputVerifier(new FileNameInputVerifier());
        label.setLabelFor(nameField);
        mainPanel.add(label);
        mainPanel.add(nameField, "grow,wrap");

        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dispatchEvent(new WindowEvent(LootSectionEditDialog.this, WindowEvent.WINDOW_CLOSING));
            }
        });
        mainPanel.add(cancelButton, "span 2,grow");
    }

    public void showDialog() {
        setVisible(true);
    }

}