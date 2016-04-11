package view;

import java.awt.*;
import javax.swing.*;

/**
 * This panel fills one of the tabs of the MainWindow. It contains a table with
 * all transactions that are not paid back yet, grouped by transactor.
 *
 * It asks for the data itself, but loads this data only in the constructor.
 *
 * @author Anaïs Ools
 */
public class LoansPanel extends JPanel {

    // Members & constructor ---------------------------------------------------
    public LoansPanel() {
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

    }

    /**
     * Set layout-related preferences for the frame.
     */
    private void setPreferences() {
        this.setBackground(Color.orange);
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

    }

    // Public functions --------------------------------------------------------
}
