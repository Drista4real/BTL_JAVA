package Utils;

import javax.swing.text.*;

public class CustomDocumentFilter extends DocumentFilter {
    private final String regex;

    public CustomDocumentFilter(String regex) {
        this.regex = regex;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string.matches(regex)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text.matches(regex)) {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}

