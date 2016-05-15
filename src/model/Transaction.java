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
    private String m_transactor;
    private String m_transactorCategory;
    private Date m_dateAdded;
    private String m_paymentMethod;
    private String m_paymentMethodCategory;

    // optional
    private Date m_datePaid;
    private boolean m_exceptional;

    // optional: payback
    private boolean m_payback;
    private String m_payBackTransactor;
    private String m_payBackTransactorCategory;

    // optional: job
    private boolean m_isJob;
    private double m_jobHours;
    private double m_jobWage;
    private Date m_jobDate;
    private String m_employer;

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

    // Getters -----------------------------------------------------------------
    public long getID() {
        return m_id;
    }

    public String getDescription() {
        return m_description;
    }

    public double getPrice() {
        return m_price;
    }

    public String getCategory() {
        return m_category;
    }

    public String getTransactor() {
        return m_transactor;
    }

    public String getTransactorCategory() {
        return m_transactorCategory;
    }

    public Date getDateAdded() {
        return m_dateAdded;
    }

    public Date getDatePaid() {
        return m_datePaid;
    }

    public String getPaymentMethod() {
        return m_paymentMethod;
    }

    public String getPaymentMethodCategory() {
        return m_paymentMethodCategory;
    }

    public boolean needsPayback() {
        return m_payback;
    }

    public String getPaybackTransactor() {
        return m_payBackTransactor;
    }

    public String getPaybackTransactorCategory() {
        return m_payBackTransactorCategory;
    }

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

    public String getJobEmployer() {
        return m_employer;
    }

    // Setters -----------------------------------------------------------------
    public void setDescription(String description) {
        m_description = description;
        notifyObserversOfChange();
    }

    public void setPrice(double price) {
        m_price = price;
        notifyObserversOfChange();
    }

    public void setCategory(String category) {
        m_category = category;
        notifyObserversOfChange();
    }

    public void setTransactor(String transactor, String transactorCategory) {
        m_transactor = transactor;
        m_transactorCategory = transactorCategory;
        notifyObserversOfChange();
    }

    public void setDateAdded(Date dateAdded) {
        m_dateAdded = dateAdded;
        notifyObserversOfChange();
    }

    public void setDatePaid(Date datePaid) {
        m_datePaid = datePaid;
        notifyObserversOfChange();
    }

    public void setPaymentMethod(String paymentMethod, String paymentMethodCategory) {
        m_paymentMethod = paymentMethod;
        m_paymentMethodCategory = paymentMethodCategory;
        notifyObserversOfChange();
    }

    public void setPayback(boolean needsPayback) {
        m_payback = needsPayback;
        notifyObserversOfChange();
    }

    public void setPaybackTransactor(String paidBackTransactor, String paidBackTransactorCategory) {
        setPayback(true);
        m_payBackTransactor = paidBackTransactor;
        m_payBackTransactorCategory = paidBackTransactorCategory;
        notifyObserversOfChange();
    }

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

    public void setJobEmployer(String jobEmployer) {
        setJob(true);
        m_employer = jobEmployer;
        notifyObserversOfChange();
    }

}
