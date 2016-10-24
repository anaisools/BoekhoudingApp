package view.swingextensions;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 * A cell renderer which adds padding to each field and displays the double
 * value of the cell as a price.
 *
 * @author Anaïs Ools
 */
public class PriceTableCellRenderer extends PaddingTableCellRenderer {

    private final boolean m_negativeInRed;
    private final boolean m_zeroGreyedOut;

    public PriceTableCellRenderer() {
        setHorizontalAlignment(SwingConstants.RIGHT);
        m_negativeInRed = false;
        m_zeroGreyedOut = false;
    }

    public PriceTableCellRenderer(boolean negativeInRed, boolean zeroGreyedOut) {
        setHorizontalAlignment(SwingConstants.RIGHT);
        m_negativeInRed = negativeInRed;
        m_zeroGreyedOut = zeroGreyedOut;
    }

    @Override
    public void setValue(Object value) {
        if (value == null) {
            value = "";
        }
        if (value.getClass().equals(Double.class)) {
            value = model.Settings.GetInstance().convertPriceToString((double) value);
        } else if (value.getClass().equals(Integer.class)) {
            value = model.Settings.GetInstance().convertPriceToString((double) ((Integer) value));
        }
        super.setValue(value);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value != null && value.getClass().equals(Double.class)) {
            double d = (double) value;
            if (m_negativeInRed && d < 0.0) {
                cellComponent.setForeground(Color.red);
            } else if (m_zeroGreyedOut && d == 0.0) {
                cellComponent.setForeground(Color.lightGray);
            } else {
                cellComponent.setForeground(Color.black);
            }
        }
        return cellComponent;
    }
}
