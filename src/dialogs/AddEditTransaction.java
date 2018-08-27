package dialogs;

import data.Data;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import javafx.util.Pair;
import javax.swing.*;
import model.CategoryString;
import model.Transaction;
import model.Transaction.TRANSACTIONFIELD;
import view.swingextensions.*;

/**
 * Dialog to add or edit a transaction.
 *
 * The dialog is created with no parameter for adding, and for editing with the
 * transaction to edit as parameter.
 *
 * After creation, the dialog is opened with the show()-method. This blocks the
 * parent until the dialog is closed.
 *
 * When closed, the parent can use the isApproved()-function to find out if the
 * dialog was confirmed or not. If the isApproved()-function returns true, the
 * getTransaction()-method can be used to receive the result of the dialog.
 *
 * @author Anaïs Ools
 */
public class AddEditTransaction extends JDialog {

    // Members -----------------------------------------------------------------
    private boolean m_approved;
    private Transaction m_transaction;

    private JPanel m_mainPanel;
    private JScrollPane m_scrollPane;
    private JButton m_approveButton;
    private JButton m_disapproveButton;

    private ArrayList<Pair<TRANSACTIONFIELD, ValidationComponent>> m_fieldsGeneral;
    private ArrayList<Pair<TRANSACTIONFIELD, ValidationComponent>> m_fieldsLoans;
    private ArrayList<Pair<TRANSACTIONFIELD, ValidationComponent>> m_fieldsJob;
    private ArrayList<Pair<TRANSACTIONFIELD, ValidationComponent>> m_fieldsHidden;

    private JCheckBox m_isLoan;
    private JPanel m_loansPanel;
    private JCheckBox m_isExceptional;
    private JCheckBox m_isJob;
    private JPanel m_jobPanel;
    private JCheckBox m_isHidden;
    private JPanel m_hiddenPanel;

    private ValidationDateField m_datePaidField;

    // Constructors ------------------------------------------------------------
    public AddEditTransaction(JFrame parent) {
        super(parent, "Add transaction", Dialog.ModalityType.APPLICATION_MODAL);
        m_transaction = null;
        m_approved = false;

        createComponents();
        setPreferences();
        setActions();
        createUI();
        loadData();

        setLoan(false);
        setJob(false);
        setHidden(false);
    }

    public AddEditTransaction(JFrame parent, Transaction transactionToEdit) {
        super(parent, "Edit transaction", Dialog.ModalityType.APPLICATION_MODAL);
        m_transaction = transactionToEdit;
        m_approved = false;

        createComponents();
        setPreferences();
        setActions();
        createUI();
        loadData();

        setLoan((boolean) m_transaction.get(TRANSACTIONFIELD.PAYBACK));
        setJob((boolean) m_transaction.get(TRANSACTIONFIELD.JOB));
        setHidden((boolean) m_transaction.get(TRANSACTIONFIELD.HIDDEN));
    }

    // Private functions -------------------------------------------------------
    /**
     * Initialize all members.
     */
    private void createComponents() {
        m_mainPanel = new JPanel();

        m_scrollPane = new JScrollPane(m_mainPanel);

        m_approveButton = new JButton("Ok");
        m_disapproveButton = new JButton("Cancel");

        m_isLoan = new JCheckBox("Add to a transactor's loan account");
        m_isExceptional = new JCheckBox("Exceptional transaction");
        m_isJob = new JCheckBox("Mark as a job");
        m_isHidden = new JCheckBox("Hide transaction from the main table");

        // create text values for dropdowns
        String[] categories = Data.GetInstance().getTransactions().getDistinctCategories();
        String[] transactors = categoryStringArrayToStringArray(Data.GetInstance().getTransactions().getDistinctTransactors());
        String[] paymentMethods = categoryStringArrayToStringArray(Data.GetInstance().getTransactions().getDistinctPaymentMethods());

        Date today = new Date();

        // text fields
        m_fieldsGeneral = new ArrayList();
        m_fieldsGeneral.add(new Pair(TRANSACTIONFIELD.DESCRIPTION, new ValidationTextField(false, "Description", null)));
        m_fieldsGeneral.add(new Pair(TRANSACTIONFIELD.PRICE, new ValidationCurrencyField()));
        m_fieldsGeneral.add(new Pair(TRANSACTIONFIELD.CATEGORY, new ValidationComboBox(false, "Category", null, categories)));
        m_fieldsGeneral.add(new Pair(TRANSACTIONFIELD.TRANSACTOR, new ValidationComboBox(false, "Transactor category > Transactor", " > ", transactors)));
        m_fieldsGeneral.add(new Pair(TRANSACTIONFIELD.DATE_ADDED, new ValidationDateField(false, today, "Date added (dd/mm/yyyy)")));
        m_datePaidField = new ValidationDateField(true, today, "Date paid (dd/mm/yyyy)");
        m_fieldsGeneral.add(new Pair(TRANSACTIONFIELD.DATE_PAID, m_datePaidField));
        m_fieldsGeneral.add(new Pair(TRANSACTIONFIELD.PAYMENT_METHOD, new ValidationComboBox(false, "Payment method category > Payment method", " > ", paymentMethods)));

        m_fieldsLoans = new ArrayList();
        m_fieldsLoans.add(new Pair(TRANSACTIONFIELD.PAYBACK_TRANSACTOR, new ValidationComboBox(false, "Loan transactor category > Loan transactor", " > ", transactors)));
        m_fieldsLoans.add(new Pair(TRANSACTIONFIELD.PAYBACK_PRICE, new ValidationCurrencyField()));

        m_fieldsJob = new ArrayList();
        m_fieldsJob.add(new Pair(TRANSACTIONFIELD.JOB_DATE, new ValidationDateField(false, null, "Job date (dd/mm/yyyy)")));
        m_fieldsJob.add(new Pair(TRANSACTIONFIELD.JOB_HOURS, new ValidationNumberField(true, "Job hours", true)));
        //m_fieldsJob.add(new Pair(TRANSACTIONFIELD.JOB_WAGE, new ValidationNumberField(true, "Job wage (per hour)", true)));
        m_fieldsJob.add(new Pair(TRANSACTIONFIELD.JOB_WAGE, new ValidationCurrencyField()));

        m_fieldsHidden = new ArrayList();
        m_fieldsHidden.add(new Pair(TRANSACTIONFIELD.HIDDEN_DATE, new ValidationDateField(true, null, "Keep transaction hidden until (dd/mm/yyyy) - can be empty")));
    }

