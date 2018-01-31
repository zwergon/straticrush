/* 
 * Copyright 2017 lecomtje.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stratifx.canvas.graphics;

import java.util.Collection;
import java.util.Iterator;

import fr.ifp.kronosflow.geometry.RectD;

/**
 * The GScene is the link between a GWindow and the graphics objects.
 * <p>
 * The GScene defines the viewport and the world extent and holds
 * device-to-world transformation objects. The scene is itself a graphics object
 * (GObject) and as such it may contain geometry.
 * <p>
 * Typical usage:
 *
 * <pre>
 *    // Creating a window
 *    GWindow window = new GWindow (Color.WHITE);
 *
 *    // Creating a scene within the window
 *    GScene scene = new GScene (window);
 *    scene.setWorldExtent (0.0, 0.0, 1000.0, 1000.0);
 * </pre>
 *
 * Setting world extent is optional. If unset it will have the same extent (in
 * floating point coordinates) as the device.
 * <p>
 * When geometry is specified (in GSegments), coordinates are specified in
 * either device coordinates or in world coordinates. Integer coordinates are
 * assumed to be device relative, while floating point coordinates are taken to
 * be world extent relative.
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
public class GScene extends GObject {

    protected ICanvas canvas_;

    protected GViewport viewport_;
    protected GWorldExtent worldExtent_;
    protected GWorldExtent initialWorldExtent_;
    protected GTransformer transformer_;
    
    protected IZoomHandler zoomHandler;

    

    private boolean isAnnotationValid_;
    private GAnnotator annotator_;

    public GScene() {
        super();
    }

    public GScene(String name) {
        super(name);
    }

    protected void initialize(ICanvas canvas, GRect screen, GWorldExtent extent) {

        canvas_ = canvas;
        viewport_ = new GViewport(screen.x, screen.y, screen.width, screen.height);
        worldExtent_ = extent;
        annotator_ = new GAnnotator(this);

        // Create transformer instance
        transformer_ = new GTransformer(viewport_, worldExtent_);

        // Initiate region
        updateRegion();
    }

    protected void initialize(ICanvas canvas, GRect screen) {
        // Default world extent equals window
        double w0[] = {screen.x, screen.y + screen.height};
        double w1[] = {screen.x + screen.width, screen.y + screen.height};
        double w2[] = {screen.x, screen.y};

        initialize(canvas, screen, new GWorldExtent(w0, w1, w2));
    }

    @Override
    public GScene getScene() {
        return this;
    }

    public ICanvas getCanvas() {
        return canvas_;
    }

    /**
     * Return the transformation object of this scene. The transformer object
     * can be used for client-side world-to-device and device-to-world
     * coordinate transformations.
     *
     * @return Current transformation object of this scene.
     */
    public GTransformer getTransformer() {
        return transformer_;
    }

    /**
     * Set viewport for this scene. The viewport is specified in device
     * coordinates. The layout is as follows:
     *
     * <pre>
     *
     *     x0,y0 o-------o  x1,y1
     *           |
     *           |
     *           |
     *     x2,y2 o
     *
     * </pre>
     *
     * It is thus possible to create a skewed viewport, which may be handy in
     * some situations.
     * <p>
     * If the viewport is not set by a client, it will fit the canvas and adjust
     * to it during resize. If it is set by client, it will stay fixed and not
     * adjusted on resize.
     */
    public void setViewport(int x0, int y0, int x1, int y1, int x2, int y2) {

        // Update viewport
        viewport_.set(x0, y0, x1, y1, x2, y2);

        // Set the new region for this scene
        updateRegion();

        transformer_.update(viewport_, worldExtent_);

        // Redraw
        annotator_.reset();
        redraw(getVisibility());
    }

    /**
     * Set viewport to a rectangular area of the screen. The viewport layout is
     * as follows:
     *
     * <pre>
     *
     *             width
     *     x0,y0 o-------o
     *           |
     *    height |
     *           |
     *           o
     *
     * </pre>
     *
     * @param x0 X coordinate of upper left corner of viewport.
     * @param y0 Y coordinate of upper left corner of viewport.
     * @param width Width of viewport.
     * @param height Height of viewport.
     */
    public void setViewport(int x0, int y0, int width, int height) {
        setViewport(x0, y0, x0 + width - 1, y0, x0, y0 + height - 1);
    }

    /**
     * Return current viewport.
     *
     * @return Current viewport of this scene.
     */
    public GViewport getViewport() {
        return viewport_;
    }

    public void initWorldExtent(double w0[], double w1[], double w2[]) {
        initialWorldExtent_ = new GWorldExtent(w0, w1, w2);
    }
    
    public void setZoomHandler(IZoomHandler zoomHandler) {
        this.zoomHandler = zoomHandler;
    }

    /**
     * Set world extent of this scene. The layout is as follows:
     *
     * <pre>
     *        w2 o
     *           |
     *           |
     *           |
     *        w0 o-------o w1
     * </pre>
     *
     * Thus w2 is mapped to viewport (x0,y0), w0 is mapped to (x2,y2) and w1 is
     * mapped to lower right corner of viewport.
     * <p>
     * w0,w1 and w2 are three dimensions, and the world extent can thus be any
     * plane in a 3D space, and the plane may be skewed.
     *
     * @param w0 Point 0 of the new world extent [x,y].
     * @param w1 Point 1 of the new world extent [x,y].
     * @param w2 Point 2 of the new world extent [x,y].
     */
    public void setWorldExtent(double w0[], double w1[], double w2[]) {
        worldExtent_.set(w0, w1, w2);

        adjustWorldExtent();

        transformer_.update(viewport_, worldExtent_);

        redraw(getVisibility());

    }

    /**
     * A convenience method for specifying a orthogonal world extent. The layout
     * is as follows:
     *
     * <pre>
     *           o
     *           |
     *    height |
     *           |
     *     x0,y0 o-------o
     *             width
     *
     * </pre>
     *
     * @param x0 X coordinate of world extent origin.
     * @param y0 Y coordinate of world extent origin.
     * @param width Width of world extent.
     * @param height Height of world extent.
     */
    public void setWorldExtent(double x0, double y0, double width, double height) {
        double w0[] = {x0, y0};
        double w1[] = {x0 + width, y0};
        double w2[] = {x0, y0 + height};

        setWorldExtent(w0, w1, w2);

    }

    /**
     * Adjust the current world extent according to current viewport. This
     * method is called whenever the viewport has changed.
     */
    private void adjustWorldExtent() {

        // Viewport dimensions
        double viewportWidth = (double) viewport_.getWidth();
        double viewportHeight = (double) viewport_.getHeight();

        // World dimensions
        double worldWidth = worldExtent_.getWidth();
        double worldHeight = worldExtent_.getHeight();

        // Compute adjusted width or height
        double newWorldWidth;
        double newWorldHeight;

        if (worldWidth / worldHeight > viewportWidth / viewportHeight) {
            newWorldWidth = worldWidth;
            newWorldHeight = viewportHeight / viewportWidth * worldWidth;
            worldExtent_.extendHeight(newWorldHeight);
        } else {
            newWorldWidth = viewportWidth / viewportHeight * worldHeight;
            newWorldHeight = worldHeight;
            worldExtent_.extendWidth(newWorldWidth);
        }

    }

    /**
     * Return the world extent as specified by the application.
     *
     * @return The world extent as it was specified through setWorldExtent().
     */
    public GWorldExtent getWorldExtent() {
        return worldExtent_;
    }

    /**
     * Update region for this GObject. The region of a GScene is always the
     * viewport extent.
     */
    private void updateRegion() {
        getRegion().set(
                new GRect(viewport_.getX0(),
                        viewport_.getY0(),
                        (int) viewport_.getWidth(),
                        (int) viewport_.getHeight())
        );
        flagRegionValid(true);
    }

    /**
     * Flag the annotation of this scene as valid or invalid. Annotation is set
     * to invalid if annotation is changed somewhere down the tree. This is an
     * instruction to the GWindow to redo the annotation on this scene. When the
     * annotation is redone, this flag is set to valid.
     *
     * @param isAnnotationValid True if the annotation of this scene is valid
     * false otherwise.
     */
    void setAnnotationValid(boolean isAnnotationValid) {
        isAnnotationValid_ = isAnnotationValid;
    }

    /**
     * Check if annotation in this scene is valid.
     *
     * @return True if the annotation is valid, false otherwise.
     */
    boolean isAnnotationValid() {
        return isAnnotationValid_;
    }

    /**
     * Compute positions of the specified positionals.
     *
     * @param positionals Positionals to compute positions of.
     */
    void computePositions(Collection positionals) {
        annotator_.computePositions(positionals);
    }

    /**
     * Compute positions for positional object that are attached to every vertex
     * of its owner.
     *
     * @param positional Positional to compute position for.
     */
    void computeVertexPositions(GPositional positional) {
        annotator_.computeVertexPositions(positional);
    }

    /**
     * Refresh the graphics scene. Only elements that has been changed since the
     * last refresh are affected.
     */
    public void refresh() {
        // This is default setting from window point of view
        refresh(getVisibility(), getRegion());

    }

    public void refresh(int visibilityMask, GRegion damagedRegion) {

        computeTextPositions();

        computeSymbolPositions();

        computeRegion(visibilityMask);

        if (!getRegion().isIntersecting(damagedRegion)) {
            return;
        }

        ICanvas canvas = getCanvas();
        canvas.clear(damagedRegion.getExtent());

        // Rendering pass 1: DATA clippend by scene viewport.
        refreshData(visibilityMask, damagedRegion);

        // Rendering pass 2: ANNOTATION
        refreshAnnotation(visibilityMask, damagedRegion);

    }

    /**
     * Pan a specific device distance.
     *
     * @param dx  Distance to pan in x direction.
     * @param dy  Distance to pan in y direction.
     */
    public void pan (int dx, int dy)
    {
        int x0 = viewport_.getX0() - dx;
        int y0 = viewport_.getY0() - dy;
        int x1 = viewport_.getX3() - dx;
        int y1 = viewport_.getY3() - dy;

        zoom (x0, y0, x1, y1);
    }

    /**
     * Zoom a specified amount around center of viewport.
     *
     * @param zoomFactor Zoom factor. Zoom in with factor < 1.0 and
     *                    out with factor > 1.0.
     */
    public void zoom(double zoomFactor) {
        double x = viewport_.getCenterX();
        double y = viewport_.getCenterY();

        zoom((int) Math.round(x), (int) Math.round(y), zoomFactor);
    }

    /**
     * Zoom a specific amount using specified point as fixed.
     *
     * <ul>
     * <li> Zoom in: zoom (x, y, 0.9);
     * <li> Zoom out: zoom (x, y, 1.1);
     * <li> etc.
     * </ul>
     *
     * @param x X coordinate of fixed point during zoom.
     * @param y Y coordinate of fixed point during zoom.
     * @param zoomFactor Zoom factor.
     */
    public void zoom(int x, int y, double zoomFactor) {
        int x0 = viewport_.getX0();
        int y0 = viewport_.getY0();
        int x1 = viewport_.getX3();
        int y1 = viewport_.getY3();

        double width = viewport_.getWidth();
        double height = viewport_.getHeight();

        x0 += (1.0 - zoomFactor) * (x - x0);
        x1 -= (1.0 - zoomFactor) * (x1 - x);

        y0 += (1.0 - zoomFactor) * (y - y0);
        y1 -= (1.0 - zoomFactor) * (y1 - y);

        zoom(x0, y0, x1, y1);
    }

    /**
     * Zoom into a specific device area.
     *
     * @param x0 X value of first corner of zoom rectangle.
     * @param y0 Y value of first corner of zoom rectangle.
     * @param x1 X value of second corner of zoom rectangle.
     * @param y1 Y value of second corner of zoom rectangle.
     */
    public void zoom(int x0, int y0, int x1, int y1) {
        // Make sure x0,y0 is upper left and x1,y1 is lower right
        if (x1 < x0) {
            int temp = x1;
            x1 = x0;
            x0 = temp;
        }

        if (y1 < y0) {
            int temp = y1;
            y1 = y0;
            y0 = temp;
        }

        // Tranform to world
        double w0[] = transformer_.deviceToWorld(x0, y1);
        double w1[] = transformer_.deviceToWorld(x1, y1);
        double w2[] = transformer_.deviceToWorld(x0, y0);

        zoom(w0, w1, w2);
    }

    /**
     * Zoom into a specified world area.
     *
     * @param w0 First world coordinate of zoom area [x,y].
     * @param w1 Second world coordinate of zoom area [x,y].
     * @param w2 Third world coordinate of zoom area [x,y].
     */
    public void zoom(double w0[], double w1[], double w2[]) {
        //Set new world extent
        setWorldExtent(w0, w1, w2);

        // Rerender the graphics
        refresh();
        
        if ( zoomHandler != null ){
            zoomHandler.update();
        }

    }

    /**
     * Unzoom. Unzooming sets the current world extent back to the initial world
     * extent as specified by the client application by setWorldExtent().
     */
    public void unzoom() {
        if (null != initialWorldExtent_) {
            zoom(initialWorldExtent_.get(0),
                    initialWorldExtent_.get(1),
                    initialWorldExtent_.get(2));
        }

    }

}
