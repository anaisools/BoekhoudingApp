package view;

import java.awt.*;
import java.text.*;
import java.util.*;
import javafx.util.Pair;
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

    private final ArrayList<Transaction> m_data;
    private final ArrayList<Pair<String, COLUMNTYPE>> m_columns;

    private JTable m_table;
    private JScrollPane m_scrollPane;

    public enum COLUMNTYPE {

        DESCRIPTION, PRICE, CATEGORY, TRANSACTOR, DATEADDED, DATEPAID, PAYMENTMETHOD
    };

    // Members & constructor ---------------------------------------------------
    public TablePanel(ArrayList<Transaction> data, ArrayList<Pair<String, COLUMNTYPE>> columns) {
        m_data = data;
        m_columns = columns;

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
        m_table.setDefaultRenderer(Object.class, new PaddingRenderer());
        for (Pair<String, COLUMNTYPE> p : m_columns) {
            switch (p.getValue()) {
                case PRICE:
                    getColumn(p.getKey()).setCellRenderer(new PriceRenderer());
                    break;
                case DATEADDED:
                case DATEPAID:
                    getColumn(p.getKey()).setCellRenderer(new DateRenderer());
                    break;
            }
        }
        //m_table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        m_table.setRowHeight(19);
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

    /**
     * Create a TableModel from the given data and the given list of columns.
     *
     * @return a tablemodel filled with data
     */
    private TableModel createTableFromData() {
        // Create headers
        String[] columnNames = new String[m_columns.size()];
        for (int i = 0; i < m_columns.size(); i++) {
            columnNames[i] = m_columns.get(i).getKey();
        }
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        // Create data
        for (Transaction t : m_data) {
            ArrayList<Object> dataObject = new ArrayList();
            for (Pair<String, COLUMNTYPE> p : m_columns) {
                switch (p.getValue()) {
                    case DESCRIPTION:
                        dataObject.add(t.getDescription());
                        break;
                    case PRICE:
                        dataObject.add(t.getPrice());
                        break;
                    case CATEGORY:
                        dataObject.add(t.getCategory());
                        break;
                    case TRANSACTOR:
                        dataObject.add(t.getTransactor());
                        break;
                    case DATEADDED:
                        dataObject.add(t.getDateAdded());
                        break;
                    case DATEPAID:
                        dataObject.add(t.getDatePaid());
                        break;
                    case PAYMENTMETHOD:
                        dataObject.add(t.getPaymentMethod());
                        break;
                }
            }
            tableModel.addRow(dataObject.toArray());
        }
        return tableModel;
    }

    /**
     * Get a column based on its name.
     *
     * @param header the name of the column
     * @return the index of the column
     */
    private TableColumn getColumn(String columnName) {
        for (int i = 0; i < m_table.getColumnCount(); i++) {
            if (m_table.getColumnName(i).equals(columnName)) {
                return m_table.getColumnModel().getColumn(i);
            }
        }
        return null;
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

    private static class PaddingRenderer extends DefaultTableCellRenderer {

        public PaddingRenderer() {
            // padding
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            return this;
        }
    }

    private static class PriceRenderer extends PaddingRenderer {

        public PriceRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
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

    private static class DateRenderer extends PaddingRenderer {

        DateFormat df;

        public DateRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
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
