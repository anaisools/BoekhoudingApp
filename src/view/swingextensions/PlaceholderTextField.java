package view.swingextensions;

import java.awt.*;
import javax.swing.JTextField;

/**
 * This class extends a JTextField such that it can have placeholder.
 *
 * @author Anaïs Ools
 */
public class PlaceholderTextField extends JTextField {

    private String m_placeholder;

    public void setPlaceholder(String placeholder) {
        m_placeholder = placeholder;
    }

    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);

        // set placeholder
        if (m_placeholder == null || m_placeholder.length() == 0 || getText().length() > 0) {
            return;
        }
        final Graphics2D g = (Graphics2D) pG;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(getDisabledTextColor());
        g.drawString(m_placeholder, getInsets().left, pG.getFontMetrics().getMaxAscent() + getInsets().top);
    }

}
