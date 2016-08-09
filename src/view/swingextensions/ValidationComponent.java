package view.swingextensions;

/**
 * Abstract interface such that every validation component can be adressed in
 * the same way.
 *
 * @author Anaïs Ools
 */
public interface ValidationComponent {

    public boolean isValid();

    public Object getValue();

    public void setValue(Object text);

    public void forceValidate();
}
