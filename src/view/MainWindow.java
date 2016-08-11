package view;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;
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

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new ShortcutManager());

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

        createMenuBar();
    }

    /**
     * Set layout-related preferences for the frame.
     */
    private void setPreferences() {
        // Frame size
        this.setPreferredSize(new Dimension(1000, 800));
        this.setMinimumSize(this.getPreferredSize());

        // Center frame on screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getPreferredSize().width / 2, dim.height / 2 - this.getPreferredSize().height / 2);

        // Padding for tabs
        int top = 5, left = 5, bottom = 10, right = 5;
        m_historyTab.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        m_statsTab.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        m_loansTab.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));

        // Maximize
        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Set actions for members of the frame.
     */
    private void setActions() {
        this.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent we) {
            }

            @Override
            public void windowClosing(WindowEvent we) {
                // TODO: only do this when autosave is on
                data.Data.GetInstance().saveData();
            }

            @Override
            public void windowClosed(WindowEvent we) {
            }

            @Override
            public void windowIconified(WindowEvent we) {
            }

            @Override
            public void windowDeiconified(WindowEvent we) {
            }

            @Override
            public void windowActivated(WindowEvent we) {
            }

            @Override
            public void windowDeactivated(WindowEvent we) {
            }

        });
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

    /**
     * Create the components of the menu bar.
     */
    private void createMenuBar() {
        m_menuBar = new JMenuBar();
        JMenu menu_file = new JMenu(" File ");
        JMenu menu_preferences = new JMenu(" Preferences ");
        JMenuItem item_save = new JMenuItem("Save");
        JMenuItem item_savequit = new JMenuItem("Save and quit");
        JMenuItem item_exit = new JMenuItem("Exit");

        item_save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        item_save.addActionListener(new DataTriggeredActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                System.out.println("Saving!");
                data.Data.GetInstance().saveData();
            }

            @Override
            public void update(Observable o, Object o1) {
                item_save.setEnabled(data.Data.GetInstance().dataHasChanged());
            }
        });
        item_savequit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                data.Data.GetInstance().saveData();
                System.exit(0);
            }
        });
        item_exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        });

        m_menuBar.add(menu_file);
        menu_file.add(item_save);
        menu_file.add(item_savequit);
        menu_file.addSeparator();
        menu_file.add(item_exit);
        m_menuBar.add(menu_preferences);
    }

    // Public functions --------------------------------------------------------
    public void showFrame() {
        this.pack();
        this.setVisible(true);
    }

    // Private classes ---------------------------------------------------------
    private class ShortcutManager implements KeyEventDispatcher {

        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    data.Data.GetInstance().saveData();
                } else if ((e.getKeyCode() == KeyEvent.VK_F4) && ((e.getModifiers() & KeyEvent.ALT_MASK) != 0)) {
                    // TODO: auto save
                }
            }
            return false;
        }
    }

    private abstract class DataTriggeredActionListener implements ActionListener, Observer {

        public DataTriggeredActionListener() {
            data.Data.GetInstance().addAsObserver(this);
        }

        @Override
        public abstract void actionPerformed(ActionEvent ae);

        @Override
        public abstract void update(Observable o, Object o1);
    }
}
