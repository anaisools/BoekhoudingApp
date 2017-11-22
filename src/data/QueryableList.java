package data;

import java.util.*;
import model.CategoryString;
import model.CategoryStringComparator;
import model.Transaction;
import model.Transaction.TRANSACTIONFIELD;

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

    public void delete(Transaction t) {
        t.deleteObservers();
        m_list.remove(t);
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

    public int count() {
        return m_list.size();
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
            if (t.get(TRANSACTIONFIELD.DATE_PAID) != null) {
                cal.setTime((Date) t.get(TRANSACTIONFIELD.DATE_PAID));
                if (cal.get(Calendar.YEAR) == year) {
                    q.add(t);
                }
            }
        }
        return q;
    }

    public QueryableList selectDateAddedByYear(int year) {
        QueryableList q = new QueryableList();
        Calendar cal = Calendar.getInstance();
        for (Transaction t : this) {
            if (t.get(TRANSACTIONFIELD.DATE_ADDED) != null) {
                cal.setTime((Date) t.get(TRANSACTIONFIELD.DATE_ADDED));
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
            if (t.get(TRANSACTIONFIELD.DATE_PAID) != null) {
                cal.setTime((Date) t.get(TRANSACTIONFIELD.DATE_PAID));
                if (cal.get(Calendar.MONTH) == month) {
                    q.add(t);
                }
            }
        }
        return q;
    }

    public QueryableList selectDateAddedByMonth(int month) {
        QueryableList q = new QueryableList();
        Calendar cal = Calendar.getInstance();
        for (Transaction t : this) {
            cal.setTime((Date) t.get(TRANSACTIONFIELD.DATE_ADDED));
            if (cal.get(Calendar.MONTH) == month) {
                q.add(t);
            }
        }
        return q;
    }

    /**
     * Get all distinct values of the categories field.
     *
     * @return an array with all the distinct categories
     */
    public String[] getDistinctCategories() {
        ArrayList<String> list = new ArrayList();
        for (Transaction t : this) {
            String element = (String) t.get(TRANSACTIONFIELD.CATEGORY);
            if (!list.contains(element)) {
                list.add(element);
            }
        }
        Collections.sort(list);
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * Get all distinct values of the transactors field.
     *
     * @return an array with all the distinct transactors
     */
    public CategoryString[] getDistinctTransactors() {
        ArrayList<CategoryString> list = new ArrayList();
        for (Transaction t : this) {
            CategoryString element = (CategoryString) t.get(TRANSACTIONFIELD.TRANSACTOR);
            if (!list.contains(element)) {
                list.add(element);
            }
            if (t.get(TRANSACTIONFIELD.PAYBACK_TRANSACTOR) != null) {
                element = (CategoryString) t.get(TRANSACTIONFIELD.PAYBACK_TRANSACTOR);
                if (!list.contains(element)) {
                    list.add(element);
                }
            }
        }
        Collections.sort(list, new CategoryStringComparator());
        return (CategoryString[]) list.toArray(new CategoryString[list.size()]);
    }

    /**
     * Get all distinct values of the payment methods field.
     *
     * @return an array with all the distinct payment methods
     */
    public CategoryString[] getDistinctPaymentMethods() {
        ArrayList<CategoryString> list = new ArrayList();
        for (Transaction t : this) {
            CategoryString element = (CategoryString) t.get(TRANSACTIONFIELD.PAYMENT_METHOD);
            if (!list.contains(element)) {
                list.add(element);
            }
        }
        Collections.sort(list, new CategoryStringComparator());
        return (CategoryString[]) list.toArray(new CategoryString[list.size()]);
    }

    /**
     * Calculate the total price of this list.
     *
     * @return
     */
    public double getTotalPrice() {
        double total = 0.0;
        for (Transaction t : this) {
            total += (double) t.get(TRANSACTIONFIELD.PRICE);
        }
        return total;
    }

    /**
     * Select all transactions that are loans.
     *
     * @return
     */
    public QueryableList getLoans() {
        QueryableList q = new QueryableList();
        for (Transaction t : this) {
            if ((boolean) t.get(TRANSACTIONFIELD.PAYBACK)) {
                q.add(t);
            }
        }
        return q;
    }

    /**
     * Sort this list according to date added.
     *
     * @return the same list, but sorted.
     */
    public QueryableList sortByDateAdded() {
        Collections.sort(m_list, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction o1, Transaction o2) {
                if (o1.get(TRANSACTIONFIELD.DATE_ADDED) != null && o2.get(TRANSACTIONFIELD.DATE_ADDED) != null) {
                    return ((Date) o1.get(TRANSACTIONFIELD.DATE_ADDED)).compareTo((Date) o2.get(TRANSACTIONFIELD.DATE_ADDED));
                } else if (o1.get(TRANSACTIONFIELD.DATE_ADDED) != null) {
                    return -1;
                } else if (o2.get(TRANSACTIONFIELD.DATE_ADDED) != null) {
                    return 1;
                } else {
                    return 1;
                }
            }
        });
        return this;
    }

    /**
     * Sort this list according to date paid.
     *
     * @return the same list, but sorted.
     */
    public QueryableList sortByDatePaid() {
        Collections.sort(m_list, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction o1, Transaction o2) {
                if (o1.get(TRANSACTIONFIELD.DATE_PAID) != null && o2.get(TRANSACTIONFIELD.DATE_PAID) != null) {
                    return ((Date) o1.get(TRANSACTIONFIELD.DATE_PAID)).compareTo((Date) o2.get(TRANSACTIONFIELD.DATE_PAID));
                } else if (o1.get(TRANSACTIONFIELD.DATE_PAID) != null) {
                    return -1;
                } else if (o2.get(TRANSACTIONFIELD.DATE_PAID) != null) {
                    return 1;
                } else {
                    return 1;
                }
            }
        });
        return this;
    }

    /**
     * Sort this list according to job date (if present).
     *
     * @return the same list, but sorted.
     */
    public QueryableList sortByJobDate() {
        Collections.sort(m_list, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction o1, Transaction o2) {
                if (o1.get(TRANSACTIONFIELD.JOB_DATE) != null && o2.get(TRANSACTIONFIELD.JOB_DATE) != null) {
                    return ((Date) o1.get(TRANSACTIONFIELD.JOB_DATE)).compareTo((Date) o2.get(TRANSACTIONFIELD.JOB_DATE));
                } else if (o1.get(TRANSACTIONFIELD.JOB_DATE) != null) {
                    return -1;
                } else if (o2.get(TRANSACTIONFIELD.JOB_DATE) != null) {
                    return 1;
                } else {
                    return 1;
                }
            }
        });
        return this;
    }

    /**
     * Select all transactions that are not exceptional.
     *
     * @return
     */
    public QueryableList selectUnexceptional() {
        QueryableList q = new QueryableList();
        for (Transaction t : this) {
            if (!((boolean) t.get(TRANSACTIONFIELD.EXCEPTIONAL))) {
                q.add(t);
            }
        }
        return q;
    }

    /**
     * Select all transactions that are not hidden.
     *
     * @return
     */
    public QueryableList selectNonhidden() {
        QueryableList q = new QueryableList();
        Date today = new Date();
        for (Transaction t : this) {
            if (((boolean) t.get(TRANSACTIONFIELD.HIDDEN))) {
                Date d = (Date) t.get(TRANSACTIONFIELD.HIDDEN_DATE);
                if (d != null && !d.after(today)) {
                    q.add(t);
                }
            } else {
                q.add(t);
            }
        }
        return q;
    }

    public HashMap<String, double[]> groupPriceByCategory() {
        HashMap<String, double[]> result = new HashMap();
        for (Transaction t : this) {
            String category = (String) t.get(TRANSACTIONFIELD.CATEGORY);
            double price = (double) t.get(TRANSACTIONFIELD.PRICE);
            double[] categoryArray = result.get(category);
            if (categoryArray == null) {
                categoryArray = new double[2];
            }
            if (price > 0) {
                categoryArray[0] += price;
            } else if (price < 0) {
                categoryArray[1] += price;
            }
            result.put(category, categoryArray);
        }
        return result;
    }

    public HashMap<Integer, double[]> groupPriceByMonth() {
        HashMap<Integer, double[]> result = new HashMap();
        Calendar cal = Calendar.getInstance();
        for (Transaction t : this) {
            cal.setTime((Date) t.get(TRANSACTIONFIELD.DATE_ADDED));
            int month = cal.get(Calendar.MONTH);
            double price = (double) t.get(TRANSACTIONFIELD.PRICE);
            double[] monthArray = result.get(month);
            if (monthArray == null) {
                monthArray = new double[2];
            }
            if (price > 0) {
                monthArray[0] += price;
            } else if (price < 0) {
                monthArray[1] += price;
            }
            result.put(month, monthArray);
        }
        return result;
    }

    public QueryableList selectJobs() {
        QueryableList q = new QueryableList();
        for (Transaction t : this) {
            if ((boolean) t.get(TRANSACTIONFIELD.JOB)) {
                q.add(t);
            }
        }
        return q;
    }
}
