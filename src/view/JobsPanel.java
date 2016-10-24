package view;

import data.Data;
import view.swingextensions.CustomTable;
import java.awt.*;
import java.util.*;
import javafx.util.Pair;
import javax.swing.*;
import model.CategoryString;
import model.Transaction;
import view.swingextensions.CustomGridBag;

/**
 * This panel fills one of the tabs of the MainWindow. It contains a table with
 * information on job transactions.
 *
 * It asks for the data itself, but loads this data only in the constructor.
 *
 * @author Anaïs Ools
 */
public class JobsPanel extends JPanel implements Observer {

    private JLabel m_title;
    private CustomTable m_table;

    // Members & constructor ---------------------------------------------------
    public JobsPanel() {
        Data.GetInstance().addAsObserver(this);

        createComponents();
        setPreferences();
        setActions();
        createUI();

        addDataToTable();
    }

    // Private functions -------------------------------------------------------
    /**
     * Initialize all members.
     */
    private void createComponents() {
        m_title = new JLabel("Jobs");

        ArrayList<Pair<String, CustomTable.COLUMNTYPE>> columns = new ArrayList();
        columns.add(new Pair("Job description", CustomTable.COLUMNTYPE.STRING));
        columns.add(new Pair("Job date", CustomTable.COLUMNTYPE.DATE));
        columns.add(new Pair("Date paid", CustomTable.COLUMNTYPE.DATE));
        columns.add(new Pair("Job hours", CustomTable.COLUMNTYPE.DOUBLE));
        columns.add(new Pair("Wage/hour", CustomTable.COLUMNTYPE.DOUBLE));
        columns.add(new Pair("Gross", CustomTable.COLUMNTYPE.PRICE));
        columns.add(new Pair("Net", CustomTable.COLUMNTYPE.PRICE));
        columns.add(new Pair("Employer", CustomTable.COLUMNTYPE.STRING));
        columns.add(new Pair("Tax", CustomTable.COLUMNTYPE.PERCENTAGE));
        columns.add(new Pair("Tax price", CustomTable.COLUMNTYPE.PRICE));
        m_table = new CustomTable(columns);
        m_table.setDecimals("Wage/hour", 4);
    }

    /**
     * Set layout-related preferences for the panel.
     */
    private void setPreferences() {
        m_title.setFont(new Font("Serif", Font.PLAIN, 36));
        m_table.setColumnWidth("Job description", 200);
        m_table.setColumnWidth("Employer", 100);
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
        CustomGridBag c = new CustomGridBag();
        c.add(this, m_title, 0, 0, false, false, 1, 0, 15, 10, 10, 10);
        c.add(this, m_table, 0, 1, true, true, 1, 1, 20);
    }

    /**
     * Get the data from the Data component, convert this to something the table
     * will understand and update the table.
     */
    private void addDataToTable() {
        ArrayList<ArrayList<Object>> newData = new ArrayList();
        for (Transaction t : Data.GetInstance().getTransactions().selectJobs().sortByJobDate()) {
            ArrayList<Object> e = new ArrayList();
            e.add(notNull(t.get(Transaction.TRANSACTIONFIELD.DESCRIPTION)));
            e.add(notNull(t.get(Transaction.TRANSACTIONFIELD.JOB_DATE)));
            e.add(notNull(t.get(Transaction.TRANSACTIONFIELD.DATE_PAID)));
            Double hours = (Double) t.get(Transaction.TRANSACTIONFIELD.JOB_HOURS);
            e.add(notNull(hours));
            Double wage = (Double) t.get(Transaction.TRANSACTIONFIELD.JOB_WAGE);
            e.add(notNull(wage));
            Double gross = (hours != null && wage != null) ? ((double) hours * (double) wage) : null;
            e.add(notNull(gross));
            Double net = (Double) t.get(Transaction.TRANSACTIONFIELD.PRICE);
            e.add(notNull(net));
            CategoryString transactor = (CategoryString) t.get(Transaction.TRANSACTIONFIELD.TRANSACTOR);
            e.add(notNull((transactor == null) ? null : transactor.getValue()));
            Double percent = (net != null && gross != null) ? (1.0 - (net / gross)) : null;
            e.add(notNull(percent));
            Double percentPrice = (percent != null) ? (percent * gross) : null;
            e.add(notNull(percentPrice));
            newData.add(e);
        }
        m_table.setData(newData);
    }

    private Object notNull(Object o) {
        if (o == null) {
            return "";
        }
        return o;
    }

    // Public functions --------------------------------------------------------
    @Override
    public void update(Observable o, Object o1) {
        addDataToTable();
    }
}
