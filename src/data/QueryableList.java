package data;

import java.util.*;
import model.Transaction;

/**
 * Class extends a list of Transactions, adding functions to query Transactions.
 * The class is specifically made to query the Transaction object.
 *
 * @author Anaïs Ools
 */
public class QueryableList extends Observable implements Iterable<Transaction> {

    // Members -----------------------------------------------------------------
    private final ArrayList<Transaction> m_list;

    public QueryableList() {
        m_list = new ArrayList();
    }

    // Private functions -------------------------------------------------------
    /**
     * Tell observers that the data has changed.
     */
    private void notifyObserversOfChange() {
        this.setChanged();
        this.notifyObservers();
    }

    // Public functions --------------------------------------------------------
    /**
     * Add an object to the list of observers to be notified when something in
     * the data changes.
     *
     * @param o
     */
    public void addAsObserver(Observer o) {
        this.addObserver(o);
    }

    public void add(Transaction t) {
        m_list.add(t);
    }

    public Transaction get(int index) {
        return m_list.get(index);
    }

    public Transaction get(long id) {
        for (Transaction t : m_list) {
            if (t.getID() == id) {
                return t;
            }
        }
        return null;
    }

    @Override
    public Iterator<Transaction> iterator() {
        return m_list.iterator();
    }

    /**
     * Cast the QueryableList to a List object.
     *
     * @return List version of the object.
     */
    public ArrayList<Transaction> toList() {
        return m_list;
    }

}
