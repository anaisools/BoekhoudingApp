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
        //m_table.setDefaultRenderer(Object.class, new MyRenderer());
        //TableColumnModel m = m_table.getColumnModel();
        //m.getColumn(1).setCellRenderer(new MyRenderer());
        //m_table.getColumnModel().getColumn(1).setCellRenderer(new MyRenderer());
        m_table.setAutoCreateRowSorter(true);
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

    /**
     * Converts a double to a string in this format: 5.937 -> € 5,94.
     *
     * @param price the double to convert
     * @return the converted string
     */
    private String priceToString(double price) {
        String result = String.format("%10.2f", (price));
        result = result.trim();
        return "€ " + result.replace('.', ',');
    }

    // Public functions --------------------------------------------------------
    // Private classes ---------------------------------------------------------
    private static class PriceRenderer extends DefaultTableCellRenderer {

        public PriceRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        public void setValue(Object value) {
            System.out.println("Type: " + value.getClass() + " " + value);
            //String result = String.format("%10.2f", value);
            //result = result.trim();
            //value = "€  " + result.replace('.', ',');

            super.setValue(value);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            //System.out.println("Object type: " + value.getClass());
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    public static class Test extends TableRowSorter {

    }

    /**
     * Use a formatter to format the cell Object
     *
     * Source: tips4java.wordpress.com/2008/10/11/table-format-renderers/
     */
    private static class FormatRenderer extends DefaultTableCellRenderer {

        private final Format formatter;

        /**
         * Use the specified formatter to format the Object
         *
         * @param formatter
         */
        public FormatRenderer(Format formatter) {
            this.formatter = formatter;
        }

        @Override
        public void setValue(Object value) {
            //  Format the Object before setting its value in the renderer
            try {
                if (value != null) {
                    value = formatter.format(value);
                }
            } catch (IllegalArgumentException e) {
            }
            super.setValue(value);
        }

        /**
         * Use the default date/time formatter for the default locale
         *
         * @return
         */
        public static FormatRenderer getDateTimeRenderer() {
            return new FormatRenderer(DateFormat.getDateTimeInstance());
        }

        /**
         * Use the default time formatter for the default locale
         *
         * @return
         */
        public static FormatRenderer getTimeRenderer() {
            return new FormatRenderer(DateFormat.getTimeInstance());
        }
    }

    /**
     * Source: tips4java.wordpress.com/2008/10/11/table-format-renderers/
     */
    private static class NumberRenderer extends FormatRenderer {

        /**
         * Use the specified number formatter and right align the text
         *
         * @param formatter
         */
        public NumberRenderer(NumberFormat formatter) {
            super(formatter);
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        /**
         * Use the default currency formatter for the default locale
         *
         * @return
         */
        public static NumberRenderer getCurrencyRenderer() {
            return new NumberRenderer(NumberFormat.getCurrencyInstance());
        }

        /**
         * Use the default integer formatter for the default locale
         *
         * @return
         */
        public static NumberRenderer getIntegerRenderer() {
            return new NumberRenderer(NumberFormat.getIntegerInstance());
        }

        /**
         * Use the default percent formatter for the default locale
         *
         * @return
         */
        public static NumberRenderer getPercentRenderer() {
            return new NumberRenderer(NumberFormat.getPercentInstance());
        }
    }

    private static class Price implements Comparable {

        private final double m_price;
        private String m_priceString;

        public Price(double price) {
            m_price = price;
            createPriceString();
        }

        public double getDouble() {
            return m_price;
        }

        public String getString() {
            return m_priceString;
        }

        private void createPriceString() {
            m_priceString = String.format("%10.2f", (m_price));
            m_priceString = m_priceString.trim();
            m_priceString = "€ " + m_priceString.replace('.', ',');
        }

        @Override
        public int compareTo(Object t) {
            System.out.println("Comparing!");
            return ((Double) m_price).compareTo((Double) t);
        }

        @Override
        public String toString() {
            return m_priceString;
        }

    }
}
