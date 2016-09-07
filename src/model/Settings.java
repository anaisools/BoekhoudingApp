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

    private String m_valutaSign;
    private boolean m_valutaSignInFront;
    private boolean m_valutaCommaSeparator;
    private boolean m_valutaThousandSeparator;

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
            m_valutaSign = "€";
            m_valutaSignInFront = true;
            m_valutaCommaSeparator = true;
            m_valutaThousandSeparator = true;
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

    /**
     * Converts a number to a valuta representation, according to the user's
     * settings.
     *
     * @param price the number to convert
     * @return the valuta string representation of the number
     */
    public String convertPriceToString(double price) {
        String p = String.format("%,10.2f", price).trim();
        if (m_valutaThousandSeparator) {
            p = p.replace(".", " ");
        }
        if (m_valutaCommaSeparator) {
            p = p.replace(".", ",");
        } else {
            p = p.replace(",", ".");
        }
        if (m_valutaSignInFront) {
            p = m_valutaSign + "  " + p;
        } else {
            p = p + " " + m_valutaSign;
        }
        return p;
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
