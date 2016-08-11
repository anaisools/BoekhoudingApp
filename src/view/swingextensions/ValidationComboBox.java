package view.swingextensions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.border.*;

/**
 * This class extends an editable JComboBox. It allows to check for a pattern
 * and add a placeholder. When typing, the user gets a dropdown with the items
 * that contain the current string.
 *
 * @author Anaïs Ools
 */
public class ValidationComboBox extends JComboBox implements ValidationComponent {

    // Members & constructors --------------------------------------------------
    private final String[] m_values;
    private JTextField m_textField;
    private String m_placeholder;
    private String m_pattern;
    private boolean m_canBeEmpty;
    private boolean m_isValid;

    public ValidationComboBox(String[] values) {
        super(values);
        m_values = values;
        this.setEditable(true);
        m_textField = ((JTextField) this.getEditor().getEditorComponent());
        addListeners();
        setText("");
    }

    public ValidationComboBox(boolean canBeEmpty, String placeholder, String pattern, String[] values) {
        super(values);
        m_values = values;
        this.setEditable(true);
        m_textField = ((JTextField) this.getEditor().getEditorComponent());
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
        return m_textField.getText();
    }

    @Override
    public void setValue(Object value) {
        if (value == null) {
            setText("");
        } else if (value.getClass().equals(String.class)) {
            setText((String) value);
        }
    }

    @Override
    public void forceValidate() {
        validateText();
    }

    // Private functions -------------------------------------------------------
    /**
     * Add the preferred behaviour for the ValidationComboBox.
     */
    private void addListeners() {
        // check if selected item changed, currently does nothing
        this.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent ie) {
                validateText();
            }
        });

        // check if typed text changed
        ValidationComboBox parent = this;
        m_textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                int key = ke.getKeyCode();
                char keyvalue = ke.getKeyChar();
                if (key == KeyEvent.VK_ENTER) {
                    m_textField.setText((String) parent.getSelectedItem());
                    parent.hidePopup();
                } else if (Character.isLetter(keyvalue) || Character.isDigit(keyvalue) || Character.isSpaceChar(keyvalue) || key == KeyEvent.VK_BACK_SPACE || key == KeyEvent.VK_DELETE) {
                    changed();
                }
                validateText();
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                int key = ke.getKeyCode();
                char keyvalue = ke.getKeyChar();
                if (Character.isLetter(keyvalue) || Character.isDigit(keyvalue) || Character.isSpaceChar(keyvalue) || key == KeyEvent.VK_BACK_SPACE || key == KeyEvent.VK_DELETE) {
                    changed();
                }
                validateText();
            }

            private void changed() {
                parent.showFilteredPopup(m_textField.getText());
            }
        });

        // check if focus lost
        m_textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent fe) {
                m_textField.selectAll();
            }

            @Override
            public void focusLost(FocusEvent fe) {
                parent.validateText();
            }
        });
    }

    /**
     * Show the combobox's popup, but filter it such that all values contain a
     * certain string. Set the string as the editor's value.
     *
     * @param filter the string to use as filter
     */
    private void showFilteredPopup(String filter) {
        // filter the values
        String[] newArray = getFilteredList(filter); //{"lalala", "test test"};

        // update the combobox's values
        DefaultComboBoxModel model = (DefaultComboBoxModel) this.getModel();
        model.removeAllElements();
        for (String item : newArray) {
            model.addElement(item);
        }
        this.setModel(model);

        // correct the editor
        m_textField = ((JTextField) this.getEditor().getEditorComponent());
        setText(filter);

        // first hide, then show the popup, so the layout gets updated
        this.hidePopup();
        this.showPopup();
    }

    /**
     * Filter a list of strings such that the strings in the result list contain
     * a certain filter.
     *
     * @param filter string that all the result strings should contain
     * @return
     */
    private String[] getFilteredList(String filter) {
        if (filter.length() == 0) {
            return m_values;
        }

        ArrayList<String> list = new ArrayList();
        for (int a = 0; a < m_values.length; a++) {
            if (m_values[a].toLowerCase().contains(filter.toLowerCase())) {
                list.add(m_values[a]);
            }
        }
        return list.toArray(new String[list.size()]);
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
        Border b = this.getBorder();
        Insets i = b.getBorderInsets(this);
        Border margin = new EmptyBorder(i.top - 1, i.left - 1, i.bottom - 1, i.right - 1);
        Color c;
        if (this.getValue().equals("")) {
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
        this.repaint();
    }

    /**
     * Set the displayed text.
     *
     * @param text
     */
    private void setText(String text) {
        m_textField.setText(text);
    }

    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);

        // set placeholder
        if (m_placeholder == null || m_placeholder.length() == 0 || getValue().length() > 0) {
            return;
        }
        final Graphics2D g = (Graphics2D) pG;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.gray);
        g.drawString(m_placeholder, getInsets().left + 2, pG.getFontMetrics().getMaxAscent() + getInsets().top + 2);
    }
}
