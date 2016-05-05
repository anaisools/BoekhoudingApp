package model;

import java.util.Date;

/**
 * Class that represents a transaction in the application.
 *
 * @author Anaïs Ools
 */
public class Transaction {

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

    @Override
    public String toString() {
        return m_description + ", € " + m_price + ", by " + m_transactor;
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
    }

    public void setPrice(double price) {
        m_price = price;
    }

    public void setCategory(String category) {
        m_category = category;
    }

    public void setTransactor(String transactor, String transactorCategory) {
        m_transactor = transactor;
        m_transactorCategory = transactorCategory;
    }

    public void setDateAdded(Date dateAdded) {
        m_dateAdded = dateAdded;
    }

    public void setDatePaid(Date datePaid) {
        m_datePaid = datePaid;
    }

    public void setPaymentMethod(String paymentMethod, String paymentMethodCategory) {
        m_paymentMethod = paymentMethod;
        m_paymentMethodCategory = paymentMethodCategory;
    }

    public void setNeedsToBePaidBack(boolean needsToBePaidBack) {
        m_needsToBePaidBack = needsToBePaidBack;
    }

    public void setDatePaidBack(Date datePaidBack) {
        m_datePaidBack = datePaidBack;
    }

    public void setPaidBackTransactor(String paidBackTransactor, String paidBackTransactorCategory) {
        m_paidBackTransactor = paidBackTransactor;
        m_paidBackTransactorCategory = paidBackTransactorCategory;
    }
}
