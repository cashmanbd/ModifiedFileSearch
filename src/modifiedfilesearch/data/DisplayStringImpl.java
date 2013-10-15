package modifiedfilesearch.data;

import java.awt.Color;

/**
 * Immutable implementation of the DisplayString class.
 *
 * @author Brendan Cashman
 */
public class DisplayStringImpl implements DisplayString {

    /**
     * Constructs the DisplayStringImpl
     *
     * @param str - String to be displayed
     * @param color - Color to display the String
     */
    public DisplayStringImpl(String str, Color color) {
        this.str = str;
        this.color = color;
    }

    @Override
    public String getString() {
        return str;
    }

    @Override
    public Color getColor() {
        return color;
    }
    private final String str;
    private final Color color;
}
