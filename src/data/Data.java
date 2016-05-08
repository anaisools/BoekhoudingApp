package data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import model.Transaction;

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

    // Private functions -------------------------------------------------------
    /**
     * Create a new XMLFileParser object.
     */
    private void init() {
        m_loadingDataSucceeded = true;

        XMLFileHandler xfh = new XMLFileHandler("data.xml");
        if (!xfh.success()) {
            m_loadingDataSucceeded = false;
        } else {
            m_transactions = new QueryableList(xfh.getTransactions());
            m_transactions.addAsObserver(this);
        }

        //loadData();
        // TODO: create new XMLFileParser object as member
    }

    /**
     * Get data from XMLFileReader.
     *
     * @return true if success, false otherwise
     */
    private void loadData() {
        // TODO: load from file using XMLFileParser class

        try {
            // TEMPORARY: hardcoded objects created
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

            Transaction t0 = new Transaction(0);
            t0.setDescription("Cadeautje voor Audric");
            t0.setPrice(-30);
            t0.setTransactor("Een winkel", "Winkel");
            t0.setCategory("Cadeau");
            t0.setDateAdded(df.parse("21/01/2016"));
            t0.setDatePaid(df.parse("22/01/2016"));
            t0.setPaymentMethod("Bankkaart", "Bank");
            m_transactions.add(t0);

            Transaction t1 = new Transaction(1);
            t1.setDescription("Zakgeld");
            t1.setPrice(15.92419);
            t1.setTransactor("Mama", "Personen");
            t1.setCategory("Zakgeld");
            t1.setDateAdded(df.parse("19/01/2016"));
            t1.setDatePaid(df.parse("19/01/2016"));
            t1.setPaymentMethod("Overschrijving", "Bank");
            m_transactions.add(t1);

            Transaction t2 = new Transaction(1);
            t2.setDescription("Iets heel duur");
            t2.setPrice(-124.5);
            t2.setTransactor("Iemand", "Personen");
            t2.setCategory("Overig");
            t2.setDateAdded(df.parse("20/01/2015"));
            t2.setDatePaid(df.parse("20/01/2015"));
            t2.setPaymentMethod("Overschrijving", "Bank");
            m_transactions.add(t2);
            // END TEMPORARY

        } catch (ParseException ex) {
            System.out.println("Parsing of date failed");
            System.out.println(ex.getMessage());
            m_loadingDataSucceeded = false;
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
