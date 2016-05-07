package view.swingextensions;

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

    public PriceTableCellRenderer() {
        setHorizontalAlignment(SwingConstants.RIGHT);
    }

    @Override
    public void setValue(Object value) {
        String result = String.format("%10.2f", value);
        result = result.trim();
        value = "€  " + result.replace('.', ',');
        super.setValue(value);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
