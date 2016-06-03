package view.subpanels;

import data.QueryableList;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.*;
import java.util.*;
import javafx.util.Pair;
import javax.swing.*;
import javax.swing.table.*;
import view.HistoryPanel;
import view.swingextensions.*;

/**
 * This panel creates an overview of a transaction dataset. It calculates the
 * sum per year and month.
 *
 * The dataset is passed along from the parent and can be updated.
 *
 * @author Anaïs Ools
 */
public class OverviewPanel extends JPanel {

    private QueryableList m_data;
    private int m_year;
    private final HistoryPanel m_parent;

    private JLabel m_title;
    private SmallTable<String, Double> m_yearTable;
    private SmallTable<Date, Double> m_monthTable;
    private SmallTable<String, Double> m_monthAvgTable;
    private JCheckBox m_hideExceptional;
    private JCheckBox m_useDateAdded;

    // Constructors ------------------------------------------------------------
    public OverviewPanel(HistoryPanel parent) {
        m_data = null;
        m_parent = parent;
        createComponents();
        setPreferences();
        setActions();
        createUI();
    }

    public OverviewPanel(HistoryPanel parent, QueryableList data) {
        m_data = data;
        m_parent = parent;
        createComponents();
        setPreferences();
        setActions();
        createUI();
        recalculate();
    }

    // Private functions -------------------------------------------------------
    /**
     * Initialize all members.
     */
    private void createComponents() {

        m_title = new JLabel("Overview");

        // year table
        m_yearTable = new SmallTable("Year", "Total", false);
        m_yearTable.add("year", 0.0);

        // month table
        m_monthTable = new SmallTable("Month", "Total", true);
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            m_monthTable.add(df.parse("01/01/2016"), 0.0);
            m_monthTable.add(df.parse("01/02/2016"), 0.0);
            m_monthTable.add(df.parse("01/03/2016"), 0.0);
            m_monthTable.add(df.parse("01/04/2016"), 0.0);
            m_monthTable.add(df.parse("01/05/2016"), 0.0);
            m_monthTable.add(df.parse("01/06/2016"), 0.0);
            m_monthTable.add(df.parse("01/07/2016"), 0.0);
            m_monthTable.add(df.parse("01/08/2016"), 0.0);
            m_monthTable.add(df.parse("01/09/2016"), 0.0);
            m_monthTable.add(df.parse("01/10/2016"), 0.0);
            m_monthTable.add(df.parse("01/11/2016"), 0.0);
            m_monthTable.add(df.parse("01/12/2016"), 0.0);
        } catch (ParseException ex) {
            System.out.println("Could not parse dates in Overview Panel.");
        }

        // month average table
        m_monthAvgTable = new SmallTable("Avg/Month", "Total", false);
        m_monthAvgTable.add("Average", 0.0);

