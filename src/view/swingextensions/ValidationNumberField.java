package view.swingextensions;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * This class extends a textField such that it's content is a number.
 *
 * @author Anaïs Ools
 */
public class ValidationNumberField extends JTextField implements ValidationComponent {

    // Members & constructors --------------------------------------------------
    private String m_placeholder;
    private boolean m_canBeEmpty;
    private boolean m_isValid;
    private boolean m_positive;

    public ValidationNumberField(boolean canBeEmpty, double initialValue, String placeholder, boolean positive) {
        m_positive = positive;
        setPlaceholder(placeholder);
        canBeEmpty(canBeEmpty);
        addListeners();
        setValue(initialValue);
    }

    public ValidationNumberField(boolean canBeEmpty, String placeholder, boolean positive) {
        m_positive = positive;
        setPlaceholder(placeholder);
        canBeEmpty(canBeEmpty);
        addListeners();
        setValue(null);
    }

    // Public functions --------------------------------------------------------
    public void setPlaceholder(String placeholder) {
        m_placeholder = placeholder;
    }

    public void canBeEmpty(boolean allowed) {
        m_canBeEmpty = allowed;
    }

    @Override
    public boolean isValid() {
        return m_isValid;
    }

    @Override
    public Double getValue() {
        if (isValid()) {
            try {
                return Double.parseDouble(getText());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    @Override
    public void setValue(Object value) {
        if (value == null) {
            setText("");
        } else if (value.getClass().equals(Double.class)) {
            setText((double) value);
        }
    }

    @Override
    public void forceValidate() {
        validateText();
    }

    // Private functions -------------------------------------------------------
    /**
     * Add the preferred behaviour for the ValidationNumberField.
     */
    private void addListeners() {
        // check when focus lost
        this.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent fe) {
                selectAll();
            }

            @Override
            public void focusLost(FocusEvent fe) {
                validateText();
            }
        });
    }

    /**
     * Check if the selected or typed text is valid.
     */
    private void validateText() {
        String text = getText();
        if (text == null) {
            setValid(false);
            return;
        }

        // no text
        if (text.equals("")) {
            if (m_canBeEmpty) {
                setValid(true);
            } else {
                setValid(false);
            }
            return;
        }

        // try to correct if necessary
        text = correctNumber(getText());

        // check if the current text is a valid date
        try {
            double d = Double.parseDouble(text);
            if (m_positive && d < 0) {
                setValid(false);
                return;
            }
            setText(d);
            setValid(true);
        } catch (NumberFormatException ex) {
            setValid(false);
        }
    }

    /**
     * Correct some issues from a string to make it easier to parse it as a
     * number.
     *
     * @param text
     * @return
     */
    private String correctNumber(String text) {
        if (text == null) {
            return null;
        }
        text = text.replaceAll(",", ".");
        text = text.replaceAll("[^\\d.-]", "");
        try {
            if (Double.parseDouble(text) == 0.0) {
                text = text.replaceAll("-", "");
            }
        } catch (NumberFormatException e) {
        }
        return text;
    }

    /**
     * If valid, set the border to green. If not, set the border to red. Also
     * set the local variable that can be retrieved with function IsValid().
     *
     * @param valid
     */
    private void setValid(boolean valid) {
        Border b = this.getBorder();
        Insets i = b.getBorderInsets(this);
        Border margin = new EmptyBorder(i.top - 1, i.left - 1, i.bottom - 1, i.right - 1);
        Color c;
        if (this.getText().equals("")) {
            if (m_canBeEmpty) {
                m_isValid = true;
                c = new Color(171, 173, 179);
            } else {
                m_isValid = false;
                c = Color.red;
            }
        } else {
            m_isValid = valid;
            if (m_isValid) {
                c = new Color(0, 200, 0);
            } else {
                c = (Color.red);
            }
        }
        Border border = new LineBorder(c);
        this.setBorder(new CompoundBorder(border, margin));
    }

    /**
     * Set the displayed number.
     *
     * @param number
     */
    private void setText(double number) {
        setText(String.valueOf(number));
    }

    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);

        // set placeholder
        if (m_placeholder == null || m_placeholder.length() == 0 || getText().length() > 0) {
            return;
        }
        final Graphics2D g = (Graphics2D) pG;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(getDisabledTextColor());
        g.drawString(m_placeholder, getInsets().left, pG.getFontMetrics().getMaxAscent() + getInsets().top);
    }
}
