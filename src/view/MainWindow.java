package view;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * The main window of the class is a container with tabs. It manages the
 * switching between tabs, but not the content of those.
 *
 * @author Anaïs Ools
 */
public class MainWindow extends JFrame {

    // Members & constructor ---------------------------------------------------
    private JTabbedPane m_tabs;
    private JLabel m_historyTab;
    private JLabel m_statsTab;
    private JLabel m_loansTab;
    private JPanel m_historyPanel;
    private JPanel m_statsPanel;
    private JPanel m_loansPanel;

    private JMenuBar m_menuBar;

    public MainWindow() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            System.out.println("Something went wrong");
            System.out.println(ex.getMessage());
        }

        this.setTitle("Boekhouding applicatie");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        // Create tabbedPane
        m_tabs = new JTabbedPane();
        m_tabs.setTabPlacement(JTabbedPane.LEFT);

        // Create content panels for the tabs
        m_historyPanel = new HistoryPanel(this);
        m_statsPanel = new StatsPanel();
        m_loansPanel = new LoansPanel();

        // Add content panels to tabbedPane
        m_tabs.add(m_historyPanel, 0);
        m_tabs.add(m_statsPanel, 1);
        m_tabs.add(m_loansPanel, 2);

        // Create labels for the tabs
        int iconWidth = 90, iconHeight = 90;
        m_historyTab = new JLabel("History");
        m_historyTab.setIcon(getScaledImageIcon("LogIcon.png", iconWidth, iconHeight));
        m_historyTab.setHorizontalTextPosition(JLabel.CENTER);
        m_historyTab.setVerticalTextPosition(JLabel.BOTTOM);
        m_statsTab = new JLabel("Statistics");
        m_statsTab.setIcon(getScaledImageIcon("StatistiekIcon.png", iconWidth, iconHeight));
        m_statsTab.setHorizontalTextPosition(JLabel.CENTER);
        m_statsTab.setVerticalTextPosition(JLabel.BOTTOM);
        m_loansTab = new JLabel("Loans");
        m_loansTab.setIcon(getScaledImageIcon("NogNietBetaaldIcon.png", iconWidth, iconHeight));
        m_loansTab.setHorizontalTextPosition(JLabel.CENTER);
        m_loansTab.setVerticalTextPosition(JLabel.BOTTOM);

        // Add labels to tabbedPane
        m_tabs.setTabComponentAt(0, m_historyTab);
        m_tabs.setTabComponentAt(1, m_statsTab);
        m_tabs.setTabComponentAt(2, m_loansTab);

        // Create menu bar
        m_menuBar = new JMenuBar();
        m_menuBar.add(new JMenu("Temp menu"));
    }

    /**
     * Set layout-related preferences for the frame.
     */
    private void setPreferences() {
        // Frame size
        this.setPreferredSize(new Dimension(1000, 600));
        this.setMinimumSize(this.getPreferredSize());

        // Center frame on screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getPreferredSize().width / 2, dim.height / 2 - this.getPreferredSize().height / 2);

        // Padding for tabs
        int top = 5, left = 5, bottom = 10, right = 5;
        m_historyTab.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        m_statsTab.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        m_loansTab.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
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
        this.getContentPane().add(m_tabs);
        this.setJMenuBar(m_menuBar);
    }

    /**
     * Load an image from the /img source folder and resize it.
     *
     * @param imageName the name of the image inside the img folder
     * @param w the preferred width of the image
     * @param h the preferred height of the image
     * @return the loaded and resized image
     */
    private ImageIcon getScaledImageIcon(String imageName, int w, int h) {
        ImageIcon icon = new ImageIcon(getClass().getResource("/img/" + imageName));
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(icon.getImage(), 0, 0, w, h, null);
        g2.dispose();
        return new ImageIcon(resizedImg);
    }

    // Public functions --------------------------------------------------------
    public void showFrame() {
        this.pack();
        this.setVisible(true);
    }
}
