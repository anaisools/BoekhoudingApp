package view.swingextensions;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * This class extends a JTextField. It allows to check for a pattern and add a
 * placeholder.
 *
 * @author Anaïs Ools
 */
public class ValidationTextField extends JTextField implements ValidationComponent {

    // Members & constructors --------------------------------------------------
    private String m_placeholder;
    private String m_pattern;
    private boolean m_canBeEmpty;
    private boolean m_isValid;

    public ValidationTextField(boolean canBeEmpty, String placeholder, String pattern) {
        super();
        setPlaceholder(placeholder);
        setPattern(pattern);
        canBeEmpty(canBeEmpty);
        addListeners();
        setText("");
    }

    // Public functions --------------------------------------------------------
    public void setPlaceholder(String placeholder) {
        m_placeholder = placeholder;
    }

    public void setPattern(String pattern) {
        m_pattern = pattern;
    }

    public void canBeEmpty(boolean allowed) {
        m_canBeEmpty = allowed;
    }

    @Override
    public boolean isValid() {
        return m_isValid;
    }

    @Override
    public String getValue() {
        return getText();
    }

    @Override
    public void setValue(Object value) {
        if (value.getClass().equals(String.class)) {
            setText((String) value);
        } else if (value.getClass().equals(model.CategoryString.class)) {
            setText(((model.CategoryString) value).toString());
        }
    }

    @Override
    public void forceValidate() {
        validateText();
    }

    // Private functions -------------------------------------------------------
    /**
     * Add the preferred behaviour for the ValidationTextField.
     */
    private void addListeners() {
        // check if typed text changed
        this.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                validateText();
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                validateText();
            }

        });

        // check if focus lost
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
     * Check if the selected or typed text is valid, according to the pattern.
     * If no pattern is set, this will always be true.
     */
    private void validateText() {
        // no pattern
        if (m_pattern == null || m_pattern.equals("")) {
            setValid(true);
            return;
        }

        String text = getValue();

        // no text
        if (text.equals("")) {
            if (m_canBeEmpty) {
                setValid(true);
            } else {
                setValid(false);
            }
            return;
        }

        // default
        setValid(text.contains(m_pattern));
    }

    /**
     * If valid, set the border to green. If not, set the border to red. Also
     * set the local variable that can be retrieved with function IsValid().
     *
     * @param valid
     */
    private void setValid(boolean valid) {
        Color c;
        if (this.getText() == null || this.getText().equals("")) {
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
        try {
            Border b = this.getBorder();
            Insets i = b.getBorderInsets(this);
            Border margin = new EmptyBorder(i.top - 1, i.left - 1, i.bottom - 1, i.right - 1);
            Border border = new LineBorder(c);
            this.setBorder(new CompoundBorder(border, margin));
        } catch (NullPointerException npe) {
        }
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
