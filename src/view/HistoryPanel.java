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

    private TablePanel m_tablePanel;
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
        columns.add(new Pair("Date added", TRANSACTIONFIELD.DATEADDED));
        columns.add(new Pair("Date paid", TRANSACTIONFIELD.DATEPAID));
        columns.add(new Pair("Payment method", TRANSACTIONFIELD.PAYMENTMETHOD));
        columns.add(new Pair("Exceptional", TRANSACTIONFIELD.EXCEPTIONAL));
        m_tablePanel = new TablePanel(columns);

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
            model.Transaction t = m_tablePanel.getSelectedTransaction();
            if (t != null) {
                AddEditTransaction dialog = new AddEditTransaction(m_parentFrame, t);
                dialog.show();
                // on approving, the passed transaction will be changed and notify the views all by itself.
            }
        });
        m_deleteButton.addActionListener((ActionEvent ae) -> {
            model.Transaction t = m_tablePanel.getSelectedTransaction();
            if (t != null) {
                Data.GetInstance().getTransactions().delete(t);
            }
        });
    }

    /**
     * Add members to the frame, using layout managers.
     */
    private void createUI() {
        // set topPanel UI
        m_topPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 20, 0, 20);
        m_topPanel.add(m_previousYearButton, c);
        c.gridx = 1;
        m_topPanel.add(m_yearLabel, c);
        c.gridx = 2;
        m_topPanel.add(m_nextYearButton, c);

        // set buttonPanel UI
        m_buttonPanel.setLayout(new GridBagLayout());
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(20, 40, 20, 40);
        m_buttonPanel.add(m_addButton, c);
        c.gridx = 1;
        m_buttonPanel.add(m_editButton, c);
        c.gridx = 2;
        m_buttonPanel.add(m_deleteButton, c);

        // set general UI
        this.setLayout(new GridBagLayout());
        c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.weightx = 1.0;
        c.weighty = 0.1;
        this.add(m_topPanel, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 0.9;
        c.weighty = 0.7;
        c.insets = new Insets(0, 20, 0, 0);
        this.add(m_tablePanel, c);

        c.gridx = 2;
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0.1;
        c.weighty = 0.7;
        c.insets = new Insets(0, 0, 0, 0);
        this.add(m_yearPanel, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        c.weightx = 1.0;
        c.weighty = 0.1;
        this.add(m_buttonPanel, c);
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
