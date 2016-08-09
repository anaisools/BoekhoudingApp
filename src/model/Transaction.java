package model;

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
    private double m_price;
    private String m_category;
    private CategoryString m_transactor;
    private Date m_dateAdded;
    private CategoryString m_paymentMethod;

    // optional
    private Date m_datePaid;
    private boolean m_exceptional;

    // optional: payback
    private boolean m_payback;
    private CategoryString m_payBackTransactor;

    // optional: job
    private boolean m_isJob;
    private double m_jobHours;
    private double m_jobWage;
    private Date m_jobDate;

    public enum TRANSACTIONFIELD {

        DESCRIPTION, PRICE, CATEGORY, TRANSACTOR, DATEADDED, DATEPAID, PAYMENTMETHOD, EXCEPTIONAL, PAYBACK, PAYBACK_TRANSACTOR
    };

    public Transaction(long id) {
        m_id = id;
    }

    // Private functions -------------------------------------------------------
    @Override
    public String toString() {
        return m_description + ", € " + m_price + ", by " + m_transactor;
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
     * Returns the class that the value of a certain field must be.
     *
     * @param field
     * @return
     */
    public Class getFieldClass(TRANSACTIONFIELD field) {
        switch (field) {
            case DESCRIPTION:
            case CATEGORY:
                return String.class;
            case PRICE:
                return Double.class;
            case TRANSACTOR:
            case PAYMENTMETHOD:
            case PAYBACK_TRANSACTOR:
                return CategoryString.class;
            case DATEADDED:
            case DATEPAID:
                return Date.class;
            case EXCEPTIONAL:
            case PAYBACK:
                return Boolean.class;
            default:
                return null;
        }
    }

    // Getters -----------------------------------------------------------------
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
            case DATEADDED:
                return m_dateAdded;
            case DATEPAID:
                return m_datePaid;
            case PAYMENTMETHOD:
                return m_paymentMethod;
            case EXCEPTIONAL:
                return m_exceptional;
            case PAYBACK:
                return m_payback;
            case PAYBACK_TRANSACTOR:
                return m_payBackTransactor;
            default:
                return null;
        }
    }

    public long getID() {
        return m_id;
    }

    @Deprecated
    public String getDescription() {
        return m_description;
    }

    @Deprecated
    public double getPrice() {
        return m_price;
    }

    @Deprecated
    public String getCategory() {
        return m_category;
    }

    @Deprecated
    public CategoryString getTransactor() {
        return m_transactor;
    }

    @Deprecated
    public Date getDateAdded() {
        return m_dateAdded;
    }

    @Deprecated
    public Date getDatePaid() {
        return m_datePaid;
    }

    @Deprecated
    public CategoryString getPaymentMethod() {
        return m_paymentMethod;
    }

    @Deprecated
    public boolean needsPayback() {
        return m_payback;
    }

    @Deprecated
    public CategoryString getPaybackTransactor() {
        return m_payBackTransactor;
    }

    @Deprecated
    public boolean isExceptional() {
        return m_exceptional;
    }

    public boolean isJob() {
        return m_isJob;
    }

    public double getJobHours() {
        return m_jobHours;
    }

    public double getJobWage() {
        return m_jobWage;
    }

    public Date getJobDate() {
        return m_jobDate;
    }

    // Setters -----------------------------------------------------------------
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
                m_price = (double) value;
                break;
            case CATEGORY:
                m_category = (String) value;
                break;
            case TRANSACTOR:
                m_transactor = (CategoryString) value;
                break;
            case DATEADDED:
                m_dateAdded = (Date) value;
                break;
            case DATEPAID:
                m_datePaid = (Date) value;
                break;
            case PAYMENTMETHOD:
                m_paymentMethod = (CategoryString) value;
                break;
            case EXCEPTIONAL:
                m_exceptional = (boolean) value;
                break;
            case PAYBACK:
                m_payback = (boolean) value;
                break;
            case PAYBACK_TRANSACTOR:
                m_payBackTransactor = (CategoryString) value;
                break;
        }
        notifyObserversOfChange();
    }

    @Deprecated
    public void setDescription(String description) {
        m_description = description;
        notifyObserversOfChange();
    }

    @Deprecated
    public void setPrice(double price) {
        m_price = price;
        notifyObserversOfChange();
    }

    @Deprecated
    public void setCategory(String category) {
        m_category = category;
        notifyObserversOfChange();
    }

    @Deprecated
    public void setTransactor(String transactor, String transactorCategory) {
        m_transactor = new CategoryString(transactorCategory, transactor);
        notifyObserversOfChange();
    }

    @Deprecated
    public void setDateAdded(Date dateAdded) {
        m_dateAdded = dateAdded;
        notifyObserversOfChange();
    }

    @Deprecated
    public void setDatePaid(Date datePaid) {
        m_datePaid = datePaid;
        notifyObserversOfChange();
    }

    @Deprecated
    public void setPaymentMethod(String paymentMethod, String paymentMethodCategory) {
        m_paymentMethod = new CategoryString(paymentMethodCategory, paymentMethod);
        notifyObserversOfChange();
    }

    @Deprecated
    public void setPayback(boolean needsPayback) {
        m_payback = needsPayback;
        notifyObserversOfChange();
    }

    @Deprecated
    public void setPaybackTransactor(String paybackTransactor, String paybackTransactorCategory) {
        setPayback(true);
        m_payBackTransactor = new CategoryString(paybackTransactorCategory, paybackTransactor);
        notifyObserversOfChange();
    }

    @Deprecated
    public void setExceptional(boolean isExceptional) {
        m_exceptional = isExceptional;
        notifyObserversOfChange();
    }

    public void setJob(boolean isJob) {
        m_isJob = isJob;
        notifyObserversOfChange();
    }

    public void setJobHours(double jobHours) {
        setJob(true);
        m_jobHours = jobHours;
        notifyObserversOfChange();
    }

    public void setJobWage(double jobWage) {
        setJob(true);
        m_jobWage = jobWage;
        notifyObserversOfChange();
    }

    public void setJobDate(Date jobDate) {
        setJob(true);
        m_jobDate = jobDate;
        notifyObserversOfChange();
    }

}
