package model;

/**
 * Singleton class: only one instance exists. This class loads the settings,
 * using the XMLFileParser class. When the settings are changed, they are
 * written to the settings file immediately.
 *
 * @author Anaïs Ools
 */
public class Settings {

    // Members -----------------------------------------------------------------
    private boolean m_maximizeWindow;
    private boolean m_autoSave;
    private boolean m_saveOnClose;
    private boolean m_minimizeToTray;

    // Private functions -------------------------------------------------------
    /**
     * Create a new XMLFileParser object.
     */
    private void init() {
        boolean loadingSucceeded = loadFromFile();
        if (!loadingSucceeded) { // default values
            m_maximizeWindow = false;
            m_autoSave = false;
            m_saveOnClose = false;
            m_minimizeToTray = false;
        }
    }

    private boolean loadFromFile() {
        // TODO: load from file
        return false;
    }

    private void writeToFile() {
        // TODO: write to file
    }

    // Public functions --------------------------------------------------------
    public boolean getMaximizeWindow() {
        return m_maximizeWindow;
    }

    public void setMaximizeWindow(boolean b) {
        m_maximizeWindow = b;
        writeToFile();
    }

    public boolean getAutoSave() {
        return m_autoSave;
    }

    public void setAutoSave(boolean b) {
        m_autoSave = b;
        writeToFile();
    }

    public boolean getSaveOnClose() {
        return m_saveOnClose;
    }

    public void setSaveOnClose(boolean b) {
        m_saveOnClose = b;
        writeToFile();
    }

    public boolean getMinimizeToTray() {
        return m_minimizeToTray;
    }

    public void setMinimizeToTray(boolean b) {
        m_minimizeToTray = b;
        writeToFile();
    }
    // Singleton ---------------------------------------------------------------
    private static Settings m_instance;

    public static Settings GetInstance() {
        if (m_instance == null) {
            m_instance = new Settings();
        }
        return m_instance;
    }

    private Settings() { // private because singleton
        init();
    }

}
