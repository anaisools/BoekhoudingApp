package dialogs;

import data.Data;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import model.Transaction;

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

    private ArrayList<Component[]> m_fieldsGeneral;
    private ArrayList<Component[]> m_fieldsLoans;

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
        String[] transactors = Data.GetInstance().getTransactions().getDistinctTransactors();
        String[] paymentMethods = Data.GetInstance().getTransactions().getDistinctPaymentMethods();

        // text fields
        m_fieldsGeneral = new ArrayList();
        m_fieldsGeneral.add(new Component[]{new JLabel("Description"), new JTextField()});
        m_fieldsGeneral.add(new Component[]{new JLabel("Price (€)"), currencySpinner()});
        m_fieldsGeneral.add(new Component[]{new JLabel("Category"), editableCombobox(categories)});
        m_fieldsGeneral.add(new Component[]{new JLabel("Transactor"), editableValidationCombobox(transactors)});
        m_fieldsGeneral.add(new Component[]{new JLabel("Date added"), dateTextfield(true)});
        m_fieldsGeneral.add(new Component[]{new JLabel("Date paid"), dateTextfield(true)});
        m_fieldsGeneral.add(new Component[]{new JLabel("Payment method"), editableCombobox(paymentMethods)});
        m_fieldsLoans = new ArrayList();
        m_fieldsLoans.add(new Component[]{new JLabel("Needs to be paid back"), new JTextField()});
        m_fieldsLoans.add(new Component[]{new JLabel("Pay back transactor"), editableCombobox(transactors)});
        m_fieldsLoans.add(new Component[]{new JLabel("Date paid back"), dateTextfield(false)});

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
            generateTransaction();
            m_approved = true;
            this.dispose();
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
            Component[] c = m_fieldsGeneral.get(col);
            addWithGridBagConstraints(general, true, true, in, 0, col, 0, 0, c[0]);
            c[0].setPreferredSize(new Dimension(140, 25));
            for (int row = 1; row < c.length; row++) {
                addWithGridBagConstraints(general, true, true, in, row, col, 1, 0, c[row]);
            }
        }

        // loans
        JPanel loans = new JPanel();
        loans.setBorder(BorderFactory.createTitledBorder(" Loans "));
        for (int col = 0; col < m_fieldsLoans.size(); col++) {
            Component[] c = m_fieldsLoans.get(col);
            addWithGridBagConstraints(loans, true, true, in, 0, col, 0, 0, c[0]);
            c[0].setPreferredSize(new Dimension(140, 25));
            for (int row = 1; row < c.length; row++) {
                addWithGridBagConstraints(loans, true, true, in, row, col, 1, 0, c[row]);
            }
        }

        // main panel
        addWithGridBagConstraints(m_mainPanel, true, false, in, 0, 0, 1, 1, general);
        addWithGridBagConstraints(m_mainPanel, true, false, in, 0, 1, 1, 1, loans);

    }

    /**
     * Generate a transaction from the (validated) text fields.
     */
    private void generateTransaction() {
        if (m_transaction == null) {
            m_transaction = new Transaction(Data.GetInstance().getTransactions().getNewID());
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String[] split;
        for (Component[] c : m_fieldsGeneral) {
            switch (((JLabel) c[0]).getText()) {
                case "Description":
                    m_transaction.setDescription(((JTextField) c[1]).getText());
                    break;
                case "Price (€)":
                    m_transaction.setPrice((Double) (((JSpinner) c[1]).getValue()));
                    break;
                case "Category":
                    m_transaction.setCategory((String) ((JComboBox) c[1]).getSelectedItem());
                    break;
                case "Transactor":
                    split = ((String) ((JComboBox) c[1]).getSelectedItem()).split(" > ");
                    m_transaction.setTransactor(split[1], split[0]);
                    break;
                case "Date added":
                    try {
                        m_transaction.setDateAdded(df.parse(((JTextField) c[1]).getText()));
                    } catch (ParseException ex) {
                        m_transaction.setDateAdded(null);
                    }
                    break;
                case "Date paid":
                    try {
                        m_transaction.setDatePaid(df.parse(((JTextField) c[1]).getText()));
                    } catch (ParseException ex) {
                        m_transaction.setDatePaid(null);
                    }
                    break;
                case "Payment method":
                    split = ((String) ((JComboBox) c[1]).getSelectedItem()).split(" > ");
                    m_transaction.setPaymentMethod(split[1], split[0]);
                    break;
            }
        }
    }

    /**
     * Load the data of an existing transaction into the text fields.
     */
    private void loadData() {
        if (m_transaction == null) {
            return;
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String comb;
        for (Component[] c : m_fieldsGeneral) {
            switch (((JLabel) c[0]).getText()) {
                case "Description":
                    ((JTextField) c[1]).setText(m_transaction.getDescription());
                    break;
                case "Price (€)":
                    ((JSpinner) c[1]).setValue(m_transaction.getPrice());
                    break;
                case "Category":
                    ((JComboBox) c[1]).setSelectedItem(m_transaction.getCategory());
                    break;
                case "Transactor":
                    comb = m_transaction.getTransactorCategory() + " > " + m_transaction.getTransactor();
                    ((JComboBox) c[1]).setSelectedItem(comb);
                    break;
                case "Date added":
                    if (m_transaction.getDateAdded() != null) {
                        ((JTextField) c[1]).setText(df.format(m_transaction.getDateAdded()));
                    }
                    break;
                case "Date paid":
                    if (m_transaction.getDatePaid() != null) {
                        ((JTextField) c[1]).setText(df.format(m_transaction.getDatePaid()));
                    }
                    break;
                case "Payment method":
                    comb = m_transaction.getPaymentMethodCategory() + " > " + m_transaction.getPaymentMethod();
                    ((JComboBox) c[1]).setSelectedItem(comb);
                    break;
            }
        }
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
     * Create an editable JComboBox with a given set of values.
     *
     * @param values
     * @return
     */
    private JComboBox editableCombobox(String[] values) {
        JComboBox jcb = new JComboBox(values);
        jcb.setEditable(true);
        return jcb;
    }

    /**
     * Create a spinner that formats its text as a currency.
     *
     * @return
     */
    private Component currencySpinner() {
        SpinnerNumberModel model = new SpinnerNumberModel(0.0, -1000000000.0, 1000000000.0, 0.5);
        JSpinner spinner = new JSpinner(model);
        String pattern = "0.00";
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, pattern);
        DecimalFormat format = editor.getFormat();
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.FRANCE);
        format.setDecimalFormatSymbols(decimalFormatSymbols);
        spinner.setEditor(editor);
        return spinner;
    }

    /**
     * Create an editable combobox that checks if its text contains " > ".
     *
     * @param values
     * @return
     */
    private Component editableValidationCombobox(String[] values) {
        JComboBox combo = editableCombobox(values);
        JTextField tf = (JTextField) (combo.getEditor().getEditorComponent());
        tf.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                String text = ((JTextField) input).getText();
                return text.contains(" > ");
            }
        });
        return combo;
    }

    /**
     * Create a textField which only accepts dates.
     *
     * @param today true if the text should be set to today.
     * @return
     */
    private Component dateTextfield(boolean today) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        JFormattedTextField ftf = new JFormattedTextField(df);
        ftf.setColumns(10);
        try {
            MaskFormatter mf = new MaskFormatter("##/##/####");
            mf.setPlaceholderCharacter('_');
            mf.install(ftf);
        } catch (ParseException ex) {
            System.out.println("Mask went wrong");
        }
        if (today) {
            Date date = new Date();
            ftf.setText(df.format(date));
        }
        return ftf;
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
