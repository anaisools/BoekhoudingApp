package view.subpanels;

import java.awt.*;
import java.util.*;
import javafx.util.Pair;
import javax.swing.*;
import javax.swing.table.*;
import view.swingextensions.*;

/**
 * This panel contains a table based on statistics. The statistics should be
 * passed on to the table from the parent, and also updated likewise. Tables can
 * contain descriptions (strings) and prices (doubles) and will be formatted
 * accordingly.
 *
 * @author Anaïs Ools
 */
public class StatsTable extends JPanel {

    private ArrayList<ArrayList<Object>> m_data;
    private final ArrayList<Pair<String, COLUMNTYPE>> m_columns;

    private JTable m_table;
    private JScrollPane m_scrollPane;

    public enum COLUMNTYPE {

        STRING, PRICE
    };

    // Constructors ------------------------------------------------------------
    public StatsTable(ArrayList<Pair<String, COLUMNTYPE>> columns) {
        m_data = new ArrayList();
        m_columns = columns;

        createComponents();
        setPreferences();
        setActions();
        createUI();
    }

    public StatsTable(ArrayList<ArrayList<Object>> data, ArrayList<Pair<String, COLUMNTYPE>> columns) {
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
     * Set layout-related preferences for the panel.
     */
    private void setPreferences() {
        m_table.setDefaultRenderer(Object.class, new PaddingTableCellRenderer());
        for (Pair<String, COLUMNTYPE> p : m_columns) {
            switch (p.getValue()) {
                case STRING:
                    //getColumn(p.getKey()).setPreferredWidth(270);
                    break;
                case PRICE:
                    getColumn(p.getKey()).setCellRenderer(new PriceTableCellRenderer(true, true));
                    //getColumn(p.getKey()).setPreferredWidth(90);
                    break;
            }
        }
        //m_table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        m_table.setRowHeight(19);
    }

    /**
     * Set actions for members of the panel.
     */
    private void setActions() {

    }

    /**
     * Add members to the panel, using layout managers.
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
        String[] columnNames = new String[m_columns.size()];
        for (int i = 0; i < m_columns.size(); i++) {
            columnNames[i] = m_columns.get(i).getKey();
        }
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // make cells uneditable
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
        for (ArrayList<Object> a : m_data) {
            tm.addRow(a.toArray());
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
    public void setData(ArrayList<ArrayList<Object>> data) {
        m_data = data;
        updateData();
    }

    public void addEntry(ArrayList<Object> entry) {
        if (m_data != null) {
            m_data.add(entry);
            updateData();
        }
    }
}
