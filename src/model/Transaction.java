package model;

import data.Data;
import java.util.*;

/**
 * Class that represents a transaction in the application.
 *
 * @author Anaïs Ools
 */
public class Transaction extends Observable {

    // required
    private final long m_id;
    private String m_description;
    private Double m_price;
    private String m_category;
    private CategoryString m_transactor;
    private Date m_dateAdded;
    private CategoryString m_paymentMethod;

    // optional
    private Date m_datePaid;
    private boolean m_exceptional;
    private boolean m_hidden;
    private Date m_hiddenDate;

    // optional: payback
    private boolean m_payback;
    private CategoryString m_payBackTransactor;
    private Double m_paybackPrice;

    // optional: job
    private boolean m_isJob;
    private Double m_jobHours;
    private Double m_jobWage;
    private Date m_jobDate;

    public enum TRANSACTIONFIELD {

        DESCRIPTION, PRICE, CATEGORY, TRANSACTOR, DATE_ADDED, DATE_PAID,
        PAYMENT_METHOD, EXCEPTIONAL, PAYBACK, PAYBACK_TRANSACTOR, PAYBACK_PRICE,
        JOB, JOB_HOURS,
        JOB_WAGE, JOB_DATE, HIDDEN, HIDDEN_DATE
    };

    public Transaction(long id) {
        m_id = id;
    }