        m_hideExceptional = new JCheckBox("Hide exceptional");
        m_useDateAdded = new JCheckBox("Use date added ");
    }

    /**
     * Set layout-related preferences for the frame.
     */
    private void setPreferences() {
        //this.setBackground(Color.pink);
        m_title.setFont(new Font("Serif", Font.PLAIN, 24));
    }

    /**
     * Set actions for members of the frame.
     */
    private void setActions() {
        m_hideExceptional.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent ie) {
                m_parent.update(null, null);
            }
        });
        m_useDateAdded.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent ie) {
                m_parent.update(null, null);
            }
        });
    }

    /**
     * Add members to the frame, using layout managers.
     */
    private void createUI() {
        Insets in = new Insets(20, 20, 20, 20); // top left bottom right

        addWithGridBagConstraints(this, false, true, in, 0, 0, 1, 0, m_title);
        addWithGridBagConstraints(this, false, true, in, 0, 1, 1, 0, m_yearTable);
        addWithGridBagConstraints(this, false, true, in, 0, 2, 1, 0, m_monthTable);
        addWithGridBagConstraints(this, false, true, in, 0, 3, 1, 0, m_monthAvgTable);

        addWithGridBagConstraints(this, false, false, new Insets(20, 20, 5, 20), 0, 4, 0, 0, m_hideExceptional);
        addWithGridBagConstraints(this, false, false, new Insets(5, 20, 20, 20), 0, 5, 0, 0, m_useDateAdded);
    }

    /**
     * Recalculate the statistics and fill the panel.
     */
    private void recalculate() {
        // update year
        m_yearTable.editLeftValue(0, String.valueOf(m_year));
        m_yearTable.editRightValue(0, m_data.getTotalPrice());

        // update month
        double[] months = new double[12];
        for (int i = 0; i < 12; i++) {
            if (usingDateAdded()) {
                months[i] = m_data.selectDateAddedByMonth(i).getTotalPrice();
            } else {
                months[i] = m_data.selectDatePaidByMonth(i).getTotalPrice();
            }
            m_monthTable.editRightValue(i, months[i]);
        }

        // update month average
        double average = 0.0;
        for (double d : months) {
            average += d;
        }
        average /= 12;
        m_monthAvgTable.editRightValue(0, average);
    }

    /**
     * Add a component to a JPanel with certain layout parameters.
     *
     * @param panel
     * @param hFill
     * @param vFill
     * @param in
     * @param x
     * @param y
     * @param xFill
     * @param yFill
     * @param o the component to add
     */
    private void addWithGridBagConstraints(JPanel panel, boolean hFill, boolean vFill, Insets in, int x, int y, int xFill, int yFill, Component o) {
        if (panel.getLayout().getClass() != GridBagLayout.class) {
            panel.setLayout(new GridBagLayout());
        }
        GridBagConstraints c = new GridBagConstraints();
        if (hFill && vFill) {
            c.fill = GridBagConstraints.BOTH;
        } else if (hFill) {
            c.fill = GridBagConstraints.HORIZONTAL;
        } else if (vFill) {
            c.fill = GridBagConstraints.VERTICAL;
        }
        c.insets = in;
        c.gridx = x;
        c.gridy = y;
        c.weightx = xFill;
        c.weighty = yFill;
        panel.add(o, c);
    }

    // Public functions --------------------------------------------------------
    /**
     * Change the data, setData the panel.
     *
     * @param data the new data
     */
    public void setData(QueryableList data) {
        m_data = data;
        recalculate();
    }

    /**
     * Set the year that the panel displays.
     *
     * @param year
     */
    public void setYear(int year) {
        m_year = year;
    }

    /**
     * Check if the displayed data should display exceptional entries or not.
     *
     * @return
     */
    public boolean hidingExceptional() {
        return m_hideExceptional.isSelected();
    }

    /**
     * Check if the displayed data should display exceptional entries or not.
     *
     * @return
     */
    public boolean usingDateAdded() {
        return m_useDateAdded.isSelected();
    }

    // Private classes ---------------------------------------------------------
    private class SmallTable<T, S> extends JPanel {

        private final ArrayList<Pair<T, S>> m_data;
        private JTable m_table;
        private DefaultTableModel m_tableModel;

        public SmallTable(String header1, String header2, boolean firstColumnIsMonth) {
            m_data = new ArrayList();
            createTable(header1, header2);
            setTablePreferences(firstColumnIsMonth);
            createUI();
        }

        private void createTable(String header1, String header2) {
            m_tableModel = new DefaultTableModel(new String[]{header1, header2}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // make cells uneditable
                }
            };
            m_table = new JTable(m_tableModel);
        }

        private void setTablePreferences(boolean firstColumnIsMonth) {
            m_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            m_table.setRowHeight(19);
            m_table.setDefaultRenderer(Object.class, new PaddingTableCellRenderer());
            m_table.getColumn(m_table.getColumnName(1)).setCellRenderer(new PriceTableCellRenderer());
            if (firstColumnIsMonth) {
                m_table.getColumn(m_table.getColumnName(0)).setCellRenderer(new PaddingTableCellRenderer() {
                    @Override
                    public void setValue(Object value) {
                        if (value.getClass().equals(Date.class)) {
                            DateFormat df = new SimpleDateFormat("MMMM");
                            value = df.format((Date) value);
                        }
                        super.setValue(value);
                    }
                });
            }
            TableRowSorter<TableModel> rowSorter = new TableRowSorter(m_table.getModel());
            for (int i = 0; i < m_table.getModel().getColumnCount(); i++) {
                rowSorter.setComparator(i, new TableCellComparator());
            }
            m_table.setRowSorter(rowSorter);
        }

        private void createUI() {
            JScrollPane jsp = new JScrollPane(m_table);
            m_table.setFillsViewportHeight(true);
            this.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1;
            c.weighty = 1;
            this.add(jsp, c);
            this.setPreferredSize(new Dimension(200, (50 - 19) + 19 * m_data.size()));
            this.setMinimumSize(this.getPreferredSize());
        }

        public void add(T t, S s) {
            m_data.add(new Pair(t, s));
            fillTable();
        }

        public void editLeftValue(int index, T t) {
            Pair p = new Pair(t, m_data.get(index).getValue());
            m_data.remove(index);
            m_data.add(index, p);
            fillTable();
        }

        public void editRightValue(int index, S s) {
            Pair p = new Pair(m_data.get(index).getKey(), s);
            m_data.remove(index);
            m_data.add(index, p);
            fillTable();
        }

        private void fillTable() {
            for (int i = m_tableModel.getRowCount() - 1; i >= 0; i--) {
                m_tableModel.removeRow(i);
            }
            for (Pair<T, S> p : m_data) {
                m_tableModel.addRow(new Object[]{p.getKey(), p.getValue()});
            }
            this.setPreferredSize(new Dimension(200, (50 - 19) + 19 * m_data.size()));
            this.setMinimumSize(this.getPreferredSize());
        }
    }
}
