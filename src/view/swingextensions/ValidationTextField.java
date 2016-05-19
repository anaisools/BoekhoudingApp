package view.swingextensions;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;
import javax.swing.border.*;

/**
 * This class extends a JTextField such that it validates its content.
 *
 * @author Anaïs Ools
 */
public class ValidationTextField extends PlaceholderTextField {

    private boolean m_isValid;
    private final boolean m_canBeEmpty;

    public ValidationTextField(boolean canBeEmpty) {
        m_isValid = false;
        m_canBeEmpty = canBeEmpty;
        ValidationTextField vtf = this;
        this.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent fe) {
            }

            @Override
            public void focusLost(FocusEvent fe) {
                if (vtf.getText().equals("")) {
                    vtf.setValid(false);
                } else {
                    vtf.validateText();
                }
            }
        });
    }

    protected void validateText() {
        setValid(true);
    }

    protected void setValid(boolean valid) {
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

    public boolean isFilled() {
        return !this.getText().equals("");
    }

    @Override
    public boolean isValid() {
        return m_isValid;
    }
}
