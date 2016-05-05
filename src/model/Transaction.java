package model;

import java.util.Date;
import java.util.Observable;
import java.util.Observer;

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
    private Date m_datePaid;
    private String m_paymentMethod;
    private String m_paymentMethodCategory;

    // optional
    private boolean m_needsToBePaidBack;
    private Date m_datePaidBack;
    private String m_paidBackTransactor;
    private String m_paidBackTransactorCategory;

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

    public boolean getNeedsToBePaidBack() {
        return m_needsToBePaidBack;
    }

    public Date getDatePaidBack() {
        return m_datePaidBack;
    }

    public String getPaidBackTransactor() {
        return m_paidBackTransactor;
    }

    public String getPaidBackTransactorCategory() {
        return m_paidBackTransactorCategory;
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

    public void setNeedsToBePaidBack(boolean needsToBePaidBack) {
        m_needsToBePaidBack = needsToBePaidBack;
        notifyObserversOfChange();
    }

    public void setDatePaidBack(Date datePaidBack) {
        m_datePaidBack = datePaidBack;
        notifyObserversOfChange();
    }

    public void setPaidBackTransactor(String paidBackTransactor, String paidBackTransactorCategory) {
        m_paidBackTransactor = paidBackTransactor;
        m_paidBackTransactorCategory = paidBackTransactorCategory;
        notifyObserversOfChange();
    }
}
