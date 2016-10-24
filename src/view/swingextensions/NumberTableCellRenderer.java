package view.swingextensions;

import java.awt.Color;
import java.awt.Component;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 * A cell renderer which adds padding to each field and displays the double
 * value of the cell as a percentage.
 *
 * @author Anaïs Ools
 */
public class NumberTableCellRenderer extends PaddingTableCellRenderer {

    private final int m_decimals;
    private final boolean m_percent;
    private DecimalFormat m_df;

    public NumberTableCellRenderer(int decimals) {
        setHorizontalAlignment(SwingConstants.RIGHT);
        m_decimals = decimals;
        m_percent = false;
        setDF();
    }

    public NumberTableCellRenderer(int decimals, boolean percent) {
        setHorizontalAlignment(SwingConstants.RIGHT);
        m_decimals = decimals;
        m_percent = percent;
        setDF();
    }

    @Override
    public void setValue(Object value) {
        if (value.getClass().equals(Double.class)) {
            if (m_percent) {
                value = numberToPercent((double) value);
            } else {
                value = numberToString((double) value);
            }
        } else if (value.getClass().equals(Integer.class)) {
            if (m_percent) {
                value = numberToPercent((double) ((Integer) value));
            } else {
                value = numberToString((double) ((Integer) value));
            }

        }
        super.setValue(value);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        return cellComponent;
    }

    private void setDF() {
        m_df = new DecimalFormat();
        m_df.setRoundingMode(RoundingMode.HALF_UP);
        m_df.setMinimumFractionDigits(m_decimals);
        m_df.setMaximumFractionDigits(m_decimals);
    }

    private String numberToString(double d) {
        String s = "";
        s += m_df.format(d);
        s = s.replace(".", ",");
        return s;
    }

    private String numberToPercent(double d) {
        String s = "";
        double dec = Math.pow(10, m_decimals + 2);
        d = Math.round(d * (dec)) / dec * 100;
        s += m_df.format(d);
        s = s.replace(".", ",");
        s += " %";
        return s;
    }

}
