package com.dumptruckman.minecraft.loottables.editor;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import java.text.ParseException;

public class FileNameInputVerifier extends InputVerifier {
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
}
