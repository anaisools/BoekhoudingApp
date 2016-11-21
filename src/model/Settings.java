package model;

import data.XMLFileHandler;
import java.util.*;
import javafx.util.Pair;

/**
 * Singleton class: only one instance exists. This class loads the settings,
 * using the XMLFileParser class. When the settings are changed, they are
 * written to the settings file immediately.
 *
 * @author Anaïs Ools
 */
public class Settings {

    // Members -----------------------------------------------------------------
    private ArrayList<Pair<String, Object>> m_settings;
    private ArrayList<Pair<String, Object>> m_fields;

    // Private functions -------------------------------------------------------
    /**
     * Create a new XMLFileParser object.
     */
    private void init() {
        // set defaults
        m_fields = new ArrayList();
        put(m_fields, "maximizeWindow", true);
        put(m_fields, "autoSave", false);
        put(m_fields, "saveOnClose", true);
        put(m_fields, "minimizeToTray", false);
        put(m_fields, "valutaSign", "€");
        put(m_fields, "valutaSignInFront", true);
        put(m_fields, "valutaCommaSeparator", true);
        put(m_fields, "valutaThousandSeparator", true);
        put(m_fields, "pricesVisible", true);
        put(m_fields, "showHiddenValues", true);
        put(m_fields, "useDateAdded", false);
        put(m_fields, "hideExceptional", false);
        put(m_fields, "saveFileLocation", System.getenv("APPDATA") + "\\GhostApps\\BoekhoudingApp\\");

        // load settings
        m_settings = new ArrayList();
        loadFromFile();

        // if setting was not loaded: complete with defaults
        for (Pair<String, Object> p : m_fields) {
            if (get(m_settings, p.getKey()) == null) {
                put(m_settings, p.getKey(), p.getValue());
            }
        }

        writeToFile();
    }

    private void loadFromFile() {
        XMLFileHandler xfh = new XMLFileHandler("settings.xml");
        xfh.loadSettings();
        if (xfh.success()) {
            for (Pair<String, Object> p : xfh.getContent()) {
                put(m_settings, p.getKey(), p.getValue());
            }
        }
    }

    private void writeToFile() {
        try {
            XMLFileHandler xfh = new XMLFileHandler("settings.xml");
            if (xfh.success()) {
                xfh.saveSettings(m_settings);
            }
        } catch (Exception e) {
        }
    }

    private Object get(ArrayList<Pair<String, Object>> list, String key) {
        for (Pair<String, Object> p : list) {
            if (p.getKey().equals(key)) {
                return p.getValue();
            }
        }
        return null;
    }

    private void put(ArrayList<Pair<String, Object>> list, String key, Object o) {
        for (int i = 0; i < list.size(); i++) {
            Pair<String, Object> p = list.get(i);
            if (p.getKey().equals(key)) {
                list.remove(i);
                list.add(i, new Pair(key, o));
                writeToFile();
                return;
            }
        }
        list.add(new Pair(key, o));
        writeToFile();
    }

    private boolean getBoolean(String field) {
        Object o = get(m_settings, field);
        if (o == null) {
            return (boolean) get(m_fields, field);
        } else if (o.getClass().equals(Boolean.class)) {
            return (boolean) get(m_settings, field);
        } else if (o.getClass().equals(String.class)) {
            return Boolean.parseBoolean((String) o);
        } else {
            return (boolean) get(m_fields, field);
        }
    }

    private String getString(String field) {
        Object o = get(m_settings, field);
        if (o == null) {
            return (String) get(m_fields, field);
        } else if (o.getClass().equals(String.class)) {
            return (String) o;
        } else {
            return (String) get(m_fields, field);
        }
    }

    // Public functions --------------------------------------------------------
    public boolean getMaximizeWindow() {
        return getBoolean("maximizeWindow");
    }

    public void setMaximizeWindow(boolean b) {
        put(m_settings, "maximizeWindow", b);
    }

    public boolean getAutoSave() {
        return getBoolean("autoSave");
    }

    public void setAutoSave(boolean b) {
        put(m_settings, "autoSave", b);
    }

    public boolean getSaveOnClose() {
        return getBoolean("saveOnClose");
    }

    public void setSaveOnClose(boolean b) {
        put(m_settings, "saveOnClose", b);
    }

    public boolean getMinimizeToTray() {
        return getBoolean("minimizeToTray");
    }

    public void setMinimizeToTray(boolean b) {
        put(m_settings, "minimizeToTray", b);
    }

    public boolean getPricesVisible() {
        return getBoolean("pricesVisible");
    }

    public void setPricesVisible(boolean b) {
        put(m_settings, "pricesVisible", b);
    }

    public boolean getShowHiddenValues() {
        return getBoolean("showHiddenValues");
    }

    public void setShowHiddenValues(boolean b) {
        put(m_settings, "showHiddenValues", b);
    }

    public boolean getUseDateAdded() {
        return getBoolean("useDateAdded");
    }

    public void setUseDateAdded(boolean b) {
        put(m_settings, "useDateAdded", b);
    }

    public boolean getHideExceptional() {
        return getBoolean("hideExceptional");
    }

    public void setHideExceptional(boolean b) {
        put(m_settings, "hideExceptional", b);
    }

    public String getSaveFileLocation() {
        return getString("saveFileLocation");
    }

    public void setSaveFileLocation(String s) {
        put(m_settings, "saveFileLocation", s);
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
        if (getBoolean("valutaThousandSeparator")) {
            p = p.replace(".", " ");
        }
        if (getBoolean("valutaCommaSeparator")) {
            p = p.replace(".", ",");
        } else {
            p = p.replace(",", ".");
        }
        if (getBoolean("valutaSignInFront")) {
            p = getString("valutaSign") + "  " + p;
        } else {
            p = p + " " + getString("valutaSign");
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
