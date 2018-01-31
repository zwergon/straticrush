/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.interaction;

import fr.ifp.kronosflow.utils.LOGGER;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.interaction.GInteraction;
import stratifx.canvas.interaction.GKeyEvent;
import stratifx.canvas.interaction.GMouseEvent;

/**
 * A default zoom interaction. The following actions are implemented:
 *
 * <ol>
 * <li>Button 1 click at a point in the scene: The scene is zoomed in a certain
 * factor with that point fixed.
 * <li>Button 1 press and keep pressed: The scene is zoomed in continously until
 * the button is released.
 * <li>Button 3 click at a point in the scene: The scene is zoomed out a certain
 * factor with that point fixed.
 * <li>Button 3 press and keep pressed: The scene is zoomed out continously
 * until the button is released.
 * <li>Button 2 click: Unzoom.
 * <li>Button 1 press, drag and release: User creates a rubber band and the
 * scene is zoomed to the specified area.
 * </ol>
 *
 * The <code>ZoomInteraction</code> class lacks the <em>G</em>
 * prefix as it is an optional extension to G and not part of the core classes.
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
public class ZoomInteraction
        implements GInteraction {

    private static final double ZOOM_FACTOR = 0.9;

    private final GScene scene_;
    private final GObject interaction_;
    private final GSegment rubberBand_;

    private boolean active_;

    private int x_[], y_[];
    private int x0_, y0_;
    private double zoomFactor_;

    /**
     * Create a new zoom interaction on the specifed scene. The interaction is
     * activated by GWindow.setInteraction().
     *
     * @see GWindow#startInteraction(GInteraction)
     * @see GWindow#stopInteraction()
     *
     * @param scene Scene of this zoom interaction.
     * @param style Style for the interaction rubber band
     */
    public ZoomInteraction(GScene scene, GStyle style) {
        scene_ = scene;

        // Create a graphic node for holding the interaction graphics
        interaction_ = new GObject("Interaction");

        // Default rubberband style if none provided
        if (style == null) {
            style = new GStyle();
            style.setLineWidth(1);
            style.setForegroundColor(new GColor(0, 0, 0));
            style.setBackgroundColor(null);
        }
        interaction_.setStyle(style);

        // Create and attach rubberband segment
        rubberBand_ = new GSegment();
        interaction_.addSegment(rubberBand_);

        // For the rubberband geometry
        x_ = new int[5];
        y_ = new int[5];

        active_ = false;
    }

    /**
     * Create a new zoom interaction on the specifed scene.
     *
     * @see GWindow#startInteraction(GInteraction)
     * @see GWindow#stopInteraction()
     *
     * @param scene Scene of this zoom interaction.
     */
    public ZoomInteraction(GScene scene) {
        this(scene, null);
    }

    /**
     * Handle mouse events in the canvas.
     *
     * @param eventType Event trigging this method.
     * @param x,y Pointer location.
     */
    public boolean mouseEvent(GScene gscene, GMouseEvent event) {

        if (gscene != scene_) {
            return false;
        }

        switch (event.getButton()) {
            case GMouseEvent.BUTTON_1:
                handleButton1(event);
                break;
            default:
                break;
        }

        return true;
    }

    private void handleButton1(GMouseEvent event) {
        switch (event.type) {

            case GMouseEvent.BUTTON_DOWN:
                active_ = true;
                x0_ = event.x;
                y0_ = event.y;

                scene_.add(interaction_);  // Front

                break;

            case GMouseEvent.BUTTON_UP:
                if ( active_ ) {
                    interaction_.remove();
                    rubberBand_.setGeometry((int[]) null);

                    int dx = Math.abs(event.x - x0_);
                    int dy = Math.abs(event.y - y0_);

                    // If the rubber band is very small, interpret it as a click
                    if (dx > 3 && dy > 3) {
                        scene_.zoom(x0_, y0_, event.x, event.y);
                    }
                    active_ = false;
                }
                break;

            case GMouseEvent.BUTTON_DRAG:
                if ( active_ ) {
                    x_[0] = x0_;
                    x_[1] = event.x;
                    x_[2] = event.x;
                    x_[3] = x0_;
                    x_[4] = x0_;

                    y_[0] = y0_;
                    y_[1] = y0_;
                    y_[2] = event.y;
                    y_[3] = event.y;
                    y_[4] = y0_;

                    rubberBand_.setGeometry(x_, y_);
                    scene_.refresh();
                }
                break;


        }
    }


    
    @Override
    public boolean keyEvent(GScene scene, GKeyEvent event) {
        return false;
    }

    @Override
    public boolean start(GScene scene) {
        return true;
    }

    @Override
    public boolean stop(GScene scene) {
        return true;
    }

}
