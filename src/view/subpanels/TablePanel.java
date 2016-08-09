package view.subpanels;

import data.Data;
import java.awt.*;
import java.util.*;
import javafx.util.Pair;
import javax.swing.*;
import javax.swing.table.*;
import model.Transaction;
import model.Transaction.TRANSACTIONFIELD;
import view.swingextensions.*;

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
    private final ArrayList<Pair<String, TRANSACTIONFIELD>> m_columns;

    private JTable m_table;
    private JScrollPane m_scrollPane;

    // Constructors ------------------------------------------------------------
    public TablePanel(ArrayList<Pair<String, TRANSACTIONFIELD>> columns) {
        m_data = new ArrayList();
        m_columns = columns;

        createComponents();
        setPreferences();
        setActions();
        createUI();
    }

    public TablePanel(ArrayList<Transaction> data, ArrayList<Pair<String, TRANSACTIONFIELD>> columns) {
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
        //m_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m_table.removeColumn(m_table.getColumn("ID")); // hide ID column
        m_scrollPane = new JScrollPane(m_table);
        m_table.setFillsViewportHeight(true);

        // Make columns sortable
        TableRowSorter<TableModel> rowSorter = new TableRowSorter(m_table.getModel());
        for (int i = 0; i < m_table.getModel().getColumnCount(); i++) {
            rowSorter.setComparator(i, new TableCellComparator());
        }
        m_table.setRowSorter(rowSorter);
    }

    /**
     * Set layout-related preferences for the frame.
     */
    private void setPreferences() {
        m_table.setDefaultRenderer(Object.class, new PaddingTableCellRenderer());
        for (Pair<String, TRANSACTIONFIELD> p : m_columns) {
            switch (p.getValue()) {
                case DESCRIPTION:
                    getColumn(p.getKey()).setPreferredWidth(250);
                    getColumn(p.getKey()).setMinWidth(100);
                    break;
                case PRICE:
                    getColumn(p.getKey()).setCellRenderer(new PriceTableCellRenderer());
                    getColumn(p.getKey()).setPreferredWidth(90);
                    getColumn(p.getKey()).setMinWidth(80);
                    getColumn(p.getKey()).setMaxWidth(120);
                    break;
                case DATEADDED:
                case DATEPAID:
                    getColumn(p.getKey()).setCellRenderer(new DateTableCellRenderer());
                    break;
                case EXCEPTIONAL:
                    getColumn(p.getKey()).setPreferredWidth(80);
                    getColumn(p.getKey()).setMinWidth(80);
                    getColumn(p.getKey()).setMaxWidth(80);
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
    private DefaultTableModel createTableFromData() {
        // Create headers
        String[] columnNames = new String[m_columns.size() + 1];
        columnNames[0] = "ID";
        for (int i = 0; i < m_columns.size(); i++) {
            columnNames[i + 1] = m_columns.get(i).getKey();
        }
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // make cells uneditable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                String columnName = this.getColumnName(columnIndex);
                if (columnName.equals("Exceptional")) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };

        // Create data
        insertData(tableModel);

        return tableModel;
    }

    /**
     * Inserts the data from the m_data member into the given TableModel.
     *
     * @param tm
     */
    private void insertData(DefaultTableModel tm) {
        for (Transaction t : m_data) {
            ArrayList<Object> dataObject = new ArrayList();
            dataObject.add(t.getID());
            for (Pair<String, TRANSACTIONFIELD> p : m_columns) {
                TRANSACTIONFIELD fieldType = p.getValue();
                Object field = t.get(fieldType);
                if (field != null && field.getClass().equals(model.CategoryString.class)) {
                    dataObject.add(((model.CategoryString) field).getValue());
                } else {
                    dataObject.add(field);
                }
            }
            tm.addRow(dataObject.toArray());
        }
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

    /**
     * Update the table to the m_data member, in case this has changed.
     */
    private void updateData() {
        DefaultTableModel tm = (DefaultTableModel) m_table.getModel();
        for (int i = tm.getRowCount() - 1; i >= 0; i--) {
            tm.removeRow(i);
        }
        insertData(tm);
    }

    // Public functions --------------------------------------------------------
    public void setData(ArrayList<Transaction> data) {
        m_data = data;
        updateData();
    }

    /**
     * Return the transaction of a selected row. Return null if no row is
     * selected.
     *
     * @return
     */
    public Transaction getSelectedTransaction() {
        int selectedRow = m_table.getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }
        int selectedModelRow = m_table.convertRowIndexToModel(selectedRow);
        long id = (long) m_table.getModel().getValueAt(selectedModelRow, 0);
        return Data.GetInstance().getTransactions().get(id);
    }
}
