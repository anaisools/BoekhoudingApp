package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import view.subpanels.*;
import view.subpanels.ChartPanel.*;
import view.swingextensions.CustomGridBag;

/**
 * This panel fills one of the tabs of the MainWindow. It contains a table with
 * sums per category.
 *
 * It asks for the data itself, but loads this data only in the constructor.
 *
 * @author Anaïs Ools
 */
public class StatsPanel extends JPanel {

    private JLabel m_title;
    private ChartPanel m_chart;
    private JComboBox m_dropdown_chartType;
    private JComboBox m_dropdown_groupby;
    private JComboBox m_dropdown_time;
    private JButton m_button_generate;

    // Members & constructor ---------------------------------------------------
    public StatsPanel() {
        createComponents();
        setPreferences();
        setActions();
        createUI();

        m_chart.setChartParameters(CHART_TYPE.TABLE, GROUP_BY.CATEGORIES, TIME.THIS_YEAR);
        m_chart.update(null, null);
    }

    // Private functions -------------------------------------------------------
    /**
     * Initialize all members.
     */
    private void createComponents() {
        m_title = new JLabel("Statistics");
        m_button_generate = new JButton("Show");
        m_chart = new ChartPanel();

        // Dropdowns
        m_dropdown_chartType = new JComboBox(new String[]{"Table", "Line chart", "Pie chart", "Population chart"});
        m_dropdown_groupby = new JComboBox(new String[]{"Categories", "Transactors", "Payment method", "Profits"});
        m_dropdown_time = new JComboBox(new String[]{"This year", "This month", "All years", "Custom"});
    }

    /**
     * Set layout-related preferences for the panel.
     */
    private void setPreferences() {
        m_title.setFont(new Font("Serif", Font.PLAIN, 36));
        m_button_generate.setBackground(Color.darkGray);
    }

    /**
     * Set actions for members of the panel.
     */
    private void setActions() {
        m_dropdown_chartType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String item = (String) m_dropdown_chartType.getSelectedItem();
                if (item.equals("Table")) {
                    m_dropdown_time.removeItem("Custom");
                } else {
                    if (m_dropdown_time.getItemCount() < 4) {
                        m_dropdown_time.addItem("Custom");
                    }
                }
                if (item.equals("Population chart")) {
                    m_dropdown_groupby.removeItem("Profits");
                } else {
                    if (m_dropdown_groupby.getItemCount() < 4) {
                        m_dropdown_groupby.addItem("Profits");
                    }
                }
            }
        });
        m_button_generate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                updateChart();
            }
        });
    }

    /**
     * Update the displayed chart to the values of the dropdown boxes.
     */
    private void updateChart() {
        String chartType = (String) m_dropdown_chartType.getSelectedItem();
        String groupBy = (String) m_dropdown_groupby.getSelectedItem();
        String time = (String) m_dropdown_time.getSelectedItem();
        CHART_TYPE enumChartType = CHART_TYPE.valueOf(chartType.toUpperCase().replace(" ", "_"));
        GROUP_BY enumGroupBy = GROUP_BY.valueOf(groupBy.toUpperCase().replace(" ", "_"));
        TIME enumTime = TIME.valueOf(time.toUpperCase().replace(" ", "_"));
        m_chart.setChartParameters(enumChartType, enumGroupBy, enumTime);
        m_chart.update(null, null);
    }

    /**
     * Add members to the panel, using layout managers.
     */
    private void createUI() {
        CustomGridBag c = new CustomGridBag();

        // Dropdown
        JPanel dropdownPanel = new JPanel();
        c.setInsets(5);
        c.add(dropdownPanel, m_dropdown_chartType, 0, 0);
        c.add(dropdownPanel, new JLabel("group by"), 1, 0);
        c.add(dropdownPanel, m_dropdown_groupby, 2, 0);
        c.add(dropdownPanel, new JLabel("showing"), 3, 0);
        c.add(dropdownPanel, m_dropdown_time, 4, 0);
        c.add(dropdownPanel, m_button_generate, 5, 0);

        // Chart
        JScrollPane chartPanel = new JScrollPane(m_chart);
        chartPanel.getVerticalScrollBar().setUnitIncrement(14);

        // General
        c.add(this, m_title, 0, 0, false, false, 1, 0, 15, 10, 10, 10);
        c.add(this, dropdownPanel, 0, 1, false, true, 1, 0, 10);
        c.add(this, chartPanel, 0, 2, true, true, 1, 0.8, 20);
    }

    // Public functions --------------------------------------------------------
}
