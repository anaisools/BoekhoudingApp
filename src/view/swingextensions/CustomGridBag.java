package view.swingextensions;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;

/**
 * Making GridBagLayout more userfriendly.
 *
 * @author Anaïs Ools
 */
public class CustomGridBag {

    private final GridBagConstraints m_gbc;

    public CustomGridBag() { // private because singleton
        m_gbc = new GridBagConstraints();
    }

    public void add(JPanel panel, Component c, int x, int y) {
        if (panel.getLayout().getClass() != GridBagLayout.class) {
            panel.setLayout(new GridBagLayout());
        }
        m_gbc.gridx = x;
        m_gbc.gridy = y;
        panel.add(c, m_gbc);
    }

    public void add(JPanel panel, Component c, int x, int y, boolean hFill, boolean vFill, double xWeight, double yWeight, Insets in) {
        setFill(hFill, vFill);
        setWeight(xWeight, yWeight);
        setInsets(in);
        add(panel, c, x, y);
    }

    public void add(JPanel panel, Component c, int x, int y, boolean hFill, boolean vFill, double xWeight, double yWeight, int paddingTop, int paddingBottom, int paddingLeft, int paddingRight) {
        setFill(hFill, vFill);
        setWeight(xWeight, yWeight);
        setInsets(paddingTop, paddingBottom, paddingLeft, paddingRight);
        add(panel, c, x, y);
    }

    public void add(JPanel panel, Component c, int x, int y, boolean hFill, boolean vFill, double xWeight, double yWeight, int padding) {
        setFill(hFill, vFill);
        setWeight(xWeight, yWeight);
        setInsets(padding);
        add(panel, c, x, y);
    }

    public void add(JPanel panel, Component c, int x, int y, boolean hFill, boolean vFill, double xWeight, double yWeight) {
        setFill(hFill, vFill);
        setWeight(xWeight, yWeight);
        add(panel, c, x, y);
    }

    public void setAnchor(int anchor) {
        m_gbc.anchor = anchor;
    }

    public void setInsets(Insets in) {
        m_gbc.insets = in;
    }

    public void setInsets(int padding) {
        m_gbc.insets = new Insets(padding, padding, padding, padding);
    }

    public void setInsets(int paddingTop, int paddingBottom, int paddingLeft, int paddingRight) {
        m_gbc.insets = new Insets(paddingTop, paddingLeft, paddingBottom, paddingRight);
    }

    public void setFill(boolean hFill, boolean vFill) {
        if (hFill && vFill) {
            m_gbc.fill = GridBagConstraints.BOTH;
        } else if (hFill) {
            m_gbc.fill = GridBagConstraints.HORIZONTAL;
        } else if (vFill) {
            m_gbc.fill = GridBagConstraints.VERTICAL;
        }
    }

    public void setWeight(double xWeight, double yWeight) {
        m_gbc.weightx = xWeight;
        m_gbc.weighty = yWeight;
    }

    public void setCells(int xCells, int yCells) {
        m_gbc.gridwidth = xCells;
        m_gbc.gridheight = yCells;
    }

}
