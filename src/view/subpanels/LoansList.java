package view.subpanels;

import data.Data;
import data.QueryableList;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import model.CategoryString;
import model.Transaction;
import view.swingextensions.CustomGridBag;

/**
 * This class represents a panel filled with loans. The loans are loaded
 * automatically from the data and the panel updates when the data changes.
 *
 * @author Anaïs Ools
 */
public class LoansList extends JPanel implements Observer {

    private final JFrame m_parentFrame;
    private JLabel m_title;
    private QueryableList m_data;
    private ArrayList<LoanWidget> m_loanWidgets;
    private JPanel m_loanPanel;

    // Constructor -------------------------------------------------------------
    public LoansList(JFrame parentFrame) {
        m_parentFrame = parentFrame;
        Data.GetInstance().addAsObserver(this);

        createComponents();
        setPreferences();
        setActions();
        createUI();

        loadData();
        addLoansToPanel();
    }

    // Private functions -------------------------------------------------------
    /**
     * Initialize all members.
     */
    private void createComponents() {
        m_title = new JLabel("Loans");
        m_loanWidgets = new ArrayList();
        m_loanPanel = new JPanel();
    }

    /**
     * Set layout-related preferences for the panel.
     */
    private void setPreferences() {
        m_title.setFont(new Font("Serif", Font.PLAIN, 30));
        m_loanPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    /**
     * Set actions for members of the panel.
     */
    private void setActions() {
    }

    /**
     * Add members to the panel, using layout managers.
     */
    private void createUI() {
        CustomGridBag cgb = new CustomGridBag();
        cgb.setAnchor(GridBagConstraints.NORTH);

        // Title
        cgb.add(this, m_title, 0, 0, false, false, 0, 0, 20, 10, 10, 10);

        // Scrollpane
        cgb.setInsets(10);
        JScrollPane jsp = new JScrollPane(m_loanPanel);
        jsp.getVerticalScrollBar().setUnitIncrement(14);
        cgb.add(this, jsp, 0, 1, true, true, 1, 1);
    }

    /**
     * Load the data into the corresponding members.
     */
    private void loadData() {
        m_data = Data.GetInstance().getTransactions().getLoans();
        m_loanWidgets.clear();
        Map<CategoryString, LoanWidget> transactorWidgets = new HashMap();
        for (model.Transaction t : m_data) {
            CategoryString transactor = (CategoryString) t.get(Transaction.TRANSACTIONFIELD.PAYBACK_TRANSACTOR);
            if (transactor != null) {
                LoanWidget widget = transactorWidgets.get(transactor);
                if (widget == null) { // does not exist yet
                    widget = new LoanWidget(m_parentFrame, transactor);
                    transactorWidgets.put(transactor, widget);
                    m_loanWidgets.add(widget);
                }
                widget.addTransaction(t);
            }
        }
        Collections.sort(m_loanWidgets, new Comparator<LoanWidget>() {
            @Override
            public int compare(LoanWidget l1, LoanWidget l2) {
                CategoryString t1 = l1.getTransactor();
                CategoryString t2 = l2.getTransactor();
                int result = t1.getValue().compareTo(t2.getValue());
                if (result == 0) {
                    return t1.getCategory().compareTo(t2.getCategory());
                } else {
                    return result;
                }
            }
        });
    }

    /**
     * Add the loans to the user interface with this function.
     */
    private void addLoansToPanel() {
        m_loanPanel.removeAll();
        CustomGridBag cgb = new CustomGridBag();
        cgb.setAnchor(GridBagConstraints.NORTH);
        cgb.setFill(true, false);
        cgb.setWeight(1, 0);
        cgb.setInsets(10, 10, 20, 20);
        int i;
        for (i = 0; i < m_loanWidgets.size(); i++) {
            cgb.add(m_loanPanel, m_loanWidgets.get(i), 0, i);
        }
        JPanel filler = new JPanel();
        filler.setOpaque(false);
        cgb.setWeight(1, 1);
        cgb.add(m_loanPanel, filler, 0, i);
    }

    // Public functions --------------------------------------------------------
    @Override
    public void update(Observable o, Object o1) {
        loadData();
        addLoansToPanel();
    }

}
