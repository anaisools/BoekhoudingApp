package dialogs;

import data.Data;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
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

    private JCheckBox m_isLoan;
    private JPanel m_loansPanel;
    private JCheckBox m_isExceptional;
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

        m_isLoan = new JCheckBox("Needs to be paid (back)");
        m_isExceptional = new JCheckBox("Exceptional transaction");

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
        m_fieldsLoans.add(new Pair(TRANSACTIONFIELD.PAYBACK_TRANSACTOR, new ValidationComboBox(false, "Payback transactor category > Payback transactor", " > ", transactors)));
    }

    /**
     * Set layout-related preferences for the dialog.
     */
    private void setPreferences() {
        int width = 600, height = 650;
        this.setPreferredSize(new Dimension(width, height));
        this.setMinimumSize(this.getPreferredSize());

        // center on screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getPreferredSize().width / 2, dim.height / 2 - this.getPreferredSize().height / 2);
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
    }

    /**
     * Validates all fields. If all fields have a valid value, the function
     * returns true, otherwise false.
     *
     * @return true if all required fields are valid, false if not
     */
    private boolean validateFields() {
        boolean allValid = true; // assume everything is valid

        for (Pair<TRANSACTIONFIELD, ValidationComponent> p : m_fieldsGeneral) {
            ValidationComponent c = p.getValue();
            c.forceValidate();
            if (!c.isValid()) {
                allValid = false;
            }
        }

        // if (isLoan) { check loan fields }
        return allValid;
    }

    /**
     * Generate a transaction from the (validated) text fields.
     */
    private void generateTransaction() {
        if (m_transaction == null) {
            m_transaction = new Transaction(Data.GetInstance().getTransactions().getNewID());
        }
        for (Pair<TRANSACTIONFIELD, ValidationComponent> p : m_fieldsGeneral) {
            setTransactionFieldFromComponent(p.getKey(), p.getValue());
        }
        m_transaction.set(TRANSACTIONFIELD.PAYBACK, m_isLoan.isSelected());
        if (m_isLoan.isSelected()) {
            for (Pair<TRANSACTIONFIELD, ValidationComponent> p : m_fieldsLoans) {
                setTransactionFieldFromComponent(p.getKey(), p.getValue());
            }
        }
        m_transaction.set(TRANSACTIONFIELD.EXCEPTIONAL, m_isExceptional.isSelected());
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
        if (preferredClass.equals(CategoryString.class) && content.getClass().equals(String.class)) {
            content = new CategoryString((String) content);
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
        for (Pair<TRANSACTIONFIELD, ValidationComponent> pair : m_fieldsGeneral) {
            TRANSACTIONFIELD fieldtype = pair.getKey();
            ValidationComponent component = pair.getValue();
            Object value = m_transaction.get(fieldtype);
            if (value != null && value.getClass().equals(CategoryString.class)) {
                value = ((CategoryString) value).toString();
            }
            component.setValue(value);
        }
        setLoan((boolean) m_transaction.get(TRANSACTIONFIELD.PAYBACK));
        if ((boolean) m_transaction.get(TRANSACTIONFIELD.PAYBACK)) {
            for (Pair<TRANSACTIONFIELD, ValidationComponent> pair : m_fieldsLoans) {
                TRANSACTIONFIELD fieldtype = pair.getKey();
                ValidationComponent component = pair.getValue();
                Object value = m_transaction.get(fieldtype);
                if (value != null && value.getClass().equals(CategoryString.class)) {
                    value = ((CategoryString) value).toString();
                }
                component.setValue(value);
            }
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

        // Main panel
        cgb.setWeight(1, 0);
        cgb.setFill(true, false);
        int pos = 0;
        cgb.add(m_mainPanel, general, 0, pos++);
        cgb.add(m_mainPanel, m_isExceptional, 0, pos++);
        cgb.add(m_mainPanel, m_isLoan, 0, pos++);
        cgb.add(m_mainPanel, m_loansPanel, 0, pos++);

        JPanel filler = new JPanel();
        filler.setOpaque(false);
        cgb.add(m_mainPanel, filler, 0, pos++, true, false, 1, 1);

    }

    /**
     * Add a component to a JPanel with certain layout parameters.
     *
     * @param panel
     * @param hFill
     * @param vFill
     * @param in
     * @param x
     * @param y
     * @param xFill
     * @param yFill
     * @param o the component to add
     */
    private void addWithGridBagConstraints(JPanel panel, boolean hFill, boolean vFill, Insets in, int x, int y, int xFill, int yFill, Component o) {
        if (panel.getLayout().getClass() != GridBagLayout.class) {
            panel.setLayout(new GridBagLayout());
        }
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        if (hFill && vFill) {
            c.fill = GridBagConstraints.BOTH;
        } else if (hFill) {
            c.fill = GridBagConstraints.HORIZONTAL;
        } else if (vFill) {
            c.fill = GridBagConstraints.VERTICAL;
        }
        c.insets = in;
        c.gridx = x;
        c.gridy = y;
        c.weightx = xFill;
        c.weighty = yFill;
        panel.add(o, c);
    }

    // Public functions --------------------------------------------------------
    /**
     * Show the dialog. This blocks the parent until the dialog is closed.
     */
    @Override
    public void show() {
        super.show();
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
