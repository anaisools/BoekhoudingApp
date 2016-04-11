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
    private boolean m_colDateAdded;
    private boolean m_colDatePaid;
    private boolean m_colPaymentMethod;

    // Members & constructor ---------------------------------------------------
    public TablePanel(ArrayList<Transaction> data) {
        m_data = data;

        // TEMPORARY: setting which columns (later: passed as parameters)
        m_colDescription = true;
        m_colPrice = true;
        m_colCategory = true;
        m_colTransactor = false;
        m_colDateAdded = false;
        m_colDatePaid = false;
        m_colPaymentMethod = false;
        // END TEMPORARY

        createComponents();
        setPreferences();
        setActions();
        createUI();
    }

    public TablePanel(ArrayList<Transaction> data, boolean description, boolean price, boolean category, boolean transactor, boolean dateAdded, boolean datePaid, boolean paymentMethod) {
        m_data = data;
        m_colDescription = description;
        m_colPrice = price;
        m_colCategory = category;
        m_colTransactor = transactor;
        m_colDateAdded = dateAdded;
        m_colDatePaid = datePaid;
        m_colPaymentMethod = paymentMethod;

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
        for (int i = 0; i < m_table.getModel().getColumnCount(); i++) {
            rowSorter.setComparator(i, new CustomComparator());
        }
        m_table.setRowSorter(rowSorter);
    }

    /**
     * Set layout-related preferences for the frame.
     */
    private void setPreferences() {
        if (m_colPrice) {
            m_table.getColumnModel().getColumn(getColumnIndex("Price")).setCellRenderer(new PriceRenderer());
        }
        if (m_colDateAdded) {
            m_table.getColumnModel().getColumn(getColumnIndex("Date added")).setCellRenderer(new DateRenderer());
        }
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
        ArrayList<String> columns = new ArrayList();
        if (m_colDescription) {
            columns.add("Description");
        }
        if (m_colPrice) {
            columns.add("Price");
        }
        if (m_colCategory) {
            columns.add("Category");
        }
        if (m_colTransactor) {
            columns.add("Transactor");
        }
        if (m_colDateAdded) {
            columns.add("Date added");
        }
        if (m_colDatePaid) {
            columns.add("Date paid");
        }
        if (m_colPaymentMethod) {
            columns.add("Payment method");
        }
        DefaultTableModel tableModel = new DefaultTableModel(columns.toArray(new String[0]), 0);

        for (Transaction t : m_data) {
            ArrayList<Object> dataObject = new ArrayList();
            if (m_colDescription) {
                dataObject.add(t.getDescription());
            }
            if (m_colPrice) {
                dataObject.add(t.getPrice());
            }
            if (m_colCategory) {
                dataObject.add(t.getCategory());
            }
            if (m_colTransactor) {
                dataObject.add(t.getTransactor());
            }
            if (m_colDateAdded) {
                dataObject.add(t.getDateAdded());
            }
            if (m_colDatePaid) {
                dataObject.add(t.getDatePaid());
            }
            if (m_colPaymentMethod) {
                dataObject.add(t.getPaymentMethod());
            }
            tableModel.addRow(dataObject.toArray());
        }
        return tableModel;
    }

    private int getColumnIndex(String header) {
        for (int i = 0; i < m_table.getColumnCount(); i++) {
            if (m_table.getColumnName(i).equals(header)) {
                return i;
            }
        }
        return -1;
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
            } else if (a.getClass().equals(Date.class)) {
                return ((Date) a).compareTo((Date) b);
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

    private static class DateRenderer extends DefaultTableCellRenderer {

        DateFormat df;

        public DateRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
            df = new SimpleDateFormat("dd/MM/yyyy");
        }

        @Override
        public void setValue(Object value) {
            if (value.getClass().equals(Date.class)) {
                value = df.format((Date) value);
            }
            super.setValue(value);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
