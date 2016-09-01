package view;

import data.Data;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import view.subpanels.LoansList;
import view.swingextensions.CustomGridBag;

/**
 * This panel fills one of the tabs of the MainWindow. It contains a table with
 * all transactions that are not paid back yet, grouped by transactor.
 *
 * It asks for the data itself, but loads this data only in the constructor.
 *
 * @author Anaïs Ools
 */
public class LoansPanel extends JPanel implements Observer {

    // Members & constructor ---------------------------------------------------
    private final JFrame m_parentFrame; // needed for opening dialogs that block the frame

    private LoansList m_loans;

    public LoansPanel(JFrame parentFrame) {
        m_parentFrame = parentFrame;

        // make sure view changes when data changes
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
        m_loans = new LoansList();
    }

    /**
     * Set layout-related preferences for the frame.
     */
    private void setPreferences() {
        //this.setBackground(Color.lightGray);
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
        CustomGridBag c = new CustomGridBag();
        c.setInsets(20);

        // Loans
        c.add(this, m_loans, 0, 0, true, true, 0.5, 1);

        // Upcoming
        JPanel temp = new JPanel();
        temp.setBackground(Color.cyan);
        c.add(this, temp, 1, 0);
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
        // TODO: stuff when data changes
    }
}
