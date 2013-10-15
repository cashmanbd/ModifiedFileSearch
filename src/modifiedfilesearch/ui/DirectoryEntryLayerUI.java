package modifiedfilesearch.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JTextField;
import javax.swing.plaf.LayerUI;

/**
 * A delegate for a JLayer with a JTextField that allows a user to enter
 * a java.nio.File.Path. If the text in the field does not represent a valid
 * directory Path on this system, a small red box with a 'X' will be rendered 
 * in the far right of the text field.
 * @author Brendan Cashman
 */
public class DirectoryEntryLayerUI 
        extends LayerUI<JTextField> {

    /**
     * See LayerUI
     * @param g
     * @param c 
     */
    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);

        JLayer jLayer = (JLayer) c;
        JTextField textField = (JTextField) jLayer.getView();
        evaluateEntry(textField.getText());
        if (!validPath) {
            Graphics2D g2 = (Graphics2D) g.create();

            // Paint the red X in the right of the JTextField.
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w = c.getWidth();
            int h = c.getHeight();
            int s = 8;
            int pad = 4;
            int x = w - pad - s;
            int y = (h - s) / 2;
            g2.setPaint(Color.red);
            g2.fillRect(x, y, s + 1, s + 1);
            g2.setPaint(Color.white);
            g2.drawLine(x, y, x + s, y + s);
            g2.drawLine(x, y + s, x + s, y);

            g2.dispose();
        }
    }
    
    /**
     * 
     * @return flag indicating whether the JTextField current text represents a 
     * valid directory.
     */
    public boolean isEntryValid() {
        return validPath;
    }
    
    /**
     * Evaluates a String to determine if it represents a directory on this
     * system.
     * @param entry String to be evaluated.
     * @return flag indicating success of evaluation.
     */
    private boolean evaluateEntry(String entry){
        try {
            Path userEntry = FileSystems.getDefault().getPath(entry);
            validPath = Files.isDirectory(userEntry, LinkOption.NOFOLLOW_LINKS);
        } catch (InvalidPathException ipe) {
            // Using the InvalidPathException to validate path
            validPath = false;
        }
        return validPath;
    }
    
    private boolean validPath = false;
}

