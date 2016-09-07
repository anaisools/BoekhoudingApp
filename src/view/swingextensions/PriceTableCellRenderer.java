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
        if (value.getClass().equals(Double.class)) {
            super.setValue(model.Settings.GetInstance().convertPriceToString((double) value));
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
