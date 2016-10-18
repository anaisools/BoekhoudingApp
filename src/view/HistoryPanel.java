package view;

import data.Data;
import data.QueryableList;
import dialogs.AddEditTransaction;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import javafx.util.Pair;
import javax.swing.*;
import model.Transaction.TRANSACTIONFIELD;
import view.subpanels.*;
import view.swingextensions.CustomGridBag;

/**
 * This panel fills one of the tabs of the MainWindow. It contains a table with
 * all transactions on the left, a yearly overview on the right and "add" and
 * "edit" buttons on the bottom.
 *
 * It asks for the data itself, but loads this data only in the constructor.
 *
 * @author Anaïs Ools
 */
public class HistoryPanel extends JPanel implements Observer {

    // Members & constructor ---------------------------------------------------
    private final JFrame m_parentFrame; // needed for opening dialogs that block the frame

    private TransactionTable m_tablePanel;
    private OverviewPanel m_yearPanel;
    private JPanel m_buttonPanel;
    private JPanel m_topPanel;
    private JLabel m_yearLabel;
    private int m_year;
    private QueryableList m_displayedData;

    private JButton m_previousYearButton;
    private JButton m_nextYearButton;

    private JButton m_addButton;
    private JButton m_editButton;
    private JButton m_deleteButton;

    public HistoryPanel(JFrame parentFrame) {
        m_parentFrame = parentFrame;

        // make sure view changes when data changes
        Data.GetInstance().addAsObserver(this);

        createComponents();
        setPreferences();
        setActions();
        createUI();

        // subset data to current year
        Calendar cal = Calendar.getInstance();
        setYear(cal.get(Calendar.YEAR));

        m_tablePanel.scrollDown();
    }

    // Private functions -------------------------------------------------------
    /**
     * Initialize all members.
     */
    private void createComponents() {
        // table panel
        ArrayList<Pair<String, TRANSACTIONFIELD>> columns = new ArrayList();
        columns.add(new Pair("Description", TRANSACTIONFIELD.DESCRIPTION));
        columns.add(new Pair("Price", TRANSACTIONFIELD.PRICE));
        columns.add(new Pair("Category", TRANSACTIONFIELD.CATEGORY));
        columns.add(new Pair("Transactor", TRANSACTIONFIELD.TRANSACTOR));
        columns.add(new Pair("Date added", TRANSACTIONFIELD.DATE_ADDED));
        columns.add(new Pair("Date paid", TRANSACTIONFIELD.DATE_PAID));
        columns.add(new Pair("Payment method", TRANSACTIONFIELD.PAYMENT_METHOD));
        columns.add(new Pair("Exceptional", TRANSACTIONFIELD.EXCEPTIONAL));
        m_tablePanel = new TransactionTable(columns);

        // top panel
        m_topPanel = new JPanel();
        m_yearLabel = new JLabel();
        m_previousYearButton = new JButton("<");
        m_nextYearButton = new JButton(">");

        // year panel
        m_yearPanel = new OverviewPanel(this);

        // button panel
        m_buttonPanel = new JPanel();
        m_addButton = new JButton("Add");
        m_editButton = new JButton("Edit");
        m_deleteButton = new JButton("Delete");
    }

    /**
     * Set layout-related preferences for the frame.
     */
    private void setPreferences() {
        m_yearLabel.setFont(new Font("Serif", Font.PLAIN, 36));
        m_previousYearButton.setBackground(Color.darkGray);
        m_nextYearButton.setBackground(Color.darkGray);
        m_addButton.setBackground(Color.darkGray);
        m_editButton.setBackground(Color.darkGray);
        m_deleteButton.setBackground(Color.darkGray);
    }

    /**
     * Set actions for members of the frame.
     */
    private void setActions() {
        m_previousYearButton.addActionListener((ActionEvent ae) -> {
            this.setYear(m_year - 1);
        });
        m_nextYearButton.addActionListener((ActionEvent ae) -> {
            this.setYear(m_year + 1);
        });
        m_addButton.addActionListener((ActionEvent ae) -> {
            AddEditTransaction dialog = new AddEditTransaction(m_parentFrame);
            dialog.show();
            if (dialog.isApproved() && dialog.getTransaction() != null) {
                Data.GetInstance().getTransactions().add(dialog.getTransaction());
            }
        });
        m_editButton.addActionListener((ActionEvent ae) -> {
            model.Transaction[] t = m_tablePanel.getSelectedTransactions();
            if (t != null && t.length > 0) {
                AddEditTransaction dialog = new AddEditTransaction(m_parentFrame, t[0]);
                dialog.show();
                // on approving, the passed transaction will be changed and notify the views all by itself.
            }
        });
        m_deleteButton.addActionListener((ActionEvent ae) -> {
            model.Transaction[] t = m_tablePanel.getSelectedTransactions();
            if (t != null) {
                for (model.Transaction a : t) {
                    Data.GetInstance().getTransactions().delete(a);
                }

            }
        });
    }

    /**
     * Add members to the frame, using layout managers.
     */
    private void createUI() {
        CustomGridBag c = new CustomGridBag();

        // TopPanel
        c.setInsets(0, 0, 20, 20);
        c.add(m_topPanel, m_previousYearButton, 0, 0);
        c.add(m_topPanel, m_yearLabel, 1, 0);
        c.add(m_topPanel, m_nextYearButton, 2, 0);

        // ButtonPanel
        c.setInsets(20, 20, 40, 40);
        c.add(m_buttonPanel, m_addButton, 0, 0);
        c.add(m_buttonPanel, m_editButton, 1, 0);
        c.add(m_buttonPanel, m_deleteButton, 2, 0);

        // General
        c.setCells(3, 1);
        c.add(this, m_topPanel, 0, 0, true, true, 1, 0.1, 10);
        c.setCells(2, 1);
        c.add(this, m_tablePanel, 0, 1, true, true, 0.9, 0.7, 0, 0, 20, 0);
        c.setCells(1, 1);
        c.add(this, m_yearPanel, 2, 1, true, true, 0.1, 0.7, 0);
        c.setCells(3, 1);
        c.add(this, m_buttonPanel, 0, 2, true, true, 1, 0.1, 0);
    }

    /**
     * Change the year of the displayed data. This changes the data in the table
     * and the overview panel.
     *
     * @param year
     */
    private void setYear(int year) {
        m_year = year;
        m_yearLabel.setText(Integer.toString(m_year));
        update(null, null);
    }

    // Public functions --------------------------------------------------------
    /**
     * Update when the data changes.
     *
     * @param o
     * @param o1
     */
    @Override
    public void update(Observable o, Object o1) {
        if (m_yearPanel.usingDateAdded()) {
            m_displayedData = Data.GetInstance().getTransactions().selectDateAddedByYear(m_year);
        } else {
            m_displayedData = Data.GetInstance().getTransactions().selectDatePaidByYear(m_year);
        }
        // Update the table
        m_tablePanel.setData(m_displayedData.toList());

        // Update the year-widget
        m_yearPanel.setYear(m_year);
        QueryableList yearData = m_displayedData;
        if (m_yearPanel.hidingExceptional()) {
            yearData = yearData.selectUnexceptional();
        }
        m_yearPanel.setData(yearData);
    }
}
