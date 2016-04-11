package view;

import data.Data;
import java.awt.*;
import java.util.*;
import javax.swing.*;

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
    private JPanel m_tablePanel;
    private JPanel m_yearPanel;
    private JPanel m_buttonPanel;
    private JPanel m_topPanel;
    private JLabel m_yearLabel;
    private int m_year;

    public HistoryPanel() {
        Data.GetInstance().addAsObserver(this);

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
        // TEMPORARY: create detectable panels
        m_tablePanel = new TablePanel(Data.GetInstance().getTransactions().toList(), true, true, true, true, true, true, true);

        m_yearPanel = new JPanel();
        m_yearPanel.setBackground(Color.cyan);

        m_buttonPanel = new JPanel();
        m_buttonPanel.setBackground(Color.red);
        // END TEMPORARY

        // TEMPORARY: year hardcoded
        m_yearLabel = new JLabel();
        m_topPanel = new JPanel();
        m_topPanel.setBackground(Color.pink);
        setYear(Calendar.getInstance().get(Calendar.YEAR));
        // END TEMPORARY
    }

    /**
     * Set layout-related preferences for the frame.
     */
    private void setPreferences() {
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
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 1.0;
        c.weighty = 0.1;
        m_topPanel.add(m_yearLabel);
        this.add(m_topPanel, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0.6;
        c.weighty = 0.7;
        this.add(m_tablePanel, c);

        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0.4;
        c.weighty = 0.7;
        this.add(m_yearPanel, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.weightx = 1.0;
        c.weighty = 0.2;
        this.add(m_buttonPanel, c);
    }

    private void setYear(int year) {
        m_year = year;
        m_yearLabel.setText(Integer.toString(m_year));

        // TODO: update table and overview
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
        // TODO: update the table
        // TODO: update the year-widget
    }
}
