package com.dumptruckman.minecraft.loottables.editor;

import javax.swing.JFormattedTextField.AbstractFormatter;
import java.text.ParseException;

public class FileNameFormatter extends AbstractFormatter {

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
}
