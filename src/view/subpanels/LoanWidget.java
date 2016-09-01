package view.subpanels;

import data.QueryableList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import model.CategoryString;
import model.Transaction;
import view.swingextensions.CustomGridBag;

/**
 * This class is a panel that is filled with the loans of one transactor.
 *
 * @author Anaïs Ools
 */
public class LoanWidget extends JPanel {

    // Members & constructor ---------------------------------------------------
    private final CategoryString m_transactor;
    private QueryableList m_transactions;
    private boolean m_collapsed;

    private JLabel m_title;
    private JLabel m_price;
    private JButton m_buttonCollapse;
    private ArrayList<JLabel> m_list;

    public LoanWidget(CategoryString transactor) {
        m_transactor = transactor;
        m_transactions = new QueryableList();

        createComponents();
        setPreferences();
        setActions();
        createUI();

        loadTransactions();
        updateTotalPrice();
        setCollapsed(true);
    }

    public LoanWidget(Transaction t) {
        m_transactor = (CategoryString) t.get(Transaction.TRANSACTIONFIELD.TRANSACTOR);
        m_transactions = new QueryableList();
        addTransaction(t);

        createComponents();
        setPreferences();
        setActions();
        createUI();

        loadTransactions();
        updateTotalPrice();
        setCollapsed(true);
    }

    // Private functions -------------------------------------------------------
    /**
     * Initialize all members.
     */
    private void createComponents() {
        m_title = new JLabel(m_transactor.getValue() + " (" + m_transactor.getCategory() + ")");
        m_price = new JLabel("");
        m_buttonCollapse = new JButton("");
        m_list = new ArrayList();
    }

    /**
     * Set layout-related preferences for the panel.
     */
    private void setPreferences() {
        // Color and borders
        this.setBackground(new Color(255, 227, 132));
        this.setBorder(BorderFactory.createLineBorder(Color.black));

        // Fonts
        m_title.setFont(new Font("Tahoma", Font.PLAIN, 16));
        m_price.setFont(new Font("Tahoma", Font.BOLD, 16));
        m_buttonCollapse.setPreferredSize(new Dimension(40, 25));
    }

    /**
     * Set actions for members of the panel.
     */
    private void setActions() {
        m_buttonCollapse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                setCollapsed(!m_collapsed);
            }
        });
    }

    /**
     * Add members to the panel, using layout managers.
     */
    private void createUI() {
        CustomGridBag c = new CustomGridBag();
        c.setInsets(10);

        // Title and price
        c.setAnchor(GridBagConstraints.WEST);
        c.add(this, m_title, 0, 0, false, false, 1, 0);
        c.setAnchor(GridBagConstraints.EAST);
        c.add(this, m_price, 2, 0, false, false, 0, 1);

        // Collapse button
        c.add(this, m_buttonCollapse, 3, 0, false, false, 0, 0);
    }

    /**
     * Set if the panel is collapsed or unfolded. This also changes the panel if
     * the state changes.
     *
     * @param collapsed
     */
    private void setCollapsed(boolean collapsed) {
        m_collapsed = collapsed;
        if (m_collapsed) {
            m_buttonCollapse.setText("+");
        } else {
            m_buttonCollapse.setText("-");
        }
        for (JLabel l : m_list) {
            l.setVisible(!m_collapsed);
        }
    }

    /**
     * This first clears the currently displayed list of transactions, and then
     * fills it with the transactor's loans.
     */
    private void loadTransactions() {
        if (m_transactions == null || m_list == null) {
            return;
        }
        for (JLabel l : m_list) {
            this.remove(l);
        }
        m_list.clear();
        m_transactions = m_transactions.sortByDateAdded();

        CustomGridBag c = new CustomGridBag();
        c.setInsets(5, 5, 30, 10);

        // Create labels
        int i = 1;
        for (Transaction t : m_transactions) {
            // Get labels
            String description = (String) t.get(Transaction.TRANSACTIONFIELD.DESCRIPTION);
            String price = Double.toString((double) t.get(Transaction.TRANSACTIONFIELD.PRICE));
            price = "€ " + price.replace(".", ",");

            // Add labels to UI
            JLabel label_description = new JLabel(description);
            JLabel label_price = new JLabel(price);
            m_list.add(label_description);
            m_list.add(label_price);
            c.setAnchor(GridBagConstraints.WEST);
            c.add(this, label_description, 0, i);
            c.setAnchor(GridBagConstraints.EAST);
            c.add(this, label_price, 2, i);
            i++;
        }
    }

    /**
     * Update the label that displays the total price.
     */
    private void updateTotalPrice() {
        double price = 0;
        for (Transaction t : m_transactions) {
            price += (double) t.get(Transaction.TRANSACTIONFIELD.PRICE);
        }
        m_price.setText("€ " + Double.toString(price).replace(".", ","));
    }

    // Public functions --------------------------------------------------------
    /**
     * Add a transaction to the panel. This happens only if the transactor is
     * the same one as the panel represents.
     *
     * @param t
     */
    public void addTransaction(Transaction t) {
        if (m_transactor.equals((CategoryString) t.get(Transaction.TRANSACTIONFIELD.TRANSACTOR))) {
            m_transactions.add(t);
        }
        loadTransactions();
        updateTotalPrice();
        setCollapsed(m_collapsed);
    }

    /**
     * Get the transactor that the panel represents.
     *
     * @return
     */
    public CategoryString getTransactor() {
        return m_transactor;
    }
}