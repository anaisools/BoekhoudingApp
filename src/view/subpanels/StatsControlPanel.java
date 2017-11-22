package view.subpanels;

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author Anaïs Ools
 */
public class StatsControlPanel extends JPanel {

    // Constructors ------------------------------------------------------------
    public StatsControlPanel() {
        this.add(new JButton("Testinglalalalalala"));
    }

    // Private functions -------------------------------------------------------
    /**
     * Initialize all members.
     */
    private void createComponents() {
        
    }

    /**
     * Set layout-related preferences for the panel.
     */
    private void setPreferences() {
        this.setBackground(Color.yellow);
        this.setPreferredSize(new Dimension(200, 200));
        this.setMinimumSize(this.getPreferredSize());
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
        
    }

    // Public functions --------------------------------------------------------
}
