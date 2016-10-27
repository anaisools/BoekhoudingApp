package data;

import java.util.*;
import model.Settings;

/**
 * Singleton class: only one instance exists. This class loads the data, using
 * the XMLFileParser class into an in-memory list of Transaction objects. This
 * list can be queried afterwards.
 *
 * This class is observable. Classes who use the data somewhere should declare
 * themselves observers to this class. They then get notified when something
 * changes.
 *
 * This class is an observer for its own QueryableList.
 *
 * @author Anaïs Ools
 */
public class Data extends Observable implements Observer {

    // Members -----------------------------------------------------------------
    private QueryableList m_transactions;
    private boolean m_loadingDataSucceeded;
    private boolean m_dataHasChanged;

    // Private functions -------------------------------------------------------
    /**
     * Create a new XMLFileParser object.
     */
    private void init() {
        m_loadingDataSucceeded = true;
        m_dataHasChanged = false;

        XMLFileHandler xfh = new XMLFileHandler("data.xml");
        xfh.loadTransactions();
        if (!xfh.success()) {
            m_loadingDataSucceeded = false;
        } else {
            m_transactions = new QueryableList(xfh.getTransactions());
            m_transactions.addAsObserver(this);
        }
    }

    /**
     * Tell observers that the data has changed.
     */
    private void notifyObserversOfChange() {
        this.setChanged();
        this.notifyObservers();
    }

    // Public functions --------------------------------------------------------
    public boolean loadingDataSucceeded() {
        return m_loadingDataSucceeded;
    }

    public QueryableList getTransactions() {
        return m_transactions;
    }

    /**
     * Add an object to the list of observers to be notified when something in
     * the data changes.
     *
     * @param o
     */
    public void addAsObserver(Observer o) {
        this.addObserver(o);
    }

    /**
     * This function is executed when the QueryableList member changes its data.
     *
     * @param o
     * @param o1
     */
    @Override
    public void update(Observable o, Object o1) {
        m_dataHasChanged = true;
        if (Settings.GetInstance().getAutoSave()) {
            saveData();
        } else {
            notifyObserversOfChange();
        }

    }

    /**
     * Returns whether the data has changed since loading it or not.
     *
     * @return true if the data has changed, false if not.
     */
    public boolean dataHasChanged() {
        return m_dataHasChanged;
    }

    /**
     * Saves the data to the XML file.
     */
    public void saveData() {
        XMLFileHandler xfh = new XMLFileHandler("data.xml");
        if (!xfh.success()) {
            m_dataHasChanged = true;
        } else {
            xfh.saveTransactions(m_transactions.sortByDatePaid().toList());
            m_dataHasChanged = false;
        }
        notifyObserversOfChange();
    }

    // Singleton ---------------------------------------------------------------
    private static Data m_instance;

    public static Data GetInstance() {
        if (m_instance == null) {
            m_instance = new Data();
        }
        return m_instance;
    }

    private Data() { // private because singleton
        init();
    }

}