    // Private functions -------------------------------------------------------
    @Override
    public String toString() {
        return m_description + ", " + model.Settings.GetInstance().convertPriceToString(m_price) + " by " + m_transactor.getValue();
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
     * Add an object to the list of observers to be notified when something in
     * the data changes.
     *
     * @param o
     */
    public void addAsObserver(Observer o) {
        this.addObserver(o);
    }

    /**
     * Get the ID of the transaction.
     *
     * @return
     */
    public long getID() {
        return m_id;
    }

    /**
     * Get a certain field based in the TRANSACTIONFIELD-type provided.
     *
     * @param field
     * @return the requested field, can be NULL
     */
    public Object get(TRANSACTIONFIELD field) {
        switch (field) {
            case DESCRIPTION:
                return m_description;
            case PRICE:
                return m_price;
            case CATEGORY:
                return m_category;
            case TRANSACTOR:
                return m_transactor;
            case DATE_ADDED:
                return m_dateAdded;
            case DATE_PAID:
                return m_datePaid;
            case PAYMENT_METHOD:
                return m_paymentMethod;
            case EXCEPTIONAL:
                return m_exceptional;
            case PAYBACK:
                return m_payback;
            case PAYBACK_TRANSACTOR:
                return m_payBackTransactor;
            case PAYBACK_PRICE:
                return m_paybackPrice;
            case JOB:
                return m_isJob;
            case JOB_HOURS:
                return m_jobHours;
            case JOB_WAGE:
                return m_jobWage;
            case JOB_DATE:
                return m_jobDate;
            case HIDDEN:
                return m_hidden;
            case HIDDEN_DATE:
                return m_hiddenDate;
            default:
                return null;
        }
    }

    /**
     * Set a certain field to the provided value. If the value is not the
     * correct class, nothing will be set.
     *
     * @param field
     * @param value
     */
    public void set(TRANSACTIONFIELD field, Object value) {
        Class preferredType = getFieldClass(field);
        if (value != null && !value.getClass().equals(preferredType)) {
            return; // incompatible types
        }
        switch (field) {
            case DESCRIPTION:
                m_description = (String) value;
                break;
            case PRICE:
                m_price = (Double) value;
                break;
            case CATEGORY:
                m_category = (String) value;
                break;
            case TRANSACTOR:
                m_transactor = (CategoryString) value;
                break;
            case DATE_ADDED:
                m_dateAdded = (Date) value;
                break;
            case DATE_PAID:
                m_datePaid = (Date) value;
                break;
            case PAYMENT_METHOD:
                m_paymentMethod = (CategoryString) value;
                break;
            case EXCEPTIONAL:
                if (value != null) {
                    m_exceptional = (boolean) value;
                }
                break;
            case PAYBACK:
                if (value != null) {
                    m_payback = (boolean) value;
                }
                break;
            case PAYBACK_TRANSACTOR:
                m_payBackTransactor = (CategoryString) value;
                break;
            case PAYBACK_PRICE:
                m_paybackPrice = (Double) value;
                break;
            case JOB:
                if (value != null) {
                    m_isJob = (boolean) value;
                }
                break;
            case JOB_HOURS:
                m_jobHours = (Double) value;
                break;
            case JOB_WAGE:
                m_jobWage = (Double) value;
                break;
            case JOB_DATE:
                m_jobDate = (Date) value;
                break;
            case HIDDEN:
                if (value != null) {
                    m_hidden = (boolean) value;
                }
                break;
            case HIDDEN_DATE:
                m_hiddenDate = (Date) value;
        }
        notifyObserversOfChange();
    }

    /**
     * Returns the class that the value of a certain field must be.
     *
     * @param field
     * @return
     */
    public Class getFieldClass(TRANSACTIONFIELD field) {
        if (field == null) {
            return null;
        }
        switch (field) {
            case DESCRIPTION:
            case CATEGORY:
                return String.class;
            case PRICE:
            case JOB_HOURS:
            case JOB_WAGE:
            case PAYBACK_PRICE:
                return Double.class;
            case TRANSACTOR:
            case PAYMENT_METHOD:
            case PAYBACK_TRANSACTOR:
                return CategoryString.class;
            case DATE_ADDED:
            case DATE_PAID:
            case JOB_DATE:
            case HIDDEN_DATE:
                return Date.class;
            case EXCEPTIONAL:
            case PAYBACK:
            case JOB:
            case HIDDEN:
                return Boolean.class;
            default:
                return null;
        }
    }

    /**
     * Convert the string-representation of a transactionfield name to the
     * actual transactionfield.
     *
     * @param name
     * @return
     */
    public TRANSACTIONFIELD stringToTransactionField(String name) {
        try {
            return TRANSACTIONFIELD.valueOf(name.toUpperCase());
        } catch (Exception e) { // conversion did not work
            return null;
        }
    }

    /**
     * Convert a transactionfield name to its string-representation.
     *
     * @param field
     * @return
     */
    public String transactionFieldToString(TRANSACTIONFIELD field) {
        return field.toString().toLowerCase();
    }

    /**
     * Get a list of all the fields that are required for a transaction to be
     * valid.
     *
     * @return
     */
    public ArrayList<TRANSACTIONFIELD> requiredFields() {
        ArrayList<TRANSACTIONFIELD> list = new ArrayList();
        list.add(TRANSACTIONFIELD.DESCRIPTION);
        list.add(TRANSACTIONFIELD.PRICE);
        list.add(TRANSACTIONFIELD.CATEGORY);
        list.add(TRANSACTIONFIELD.TRANSACTOR);
        list.add(TRANSACTIONFIELD.DATE_ADDED);
        list.add(TRANSACTIONFIELD.PAYMENT_METHOD);
        return list;
    }

    /**
     * Get a list of all fields that have a non-null value in the current
     * transaction.
     *
     * @return
     */
    public ArrayList<TRANSACTIONFIELD> presentFields() {
        ArrayList<TRANSACTIONFIELD> list = new ArrayList();
        for (TRANSACTIONFIELD t : TRANSACTIONFIELD.values()) {
            Object o = get(t);
            if (o != null && !(o.getClass().equals(Boolean.class) && !((boolean) o))) {
                list.add(t);
            }
        }
        if (!m_payback) {
            list.remove(TRANSACTIONFIELD.PAYBACK_TRANSACTOR);
            list.remove(TRANSACTIONFIELD.PAYBACK_PRICE);
        }
        if (!m_isJob) {
            list.remove(TRANSACTIONFIELD.JOB_HOURS);
            list.remove(TRANSACTIONFIELD.JOB_WAGE);
            list.remove(TRANSACTIONFIELD.JOB_DATE);
        }
        if (!m_hidden) {
            list.remove(TRANSACTIONFIELD.HIDDEN_DATE);
        }
        return list;
    }

    /**
     * Generate a copy of the transaction with all fields identical but a new
     * ID.
     *
     * @return a copy of the transaction.
     */
    public Transaction copy() {
        long id = Data.GetInstance().getTransactions().getNewID();
        Transaction t = new Transaction(id);
        for (TRANSACTIONFIELD field : TRANSACTIONFIELD.values()) {
            t.set(field, this.get(field));
        }
        return t;
    }
}
