package modifiedfilesearch.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.plaf.LayerUI;

import modifiedfilesearch.data.DisplayString;

/**
 * A delegate for a JLayer with a JPanel that decorates said JPanel with the
 * DisplayString that are added to Queue provided in the constructor.
 *
 * @author Brendan
 */
public class DisplayStringLayerUI extends LayerUI<JComponent> implements ActionListener {

    /**
     * The only constructor for this LayerUI, a collection of DisplayString
     * objects is needed in order to decorate the JPanel.
     *
     * @param dspStrQueue
     */
    public DisplayStringLayerUI(Queue<DisplayString> dspStrQueue) {
        super();
        this.dspStrQueue = dspStrQueue;
        this.localList = new ArrayList<DisplayString>();
    }

    /**
     * See LayerUI
     *
     * @param g
     * @param c
     */
    @Override
    public void paint(Graphics g, JComponent c) {
        int w = c.getWidth();
        int h = c.getHeight();

        // Paint the view.
        super.paint(g, c);

        Graphics2D g2 = (Graphics2D) g.create();

        float fade = (float) fadeCount / (float) fadeLimit;
        Composite urComposite = g2.getComposite();
        // Gray it out.
        g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, .75f * fade));
        g2.fillRect(0, 0, w, h);
        // reset composite
        g2.setComposite(urComposite);
        if (isShowingStatus) {
            float buffer = 5.f;
            double yTInc = 14;
            float yTrack = 0.f;
            int startIndex = 0;
            double space = localList.size() * yTInc;
            // The most recent DisplayString(s) will be rendered in the available
            // space. If the space grows or shrinks (due to resizing) this 
            // will render accordingly.
            if (space > h) {
                startIndex = (int) Math.ceil((space - h) / yTInc);
            }
            for (int i = startIndex; i < localList.size(); i++) {
                DisplayString dispStr = localList.get(i);
                yTrack += yTInc;
                String pathString = dispStr.getString();
                Font font = new Font("Monospaced", Font.BOLD, 13);

                // If the length of the String exceeds, the available space,
                // abbreviate it.
                Rectangle2D rect = font.getStringBounds(pathString, g2.getFontRenderContext());
                while (rect.getWidth() > w) {
                    pathString = shortenStringInMiddle(pathString, pathString.length() - 6);
                    rect = font.getStringBounds(pathString, g2.getFontRenderContext());
                }
                g2.setFont(font);
                FontRenderContext frc = g2.getFontRenderContext();
                TextLayout tl = new TextLayout(pathString, font, frc);
                if (isTextOutlined) {
                    AffineTransform at = new AffineTransform();
                    at.setToTranslation(buffer, yTrack);
                    Shape outline = tl.getOutline(at);
                    g2.setColor(Color.BLACK);
                    g2.draw(outline);
                }
                g2.setColor(dispStr.getColor());
                tl.draw(g2, buffer, yTrack);//g2.drawString(pathString, buffer, yTrack);
            }
        }
        g2.dispose();
    }

    /**
     * The current JComponent will "fade" to a dark grey and then begin to 
     * display the DisplayStrings on top of the JPanel.
     */
    public void start() {
        isFadingFinished = false;
        isFadingOut = true;
        isShowingStatus = false;
        isRunning = true;
        fadeCount = 0;
        renderTimer = new Timer(fadeDelay, this);
        renderTimer.setActionCommand(RENDER_ACTION_COMMAND);
        // Start the animation rendering on AWT-Event thread.
        renderTimer.start();
    }
    
    /**
     * Stops the display of the DisplayStrings
     */
    public void stop() {
        localList.clear();
        isFadingFinished = false;
        isFadingOut = false;
        isShowingStatus = false;
        isRunning = true;
        setFPS(fadeDelay);
    }

    /**
     * 
     */
    public void pauseScroll() {
        isRunning = false;
    }

    /**
     * 
     */
    public void resumeScroll() {
        isRunning = true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() != null
                && e.getActionCommand().equals(RENDER_ACTION_COMMAND)) {
            firePropertyChange(RENDER_ACTION_COMMAND, 0, 1);
            if (isFadingFinished) {
                if (!isShowingStatus) {
                    isShowingStatus = true;
                    //throttle rendering so animation doesn't spam the screen
                    setFPS(10);
                }
                if (dspStrQueue.peek() != null && isRunning) {
                    localList.add(dspStrQueue.poll());
                }
            } else if ( isFadingOut && fadeCount < fadeLimit) {
                fadeCount++;
            } else if ( !isFadingOut && fadeCount > 0) {
                fadeCount--;
            } else {
                isFadingFinished = true;
                if (isFadingOut)
                    isFadingOut = false;
            }
        }
    }
    
    /**
     * Toggles whether the outline of the text is drawn.
     * @param isOutlined - Flag to display outlines or not.
     */
    public void outlineText(boolean isOutlined) {
        isTextOutlined = isOutlined;
        firePropertyChange(RENDER_ACTION_COMMAND, 0, 1);
    }

    /**
     * Sets the rate at which the Strings are rendered. A value of 
     * zero stops updates.
     * @param fps - Number of Strings to display a second.
     */
    public void setFPS(int fps) {
        if (!SwingUtilities.isEventDispatchThread()) {
            return;
        }
        if (fps <= 0) {
            renderTimer.stop();
            renderTimer.setDelay(0);
        } else {
            int newDelay = (int) (1000 / fps);
            if (newDelay != renderTimer.getDelay()) {
                renderTimer.stop();
                renderTimer.setDelay(newDelay);
                renderTimer.start();
            }
        }
    }

    @Override
    public void applyPropertyChange(PropertyChangeEvent pce, JLayer l) {
        if (RENDER_ACTION_COMMAND.equals(pce.getPropertyName())) {
            l.repaint();
        }
    }

    /**
     * Shortens a string to the length provided, replacing the middle characters
     * with "...".
     *
     * @param origStr
     * @param newLength
     * @return
     */
    public static String shortenStringInMiddle(String origStr, int newLength) {
        String middle = "...";

        int targetLength = newLength - middle.length();
        int startOffset = targetLength / 2 + targetLength % 2;
        int endOffset = origStr.length() - targetLength / 2;

        StringBuilder builder = new StringBuilder(newLength);
        builder.append(origStr.substring(0, startOffset)).append(middle).append(origStr.substring(endOffset));

        return builder.toString();
    }
    
    private final List<DisplayString> localList;
    private boolean isFadingFinished;
    private boolean isRunning;
    private boolean isShowingStatus;
    private boolean isTextOutlined = false;
    private boolean isFadingOut = false;
    private int fadeCount;
    private int fadeLimit = 20;
    private int fadeDelay = 1000 / 24;
    private final Queue<DisplayString> dspStrQueue;
    private Timer renderTimer;
    private static final String RENDER_ACTION_COMMAND = "render";
}