    /**
     * Set layout-related preferences for the dialog.
     */
    private void setPreferences() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.getWidth() * 0.3125);
        int height = (int) (screenSize.getHeight() * 0.601852);
        this.setPreferredSize(new Dimension(width, height));
        this.setMinimumSize(this.getPreferredSize());

        // center on screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getPreferredSize().width / 2, dim.height / 2 - this.getPreferredSize().height / 2);

        m_scrollPane.getVerticalScrollBar().setUnitIncrement(14);
    }

    /**
     * Set actions for the dialog.
     */
    private void setActions() {
        m_approveButton.addActionListener((ActionEvent ae) -> {
            if (validateFields()) {
                generateTransaction();
                m_approved = true;
                this.dispose();
            }
        });
        m_disapproveButton.addActionListener((ActionEvent ae) -> {
            m_approved = false;
            this.dispose();
        });
        m_isLoan.addItemListener((ItemEvent ie) -> {
            setLoan(m_isLoan.isSelected());
        });
        m_isJob.addItemListener((ItemEvent ie) -> {
            setJob(m_isJob.isSelected());
        });
        m_isHidden.addItemListener((ItemEvent ie) -> {
            setHidden(m_isHidden.isSelected());
        });
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addPropertyChangeListener("focusOwner", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (!(evt.getNewValue() instanceof JComponent)) {
                    return;
                }
                JComponent focused = (JComponent) evt.getNewValue();
                if (m_scrollPane.isAncestorOf(focused)) {
                    Rectangle bounds = focused.getBounds();
                    bounds.y -= 35;
                    bounds.height += 70;
                    ((JComponent) focused.getParent()).scrollRectToVisible(bounds);
                }
            }
        });
    }

    /**
     * Validates all fields. If all fields have a valid value, the function
     * returns true, otherwise false.
     *
     * @return true if all required fields are valid, false if not
     */
    private boolean validateFields() {
        ArrayList<Pair<TRANSACTIONFIELD, ValidationComponent>> fieldsToCheck = new ArrayList();
        fieldsToCheck.addAll(m_fieldsGeneral);
        if (m_isLoan.isSelected()) {
            fieldsToCheck.addAll(m_fieldsLoans);
        }
        if (m_isJob.isSelected()) {
            fieldsToCheck.addAll(m_fieldsJob);
        }
        if (m_isHidden.isSelected()) {
            fieldsToCheck.addAll(m_fieldsHidden);
        }

        boolean allValid = true; // assume everything is valid
        for (Pair<TRANSACTIONFIELD, ValidationComponent> p : fieldsToCheck) {
            ValidationComponent c = p.getValue();
            c.forceValidate();
            if (!c.isValid()) {
                allValid = false;
            }
        }

        return allValid;
    }

    /**
     * Generate a transaction from the (validated) text fields.
     */
    private void generateTransaction() {
        ArrayList<Pair<TRANSACTIONFIELD, ValidationComponent>> fieldsToSet = new ArrayList();
        fieldsToSet.addAll(m_fieldsGeneral);
        if (m_isLoan.isSelected()) {
            fieldsToSet.addAll(m_fieldsLoans);
        }
        if (m_isJob.isSelected()) {
            fieldsToSet.addAll(m_fieldsJob);
        }
        if (m_isHidden.isSelected()) {
            fieldsToSet.addAll(m_fieldsHidden);
        }
        if (m_transaction == null) {
            m_transaction = new Transaction(Data.GetInstance().getTransactions().getNewID());
        }
        for (Pair<TRANSACTIONFIELD, ValidationComponent> p : fieldsToSet) {
            setTransactionFieldFromComponent(p.getKey(), p.getValue());
        }
        m_transaction.set(TRANSACTIONFIELD.EXCEPTIONAL, m_isExceptional.isSelected());
        m_transaction.set(TRANSACTIONFIELD.PAYBACK, m_isLoan.isSelected());
        m_transaction.set(TRANSACTIONFIELD.JOB, m_isJob.isSelected());
        m_transaction.set(TRANSACTIONFIELD.HIDDEN, m_isHidden.isSelected());
    }

    /**
     * Set the value of the transaction's field with the value from a certain
     * component.
     *
     * @param field
     * @param component
     */
    private void setTransactionFieldFromComponent(TRANSACTIONFIELD field, ValidationComponent component) {
        Object content = component.getValue();
        Class preferredClass = m_transaction.getFieldClass(field);
        if (content != null && preferredClass.equals(CategoryString.class) && content.getClass().equals(String.class)) {
            content = new CategoryString((String) content);
        }
        if (field == TRANSACTIONFIELD.JOB_WAGE) {
            Object hours = m_transaction.get(TRANSACTIONFIELD.JOB_HOURS);
            if (hours != null && ((double) hours) > 0) {
                content = ((double) content) / ((double) hours);
            } else {
                content = null;
            }
        }
        m_transaction.set(field, content);
    }

    /**
     * Load the data of an existing transaction into the text fields.
     */
    private void loadData() {
        if (m_transaction == null) {
            return;
        }
        setLoan((boolean) m_transaction.get(TRANSACTIONFIELD.PAYBACK));
        setJob((boolean) m_transaction.get(TRANSACTIONFIELD.JOB));
        setHidden((boolean) m_transaction.get(TRANSACTIONFIELD.HIDDEN));
        m_isExceptional.setSelected((boolean) m_transaction.get(TRANSACTIONFIELD.EXCEPTIONAL));

        ArrayList<Pair<TRANSACTIONFIELD, ValidationComponent>> fieldsToLoad = new ArrayList();
        fieldsToLoad.addAll(m_fieldsGeneral);
        if (m_isLoan.isSelected()) {
            fieldsToLoad.addAll(m_fieldsLoans);
        }
        if (m_isJob.isSelected()) {
            fieldsToLoad.addAll(m_fieldsJob);
        }
        if (m_isHidden.isSelected()) {
            fieldsToLoad.addAll(m_fieldsHidden);
        }

        for (Pair<TRANSACTIONFIELD, ValidationComponent> pair : fieldsToLoad) {
            TRANSACTIONFIELD fieldtype = pair.getKey();
            ValidationComponent component = pair.getValue();
            Object value = m_transaction.get(fieldtype);
            if (fieldtype == TRANSACTIONFIELD.JOB_WAGE) {
                Object hours = m_transaction.get(TRANSACTIONFIELD.JOB_HOURS);
                Object wage = m_transaction.get(TRANSACTIONFIELD.JOB_WAGE);
                if (hours != null && wage != null) {
                    value = ((double) wage) * ((double) hours);
                } else {
                    value = 0.0;
                }
            }
            if (value != null && value.getClass().equals(CategoryString.class)) {
                value = ((CategoryString) value).toString();
            }
            component.setValue(value);
        }
    }

    /**
     * Convert an array filled with CategoryString objects to a String array.
     *
     * @param cs
     * @return
     */
    private String[] categoryStringArrayToStringArray(CategoryString[] cs) {
        String[] result = new String[cs.length];
        for (int i = 0; i < cs.length; i++) {
            result[i] = cs[i].toString();
        }
        return result;
    }

    /**
     * Set if the dialog should show fields for loans or not. This also sets the
     * isLoan checkbox.
     *
     * @param isLoan
     */
    private void setLoan(boolean isLoan) {
        m_isLoan.setSelected(isLoan);
        m_loansPanel.setVisible(isLoan);
    }

    /**
     * Set if the dialog should show fields for jobs or not. This also sets the
     * isJob checkbox.
     *
     * @param isJob
     */
    private void setJob(boolean isJob) {
        m_isJob.setSelected(isJob);
        m_jobPanel.setVisible(isJob);
    }

    /**
     * Set if the dialog should show fields for hiding or not. This also sets
     * the isHidden checkbox.
     *
     * @param isHidden
     */
    private void setHidden(boolean isHidden) {
        m_isHidden.setSelected(isHidden);
        m_hiddenPanel.setVisible(isHidden);
    }

    // UI functions ------------------------------------------------------------
    /**
     * Add members to the dialog using layout managers.
     */
    private void createUI() {
        // scrollpane
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weighty = 1;
        c.weightx = 1;
        this.add(m_scrollPane, c);

        // buttons
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.weighty = 0;
        c.weightx = 0.5;
        c.insets = new Insets(20, 20, 20, 20);
        this.add(m_disapproveButton, c);
        c.gridx = 1;
        c.gridy = 1;
        c.weighty = 0;
        c.weightx = 0.5;
        this.add(m_approveButton, c);

        createUITextFields();
    }

    /**
     * Add all text fields to the content panel.
     */
    private void createUITextFields() {
        CustomGridBag cgb = new CustomGridBag();
        cgb.setAnchor(GridBagConstraints.NORTH);
        cgb.setInsets(10);
        cgb.setFill(true, false);
        cgb.setWeight(1, 1);

        // General
        JPanel general = new JPanel();
        general.setBorder(BorderFactory.createTitledBorder(" General "));
        for (int col = 0; col < m_fieldsGeneral.size(); col++) {
            Pair<TRANSACTIONFIELD, ValidationComponent> c = m_fieldsGeneral.get(col);
            Component comp = (Component) c.getValue();
            comp.setPreferredSize(new Dimension(140, 25));
            cgb.add(general, comp, 0, col);
        }

        // Loans
        m_loansPanel = new JPanel();
        m_loansPanel.setBorder(BorderFactory.createTitledBorder(" Loans "));
        for (int col = 0; col < m_fieldsLoans.size(); col++) {
            Pair<TRANSACTIONFIELD, ValidationComponent> c = m_fieldsLoans.get(col);
            Component comp = (Component) c.getValue();
            comp.setPreferredSize(new Dimension(140, 25));
            cgb.add(m_loansPanel, comp, 0, col);
        }

        // Job
        m_jobPanel = new JPanel();
        m_jobPanel.setBorder(BorderFactory.createTitledBorder(" Job "));
        for (int col = 0; col < m_fieldsJob.size(); col++) {
            Pair<TRANSACTIONFIELD, ValidationComponent> c = m_fieldsJob.get(col);
            Component comp = (Component) c.getValue();
            comp.setPreferredSize(new Dimension(140, 25));
            cgb.add(m_jobPanel, comp, 0, col);
        }

        // Hiding
        m_hiddenPanel = new JPanel();
        m_hiddenPanel.setBorder(BorderFactory.createTitledBorder(" Hiding "));
        for (int col = 0; col < m_fieldsHidden.size(); col++) {
            Pair<TRANSACTIONFIELD, ValidationComponent> c = m_fieldsHidden.get(col);
            Component comp = (Component) c.getValue();
            comp.setPreferredSize(new Dimension(140, 25));
            cgb.add(m_hiddenPanel, comp, 0, col);
        }

        // Main panel
        cgb.setWeight(1, 0);
        cgb.setFill(true, false);
        int pos = 0;
        cgb.add(m_mainPanel, general, 0, pos++);
        cgb.add(m_mainPanel, m_isExceptional, 0, pos++);
        cgb.add(m_mainPanel, m_isLoan, 0, pos++);
        cgb.add(m_mainPanel, m_loansPanel, 0, pos++);
        cgb.add(m_mainPanel, m_isJob, 0, pos++);
        cgb.add(m_mainPanel, m_jobPanel, 0, pos++);
        cgb.add(m_mainPanel, m_isHidden, 0, pos++);
        cgb.add(m_mainPanel, m_hiddenPanel, 0, pos++);

        JPanel filler = new JPanel();
        filler.setOpaque(false);
        cgb.add(m_mainPanel, filler, 0, pos++, true, false, 1, 1);

    }

    // Public functions --------------------------------------------------------
    /**
     * Show the dialog. This blocks the parent until the dialog is closed.
     */
    public void showDialog() {
        super.setVisible(true);
    }

    /**
     * Check if the dialog was approved when closed or not. If the dialog wasn't
     * opened, this will return false.
     *
     * @return true if the dialog was approved. False otherwise.
     */
    public boolean isApproved() {
        return m_approved;
    }

    /**
     * Get the transaction that is created or edited by the dialog. If the
     * dialog was not approved, this will return null.
     *
     * @return the new or updated transaction, or null if the dialog was not
     * approved.
     */
    public Transaction getTransaction() {
        if (m_approved) {
            return m_transaction;
        }
        return null;
    }
}
