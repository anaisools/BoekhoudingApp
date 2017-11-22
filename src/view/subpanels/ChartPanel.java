package view.subpanels;

import view.swingextensions.CustomTable;
import data.Data;
import data.QueryableList;
import java.awt.*;
import java.text.NumberFormat;
import java.util.*;
import javafx.util.Pair;
import javax.swing.*;
import model.Transaction;
import model.Transaction.TRANSACTIONFIELD;
import org.jfree.chart.*;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.*;
import view.swingextensions.CustomGridBag;
import view.swingextensions.CustomTable.COLUMNTYPE;

/**
 * This panel displays a chart of the data. Parameters for the chart type,
 * grouping and time are passed on and then the panel is filled with the
 * according chart. Data need not be passed, the panel gets this by itself.
 *
 * @author Anaïs Ools
 */
public class ChartPanel extends JPanel implements Observer {

    private CHART_TYPE m_chartType;
    private GROUP_BY m_groupBy;
    private TIME m_time;

    JPanel m_chartPanel;

    public enum CHART_TYPE {

        TABLE, POPULATION_CHART, PIE_CHART, LINE_CHART
    };

    public enum GROUP_BY {

        TRANSACTORS, CATEGORIES, PAYMENT_METHOD, PROFITS
    };

    public enum TIME {

        THIS_YEAR, THIS_MONTH, ALL_YEARS, CUSTOM
    };

    // Constructor -------------------------------------------------------------
    public ChartPanel() {
        Data.GetInstance().addAsObserver(this);
        this.setBackground(Color.CYAN);
    }

    // Private functions -------------------------------------------------------
    private void tableLayout() {
        // TABLE - CATEGORIES - THIS_YEAR
        // create table
        ArrayList<Pair<String, COLUMNTYPE>> columns = new ArrayList();
        columns.add(new Pair("Category", COLUMNTYPE.STRING));
        columns.add(new Pair("+", COLUMNTYPE.PRICE));
        columns.add(new Pair("-", COLUMNTYPE.PRICE));
        columns.add(new Pair("Profit", COLUMNTYPE.PRICE));
        CustomTable st = new CustomTable(columns);
        m_chartPanel = st;
        this.setLayout(new BorderLayout());
        this.add(st, BorderLayout.CENTER);
        //this.add(new StatsControlPanel(), BorderLayout.EAST);

        // add data
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        HashMap<String, double[]> categoryData = Data.GetInstance().getTransactions().selectDateAddedByYear(currentYear).groupPriceByCategory();
        Iterator it = categoryData.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, double[]> pair = (Map.Entry) it.next();
            String category = pair.getKey();
            double[] prices = pair.getValue();

            ArrayList<Object> entry = new ArrayList();
            entry.add(category);
            entry.add(prices[0]);
            entry.add(prices[1]);
            entry.add(prices[0] + prices[1]);
            st.addEntry(entry);
            it.remove();
        }
    }

    private void pieChartLayout() {
        // PIE - CATEGORIES - THIS_YEAR

        // Get data
        int year = Calendar.getInstance().get(Calendar.YEAR);
        QueryableList entries = Data.GetInstance().getTransactions().selectDateAddedByYear(year);

        // Group data by categories
        Map<String, Double> map = new HashMap();
        for (Transaction t : entries) {
            String category = (String) t.get(TRANSACTIONFIELD.CATEGORY);
            double price = (double) t.get(TRANSACTIONFIELD.PRICE);
            if (price > 0) {
                if (map.containsKey(category)) {
                    map.put(category, map.get(category) + price);
                } else {
                    map.put(category, price);
                }
            }
        }
        System.out.println(map.toString());

        // Pie data set
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        dataset.sortByValues(org.jfree.util.SortOrder.DESCENDING);

        // Pie chart
        JFreeChart chart = ChartFactory.createPieChart(
                "Category profit from this year", // chart title
                dataset, // data
                false, // include legend
                false,
                false
        );
        chart.setBackgroundPaint(Color.white);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setIgnoreZeroValues(true);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0} = {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()
        ));
        plot.setCircular(true);
        plot.setNoDataMessage("No data available");
        m_chartPanel = new org.jfree.chart.ChartPanel(chart);

        this.setLayout(new BorderLayout());
        this.add(m_chartPanel, BorderLayout.CENTER);

    }

    private void lineChartLayout() {
        System.out.println("Hello");
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        HashMap<Integer, double[]> categoryData = Data.GetInstance().getTransactions().selectDateAddedByYear(currentYear).groupPriceByMonth();
        Iterator it = categoryData.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, double[]> pair = (Map.Entry) it.next();
            int month = pair.getKey();
            double[] prices = pair.getValue();
            System.out.print(month + ":\t");
            System.out.print(Math.round(prices[0]) + "\t");
            System.out.print(Math.round(prices[1]) + "\t");
            System.out.println(Math.round(prices[0] + prices[1]));
        }
    }

    // Public functions --------------------------------------------------------
    /**
     * Set the parameters that the chart should display. Afterward, these
     * parameters can be applied with the Update-function.
     *
     * @param ct
     * @param gb
     * @param t
     */
    public void setChartParameters(CHART_TYPE ct, GROUP_BY gb, TIME t) {
        m_chartType = ct;
        m_groupBy = gb;
        m_time = t;
    }

    /**
     * Update the chart when the data changes.
     *
     * @param o
     * @param o1
     */
    @Override
    public void update(Observable o, Object o1) {
        if (m_chartType == null || m_groupBy == null || m_time == null) {
            return;
        }
        if (m_chartType == CHART_TYPE.TABLE && m_groupBy == GROUP_BY.CATEGORIES && m_time == TIME.THIS_YEAR) {
            tableLayout();
        } else if (m_chartType == CHART_TYPE.LINE_CHART) {
            lineChartLayout();
        } //        else if (m_chartType == CHART_TYPE.PIE_CHART) {
        //            pieChartLayout();
        //        } 
        else {
            tableLayout();
        }
    }
}
