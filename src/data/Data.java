package data;

import java.util.*;
import model.Transaction;

/**
 * Singleton class: only one instance exists. This class loads the data, using
 * the XMLFileReader class into an in-memory list of Transaction objects. This
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
    private final QueryableList m_transactions;

    // Private functions -------------------------------------------------------
    /**
     * Create a new XMLFileReader object.
     */
    private void init() {
        // TODO: create new XMLFileReader object as member
    }

    /**
     * Tell observers that the data has changed.
     */
    private void notifyObserversOfChange() {
        this.setChanged();
        this.notifyObservers();
    }

    // Public functions --------------------------------------------------------
    /**
     * Get data from XMLFileReader.
     *
     * @return true if success, false otherwise
     */
    public boolean loadData() {
        // TODO: load from file using XMLFileReader class

        // TEMPORARY: hardcoded objects created
        Transaction t0 = new Transaction(0);
        t0.setDescription("Cadeautje voor Audric");
        t0.setPrice(-30);
        t0.setTransactor("Een winkel", "Winkel");
        t0.setCategory("Cadeau");
        t0.setDateAdded(new Date(2016, 1, 21));
        t0.setDatePaid(new Date(2016, 1, 22));
        t0.setPaymentMethod("Bankkaart", "Bank");
        m_transactions.add(t0);

        Transaction t1 = new Transaction(1);
        t1.setDescription("Zakgeld");
        t1.setPrice(15.92419);
        t1.setTransactor("Mama", "Personen");
        t1.setCategory("Zakgeld");
        t1.setDateAdded(new Date(2016, 1, 19));
        t1.setDatePaid(new Date(2016, 1, 19));
        t1.setPaymentMethod("Overschrijving", "Bank");
        m_transactions.add(t1);

        Transaction t2 = new Transaction(1);
        t2.setDescription("Iets heel duur");
        t2.setPrice(-124.5);
        t2.setTransactor("Iemand", "Personen");
        t2.setCategory("Overig");
        t2.setDateAdded(new Date(2016, 1, 20));
        t2.setDatePaid(new Date(2016, 1, 20));
        t2.setPaymentMethod("Overschrijving", "Bank");
        m_transactions.add(t2);
        // END TEMPORARY

        return true;
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
        m_transactions = new QueryableList();
        m_transactions.addAsObserver(this);
        init();
    }

}
