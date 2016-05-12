package data;

import java.util.*;
import model.Transaction;

/**
 * Class extends a list of Transactions, adding functions to query Transactions.
 * The class is specifically made to query the Transaction object.
 *
 * @author Anaïs Ools
 */
public class QueryableList extends Observable implements Observer, Iterable<Transaction> {

    // Members & constructors --------------------------------------------------
    private final ArrayList<Transaction> m_list;
    
    public QueryableList() {
        m_list = new ArrayList();
    }
    
    public QueryableList(ArrayList<Transaction> list) {
        m_list = list;
        for (Transaction t : m_list) {
            t.addObserver(this);
        }
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
        t.addAsObserver(this);
        notifyObserversOfChange();
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

    /**
     * Get an ID that has not been assigned to any transaction.
     *
     * @return new ID
     */
    public long getNewID() {
        long id = -1;
        // find current max id
        for (Transaction t : this) {
            if (t.getID() > id) {
                id = t.getID();
            }
        }
        // increment
        return ++id;
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

    /**
     * This function is executed when a transaction in the list changes its
     * data.
     *
     * @param o
     * @param o1
     */
    @Override
    public void update(Observable o, Object o1) {
        notifyObserversOfChange();
    }

    // Query functions ---------------------------------------------------------
    public QueryableList selectDatePaidByYear(int year) {
        QueryableList q = new QueryableList();
        Calendar cal = Calendar.getInstance();
        for (Transaction t : this) {
            if (t.getDatePaid() != null) {
                cal.setTime(t.getDatePaid());
                if (cal.get(Calendar.YEAR) == year) {
                    q.add(t);
                }
            } else {
                cal.setTime(t.getDateAdded());
                if (cal.get(Calendar.YEAR) == year) {
                    q.add(t);
                }
            }
        }
        return q;
    }
    
    public QueryableList selectDatePaidByMonth(int month) {
        QueryableList q = new QueryableList();
        Calendar cal = Calendar.getInstance();
        for (Transaction t : this) {
            if (t.getDatePaid() != null) {
                cal.setTime(t.getDatePaid());
                if (cal.get(Calendar.MONTH) == month) {
                    q.add(t);
                }
            } else {
                cal.setTime(t.getDateAdded());
                if (cal.get(Calendar.MONTH) == month) {
                    q.add(t);
                }
            }
        }
        return q;
    }

    /**
     * Get all distinct values of the categories field. An empty string is
     * added.
     *
     * @return an array with all the distinct categories
     */
    public String[] getDistinctCategories() {
        ArrayList<String> list = new ArrayList();
        list.add("");
        for (Transaction t : this) {
            if (!list.contains(t.getCategory())) {
                list.add(t.getCategory());
            }
        }
        Collections.sort(list);
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * Get all distinct values of the transactors field. An empty string is
     * added.
     *
     * @return an array with all the distinct transactors
     */
    public String[] getDistinctTransactors() {
        ArrayList<String> list = new ArrayList();
        list.add("");
        for (Transaction t : this) {
            String element = t.getTransactorCategory() + " > " + t.getTransactor();
            if (!list.contains(element)) {
                list.add(element);
            }
            if (t.getPaybackTransactor() != null) {
                element = t.getPaybackTransactorCategory() + " > " + t.getPaybackTransactor();
                if (!list.contains(element)) {
                    list.add(element);
                }
            }
        }
        Collections.sort(list);
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * Get all distinct values of the payment methods field. An empty string is
     * added.
     *
     * @return an array with all the distinct payment methods
     */
    public String[] getDistinctPaymentMethods() {
        ArrayList<String> list = new ArrayList();
        list.add("");
        for (Transaction t : this) {
            String element = t.getPaymentMethodCategory() + " > " + t.getPaymentMethod();
            if (!list.contains(element)) {
                list.add(element);
            }
        }
        Collections.sort(list);
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * Calculate the total price of this list.
     *
     * @return
     */
    public double getTotalPrice() {
        double total = 0.0;
        for (Transaction t : this) {
            total += t.getPrice();
        }
        return total;
    }
}
