package dialogs;

import data.Data;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.*;
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
        m_fieldsGeneral.add(new Pair(TRANSACTIONFIELD.DATEADDED, new ValidationDateField(false, today, "Date added (dd/MM/yyyy)")));
        m_fieldsGeneral.add(new Pair(TRANSACTIONFIELD.DATEPAID, new ValidationDateField(true, today, "Date paid (dd/MM/yyyy)")));
        m_fieldsGeneral.add(new Pair(TRANSACTIONFIELD.PAYMENTMETHOD, new ValidationComboBox(false, "Payment method category > Payment method", " > ", paymentMethods)));

        m_fieldsLoans = new ArrayList();
        //m_fieldsLoans.add(new Pair(TRANSACTIONFIELD.PAYBACK, new ValidationTextField(true, "REMOVE THIS", null)));
        //m_fieldsLoans.add(new Pair(TRANSACTIONFIELD.PAYBACK_TRANSACTOR, new ValidationComboBox(true, "Transactorcategory > Transactor", " > ", transactors)));
        //m_fieldsLoans.add(new Pair(TRANSACTIONFIELD.DATEPAID, new ValidationDateField(true, null, "Date paid back (dd/mm/yyyy)")));
    }

    /**
     * Set layout-related preferences for the dialog.
     */
    private void setPreferences() {
        int width = 600, height = 700;
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
    }

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
        Insets in = new Insets(10, 10, 10, 10);

        // general
        JPanel general = new JPanel();
        general.setBorder(BorderFactory.createTitledBorder(" General "));
        for (int col = 0; col < m_fieldsGeneral.size(); col++) {
            Pair<TRANSACTIONFIELD, ValidationComponent> c = m_fieldsGeneral.get(col);
            //addWithGridBagConstraints(general, true, true, in, 0, col, 0, 0, c.getKey());
            ((Component) c.getValue()).setPreferredSize(new Dimension(140, 25));
            addWithGridBagConstraints(general, true, true, in, 0, col, 1, 0, (Component) c.getValue());
        }

        // loans
        JPanel loans = new JPanel();
        loans.setBorder(BorderFactory.createTitledBorder(" Loans "));
        for (int col = 0; col < m_fieldsLoans.size(); col++) {
            Pair<TRANSACTIONFIELD, ValidationComponent> c = m_fieldsLoans.get(col);
            //addWithGridBagConstraints(loans, true, true, in, 0, col, 0, 0, c.getKey());
            ((Component) c.getValue()).setPreferredSize(new Dimension(140, 25));
            addWithGridBagConstraints(loans, true, true, in, 0, col, 1, 0, (Component) c.getValue());
        }

        // main panel
        addWithGridBagConstraints(m_mainPanel, true, false, in, 0, 0, 1, 1, general);
        addWithGridBagConstraints(m_mainPanel, true, false, in, 0, 1, 1, 1, loans);

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
        for (Pair<TRANSACTIONFIELD, ValidationComponent> c : m_fieldsGeneral) {
            Object content = c.getValue().getValue();
            TRANSACTIONFIELD field = c.getKey();
            Class preferredClass = m_transaction.getFieldClass(field);
            if (preferredClass.equals(CategoryString.class) && content.getClass().equals(String.class)) {
                content = new CategoryString((String) content);
            }
            m_transaction.set(c.getKey(), content);
        }
    }

    /**
     * Load the data of an existing transaction into the text fields.
     */
    private void loadData() {
        if (m_transaction == null) {
            return;
        }
        for (Pair<TRANSACTIONFIELD, ValidationComponent> c : m_fieldsGeneral) {
            TRANSACTIONFIELD fieldtype = c.getKey();
            ValidationComponent component = c.getValue();
            Object value = m_transaction.get(fieldtype);
            if (value.getClass().equals(CategoryString.class)) {
                value = ((CategoryString) value).toString();
            }
            component.setValue(value);
        }
    }

    private String[] categoryStringArrayToStringArray(CategoryString[] cs) {
        String[] result = new String[cs.length];
        for (int i = 0; i < cs.length; i++) {
            result[i] = cs[i].toString();
        }
        return result;
    }

    // UI help functions -------------------------------------------------------
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

    /**
     * Create a spinner that formats its text as a currency.
     *
     * @return
     */
    private Component currencySpinner() {
        SpinnerNumberModel model = new SpinnerNumberModel(0.0, -1000000000.0, 1000000000.0, 0.5);
        JSpinner spinner = new JSpinner(model);
        String pattern = "€ 0.00";
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, pattern);
        DecimalFormat format = editor.getFormat();
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.FRANCE);
        format.setDecimalFormatSymbols(decimalFormatSymbols);
        spinner.setEditor(editor);
        return spinner;
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
