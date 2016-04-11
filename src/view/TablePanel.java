package view;

import java.awt.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import model.Transaction;

/**
 * This panel represents a table filled with Transactions. It's placed inside a
 * scrollable JPanel for convenient use.
 *
 * The data in it gets passed along from the parent. A function can be used to
 * change or update the data.
 *
 * @author Anaïs Ools
 */
public class TablePanel extends JPanel {

    private ArrayList<Transaction> m_data;
    private JTable m_table;
    private JScrollPane m_scrollPane;

    private boolean m_colDescription;
    private boolean m_colPrice;
    private boolean m_colCategory;
    private boolean m_colTransactor;
    private boolean m_colTransactorCategory;
    private boolean m_colDateAdded;
    private boolean m_colDatePaid;
    private boolean m_colPaymentMethod;
    private boolean m_colPaymentMethodCategory;

    // Members & constructor ---------------------------------------------------
    public TablePanel(ArrayList<Transaction> data) {
        m_data = data;

        // TEMPORARY: setting which columns (later: passed as parameters)
        m_colDescription = true;
        m_colPrice = true;
        m_colCategory = true;
        m_colTransactor = false;
        m_colTransactorCategory = false;
        m_colDateAdded = false;
        m_colDatePaid = false;
        m_colPaymentMethod = false;
        m_colPaymentMethodCategory = false;
        // END TEMPORARY

        createComponents();
        setPreferences();
        setActions();
        createUI();
    }

    // Private functions -------------------------------------------------------
    /**
     * Initialize all members.
     */
    private void createComponents() {
        m_table = new JTable(createTableFromData());
        m_scrollPane = new JScrollPane(m_table);
        m_table.setFillsViewportHeight(true);

        // Make columns sortable
        TableRowSorter<TableModel> rowSorter = new TableRowSorter(m_table.getModel());
        rowSorter.setComparator(0, new CustomComparator());
        rowSorter.setComparator(1, new CustomComparator());
        rowSorter.setComparator(2, new CustomComparator());
        m_table.setRowSorter(rowSorter);

    }

    /**
     * Set layout-related preferences for the frame.
     */
    private void setPreferences() {
        m_table.getColumnModel().getColumn(1).setCellRenderer(new PriceRenderer());
    }

    /**
     * Set actions for members of the frame.
     */
    private void setActions() {

    }

    /**
     * Add members to the frame, using layout managers.
     */
    private void createUI() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        this.add(m_scrollPane, c);
    }

    private TableModel createTableFromData() {
        // TEMPORARY: hard coded columns
        String[] columns = {"Description", "Price", "Category"};
        // END TEMPORARY

        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);

        for (Transaction t : m_data) {
            ArrayList<Object> a = new ArrayList();
            a.add(t.getDescription());
            //a.add(new Price(t.getPrice()));
            a.add(t.getPrice());
            a.add(t.getCategory());

            tableModel.addRow(a.toArray());
        }

        return tableModel;
    }

    // Public functions --------------------------------------------------------
    // Private classes ---------------------------------------------------------
    /**
     * Compare two objects as numbers if they are numeric values. Otherwise,
     * compare their string-representation.
     */
    private static class CustomComparator implements Comparator {

        @Override
        public int compare(Object a, Object b) {
            if (a.getClass().equals(Double.class)) {
                return ((Double) a).compareTo((Double) b);
            } else {
                return a.toString().compareTo(b.toString());
            }
        }
    }

    private static class PriceRenderer extends DefaultTableCellRenderer {

        public PriceRenderer() {
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
}
