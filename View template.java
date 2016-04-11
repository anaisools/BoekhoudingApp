package view;

import java.awt.*;
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
public class HistoryPanel extends JPanel {

    // Members & constructor ---------------------------------------------------
    public HistoryPanel() {
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
        this.setBackground(Color.green);
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
