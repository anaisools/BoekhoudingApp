package view.swingextensions;

import java.awt.event.*;
import javax.swing.*;
import model.Settings;

/**
 * This class extends a textField such that it's content is a number, converted
 * to a currency. When changing the text, the content will appear as a simple
 * number.
 *
 * @author Anaïs Ools
 */
public class ValidationCurrencyField extends JTextField implements ValidationComponent {

    // Members & constructors --------------------------------------------------
    private double m_value;

    public ValidationCurrencyField() {
        addListeners();
        setValue(0.0);
        setHorizontalAlignment(SwingConstants.RIGHT);
    }

    // Public functions --------------------------------------------------------
    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Object getValue() {
        return m_value;
    }

    @Override
    public void setValue(Object value) {
        if (value == null) {
            return;
        }
        if (value.getClass().equals(Double.class)) {
            m_value = (double) value;
            setText(model.Settings.GetInstance().convertPriceToString(m_value));
        }
    }

    @Override
    public void forceValidate() {
    }

    // Private functions -------------------------------------------------------
    /**
     * Add the preferred behaviour for the ValidationCurrencyField.
     */
    private void addListeners() {
        ValidationCurrencyField parent = this;
        this.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent fe) {
                parent.setText(String.valueOf(Settings.GetInstance().convertPriceToDouble(parent.getText())));
                parent.selectAll();
            }

            @Override
            public void focusLost(FocusEvent fe) {
                parent.setValue(Settings.GetInstance().convertPriceToDouble(parent.getText()));
            }
        });
    }
}
