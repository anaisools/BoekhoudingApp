package view.swingextensions;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;

/**
 * This class extends a JTextField such that it only accepts dates in the format
 * of dd/MM/yyyy.
 *
 * @author Anaïs Ools
 */
public class ValidationDateField extends JTextField implements ValidationComponent {

    // Members & constructors --------------------------------------------------
    private String m_placeholder;
    private boolean m_canBeEmpty;
    private boolean m_isValid;
    private SimpleDateFormat m_format;

    public ValidationDateField(boolean canBeEmpty, Date initialValue, String placeholder) {
        setPlaceholder(placeholder);
        setFormat(new SimpleDateFormat("dd/MM/yyyy"));
        canBeEmpty(canBeEmpty);
        addListeners();
        setValue(initialValue);
    }

    // Public functions --------------------------------------------------------
    public void setPlaceholder(String placeholder) {
        m_placeholder = placeholder;
    }

    public void setFormat(SimpleDateFormat format) {
        m_format = format;
        m_format.setLenient(false);
    }

    public void canBeEmpty(boolean allowed) {
        m_canBeEmpty = allowed;
    }

    @Override
    public boolean isValid() {
        return m_isValid;
    }

    @Override
    public Date getValue() {
        if (isValid()) {
            try {
                return m_format.parse(getText());
            } catch (ParseException ex) {
                return null;
            }
        }
        return null;
    }

    @Override
    public void setValue(Object value) {
        if (value == null) {
            return;
        }
        if (value.getClass().equals(Date.class)) {
            setText((Date) value);
        }
    }

    @Override
    public void forceValidate() {
        validateText();
    }

    // Private functions -------------------------------------------------------
    /**
     * Add the preferred behaviour for the ValidationDateField.
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
        text = correctDate(getText());

        // check if the current text is a valid date
        try {
            Date d = m_format.parse(text);
            setText(d);
            setValid(true);
        } catch (ParseException ex) {
            setValid(false);
        }
    }

    /**
     * Correct some issues from a string to make it easier to parse it as a
     * date.
     *
     * @param date
     * @return
     */
    private String correctDate(String date) {
        if (date == null) {
            return null;
        }

        date = date.replaceAll("-", "/");
        date = date.replaceAll("\\.", "/");
        date = date.replaceAll("[^\\d/]", "");

        // add year if not present
        String[] parts = date.split("/");
        if (parts.length == 2) { // if year not specified, set to current year
            Date today = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy");
            date = parts[0] + "/" + parts[1] + "/" + df.format(today);
        }
        if (parts.length == 3 && parts[2].length() == 2) {
            SimpleDateFormat df = new SimpleDateFormat("yy");
            SimpleDateFormat df2 = new SimpleDateFormat("yyyy");
            try {
                date = parts[0] + "/" + parts[1] + "/" + df2.format(df.parse(parts[2]));
            } catch (ParseException ex) {
            }
        }

        return date;
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
     * Set the displayed date.
     *
     * @param date
     */
    private void setText(Date date) {
        setText(m_format.format(date));
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
