package modifiedfilesearch.data;

import java.awt.Color;

/**
 * An object that correlates a String with a Color for display. 
 *
 * @author Brendan Cashman
 */
public interface DisplayString {

    /**
     * Returns the String to be displayed.
     * @return the String to be displayed.
     */
    public String getString();
    
    /**
     * Returns the Color to be displayed.
     * @return the Color to be displayed.
     */
    public Color getColor();
}
