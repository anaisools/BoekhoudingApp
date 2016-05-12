package view.swingextensions;

import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 * A cellrenderer which adds padding to a cell and displays its contents as a
 * date.
 *
 * @author Anaïs Ools
 */
public class DateTableCellRenderer extends PaddingTableCellRenderer {

    DateFormat df;

    public DateTableCellRenderer() {
        setHorizontalAlignment(SwingConstants.RIGHT);
        df = new SimpleDateFormat("EEEE, dd/MM/yyyy");
    }

    @Override
    public void setValue(Object value) {
        if (value == null) {
            value = "";
        } else if (value.getClass().equals(Date.class)) {
            value = df.format((Date) value);
        }
        super.setValue(value);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
