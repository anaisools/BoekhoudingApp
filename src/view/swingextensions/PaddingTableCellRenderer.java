package view.swingextensions;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import model.Settings;

/**
 * A cellrenderer which adds padding to each field.
 *
 * @author Anaïs Ools
 */
public class PaddingTableCellRenderer extends DefaultTableCellRenderer {

    public PaddingTableCellRenderer() {
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        if (Settings.GetInstance().getHigherRows()) {
            table.setRowHeight(row, 40);
        } else {
            table.setRowHeight(row, 19);
        }
        return this;
    }
}
