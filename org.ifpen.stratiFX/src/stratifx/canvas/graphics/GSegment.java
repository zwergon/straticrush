package stratifx.canvas.graphics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import fr.ifp.kronosflow.geometry.Geometry;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.explicit.ExplicitPolyLine;
import fr.ifp.kronosflow.utils.LOGGER;

/**
 * Class for holding a polyline. <tt>GSegment</tt>s are contained by
 * <tt>GObjects</tt>. They can have its own rendering style (<tt>GStyle</tt>) or
 * inherit style from its parent <tt>GObject</tt> if not specified.
 *
 */
public class GSegment
        implements GStyleListener {

    private GObject owner_;       // Owner
    private int x_[];
    private int y_[];
    private GImage vertexImage_;
    private GRect rectangle_;   // Bounding box
    private Object userData_;    // Whatever app assoc with graphics
    private GStyle style_;       // As applied to this object
    private GStyle actualStyle_; // Adjusted for owner inherits
    private List<GText> texts_;       // of GText
    private GImage texture_;

    /**
     * Create a GSegment.
     */
    public GSegment() {
        owner_ = null;
        x_ = null;
        y_ = null;
        rectangle_ = null;
        texts_ = null;
        vertexImage_ = null;
        texture_ = null;

        style_ = null;
        actualStyle_ = new GStyle();
    }

    /**
     * Return the owner GObject of this GSegment. If the GSegment has not been
     * added to a GObject, owner is null.
     *
     * @return GObject owner of this GSegment, or null if not attacted to one.
     */
    public GObject getOwner() {
        return owner_;
    }

    /**
     * Set the owner of this GSegment.
     *
     * @param owner New owner of this GSegment.
     */
    void setOwner(GObject owner) {
        owner_ = owner;
        updateContext();
    }

    /**
     * Convenience method to get the scene of the graphics hierarchy of this
     * GSegment.
     *
     * @return Scene of the graphics hierarchy of this GSegment (or null if it
     * is somehow not attached to a scene).
     */
    GScene getScene() {
        return owner_ == null ? null : owner_.getScene();
    }

    /**
     * Set user data of this GSegment.
     *
     * @param userData User data of this GSegment.
     */
    public void setUserData(Object userData) {
        userData_ = userData;
    }

    /**
     * Return user data of this GSegment.
     *
     * @return User data of this GSegment.
     */
    public Object getUserData() {
        return userData_;
    }

    /**
     * Return device X coordinates of this segment.
     *
     * @return Device X coordinates of this segment.
     */
    public int[] getX() {
        return x_;
    }

    /**
     * Return device X coordinates of this GSegment.
     *
     * @return Device X coordinates of this segment.
     */
    public int[] getY() {
        return y_;
    }

    public double[][] getXY() {

        double[][] xy = new double[2][x_.length];
        for (int i = 0; i < x_.length; i++) {
            xy[0][i] = x_[i];
            xy[1][i] = y_[i];
        }

        return xy;
    }

    public double[][] getWorldXY() {

        GTransformer transformer = owner_.getScene().getTransformer();

        double[][] xy = new double[2][x_.length];
        for (int i = 0; i < x_.length; i++) {
            double[] wCoord = transformer.deviceToWorld(x_[i], y_[i]);
            xy[0][i] = wCoord[0];
            xy[1][i] = wCoord[1];
        }

        return xy;
    }

    /**
     * return values associated at each (x,y) point.
     */
    public GImage getTexture() {
        return texture_;
    }

    public void useTexture(boolean useTexture) {
        if (useTexture) {
            texture_ = new GFXTexture();
        } else {
            texture_ = null;
        }
    }

    public void updateTexture() {
        if (null != texture_) {

            int[] data = new int[rectangle_.width * rectangle_.height];
            for (int j = 0; j < rectangle_.height; j++) {
                int y = rectangle_.y + j;

                int offset = j * rectangle_.width;

                for (int i = 0; i < rectangle_.width; i++) {
                    int x = rectangle_.x + i;

                    GColor gcolor = owner_.getColor(x, y);
                    int color = 0;
                    if (gcolor != null) {
                        color = (gcolor.getAlpha() << 24)
                                | (gcolor.getRed() << 16)
                                | (gcolor.getGreen() << 8)
                                | gcolor.getBlue();
                    }
                    data[offset + i] = color;
                }
            }

            texture_.setImage(rectangle_.width, rectangle_.height, data);
        }
    }

    /**
     * Return number of points in the polyline of this GSegment.
     *
     * @return Number of points in this GSegment. May be 0 if the GSegment is
     * empty.
     */
    public int size() {
        return x_ == null ? 0 : x_.length;
    }

    /**
     * Return rectangle bounding box of this GSegment. Covers GSegment geometry
     * only, not associated annotations, images or AWT components.
     *
     * @return Rectangle bounding box of the geometry of this GSegment.
     */
    public GRect getRectangle() {
        return rectangle_;
    }

    /**
     * Return region of this GSegment including associated annotations, images
     * and AWT components.
     *
     * @return Region of this GSegment includings its sub components.
     */
    GRegion getRegion() {
        GRegion region = new GRegion();

        // First add geometry part
        if (rectangle_ != null) {
            region.union(rectangle_);
        }

        // Add extent of all texts
        if (texts_ != null) {
            for (Iterator<GText> i = texts_.iterator(); i.hasNext();) {
                GText text = i.next();
                region.union(text.getRectangle());
            }
        }

        return region;
    }

    /**
     * Return the X center of the geometry of this GSegment.
     *
     * @return X center of the geometry of this GSegment.
     */
    int getCenterX() {
        return rectangle_ == null ? 0 : rectangle_.getCenterX();
    }

    /**
     * Return the Y center of the geometry of this GSegment.
     *
     * @return Y center of the geometry of this GSegment.
     */
    int getCenterY() {
        return rectangle_ == null ? 0 : rectangle_.getCenterY();
    }

    /**
     * This method is called in a response to updated geometry, new parent or
     * new style settings.
     */
    private void updateContext() {

        // Nothing to update if we are not in the tree
        if (owner_ == null) {
            return;
        }

        // Flag owner region as invalid
        owner_.flagRegionValid(false);

        // Not more we can do if we're not in the scene
        if (owner_.getScene() == null) {
            return;
        }

        // If segment has text, annotation must be updated
        if (texts_ != null) {
            owner_.getScene().setAnnotationValid(false);
        }

        if (texture_ != null) {
            updateTexture();
        }

    }

    /**
     * Compute the rectangle bounding box of this GSegment.
     */
    private void computeRectangle() {

        // Actual rectangle depends on line width
        int lineWidth = actualStyle_.getLineWidth() - 1;

        if (x_ != null) {
            if (rectangle_ == null) {
                rectangle_ = new GRect();
            }

            int vImageWidth = 0;
            if (vertexImage_ != null) {
                vImageWidth += Math.max(
                        vertexImage_.rectangle_.width,
                        vertexImage_.rectangle_.height) / 2;
            }

            rectangle_.set(x_, y_);
            rectangle_.expand(vImageWidth + lineWidth + 1, vImageWidth + lineWidth + 1);
        }
    }

    /**
     * Set single point device coordinate geometry.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    public void setGeometry(int x, int y) {
        setGeometry(new int[]{x}, new int[]{y});
    }

    /**
     * Set two point (line) device coordinate geometry.
     *
     * @param x0 X coordinate of first end point.
     * @param y0 Y coordinate of first end point.
     * @param x1 X coordinate of second end point.
     * @param y1 Y coordinate of second end point.
     */
    public void setGeometry(int x0, int y0, int x1, int y1) {
        setGeometry(new int[]{x0, x1}, new int[]{y0, y1});
    }

    /**
     * Set polyline device coordinate geometry.
     *
     * @param x X coordinates.
     * @param y Y coordinates.
     */
    public void setGeometry(int[] x, int[] y) {

        // Update geometry
        if (x == null) {
            x_ = null;
            y_ = null;
            rectangle_ = null;
        } else {
            int nPoints = x.length;

            // Reallocate
            if (x_ == null || x_.length != nPoints) {
                x_ = new int[nPoints];
                y_ = new int[nPoints];
            }

            for (int i = 0; i < nPoints; i++) {
                x_[i] = x[i];
                y_[i] = y[i];
            }

            // Update bounding box
            computeRectangle();
        }

        updateContext();
    }

    /**
     * Set polyline device coordinate geometry.
     *
     * @param xy Polyline geometry [x,y,x,y,...]. null can be specified to
     * indicate that the present geometry should be removed.
     */
    public void setGeometry(int[] xy) {

        // Update geometry
        if (xy == null) {
            x_ = null;
            y_ = null;
            rectangle_ = null;
        } else {
            int nPoints = xy.length / 2;

            // Reallocate
            if (x_ == null || x_.length != nPoints) {
                x_ = new int[nPoints];
                y_ = new int[nPoints];
            }

            for (int i = 0; i < nPoints; i++) {
                x_[i] = xy[i * 2 + 0];
                y_[i] = xy[i * 2 + 1];
            }

            // Update bounding box
            computeRectangle();
        }

        updateContext();
    }

    /**
     * Set single point world coordinate geometry
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    public void setWorldGeometry(double x, double y) {
        setWorldGeometry(
                new double[]{x},
                new double[]{y}
        );
    }

    /**
     * Set two point (line) world coordinate geometry.
     *
     * @param x0 X coordinate of first end point.
     * @param y0 Y coordinate of first end point.
     * @param x1 X coordinate of second end point.
     * @param y1 Y coordinate of second end point.
     */
    public void setWorldGeometry(double x0, double y0,
            double x1, double y1) {
        setWorldGeometry(new double[]{x0, x1},
                new double[]{y0, y1});
    }

    /**
     * Set polyline world coordinate geometry. TODO: Look at the implementation
     *
     * @param wx X coordinates.
     * @param wy Y coordinates.
     */
    public void setWorldGeometry(double x[], double y[]) {
        GTransformer transformer = owner_.getScene().getTransformer();

        int nPoints = x.length;

        double[] world = new double[2];
        int[] device = new int[2];
        int[] devx = new int[nPoints];
        int[] devy = new int[nPoints];

        for (int i = 0; i < x.length; i++) {
            world[0] = x[i];
            world[1] = y[i];

            device = transformer.worldToDevice(world);

            devx[i] = device[0];
            devy[i] = device[1];
        }

        setGeometry(devx, devy);
    }

    /**
     * Set polyline world coordinate geometry. (set to 0.0).
     *
     * @param xy Polyline geometry [x,y,x,y,...].
     */
    public void setWorldGeometryXY(double[] xy) {
        GTransformer transformer = owner_.getScene().getTransformer();

        int nPoints = xy.length / 2;

        int[] devxy = new int[nPoints * 2];

        double[] world = new double[2];
        int[] device = new int[2];

        int wIndex = 0;
        int dIndex = 0;

        for (int i = 0; i < nPoints; i++) {
            world[0] = xy[wIndex + 0];
            world[1] = xy[wIndex + 1];

            device = transformer.worldToDevice(world);

            devxy[dIndex + 0] = device[0];
            devxy[dIndex + 1] = device[1];

            wIndex += 2;
            dIndex += 2;
        }

        setGeometry(devxy);
    }

    /**
     *
     * @return
     */
    public int[] getGeometry() {
        int[] xy = new int[x_.length * 2];
        for (int i = 0; i < x_.length; i++) {
            xy[2 * i] = x_[i];
            xy[2 * i + 1] = y_[i];
        }

        return xy;
    }

    /**
     * Set new style for this segment. Style elements not explicitly set within
     * this GStyle object are inherited from parent objects. Default style is
     * null, i.e. all style elements are inherited from parent.
     *
     * @param style Style for this segment (or null if the intent is to unset
     * the current style).
     */
    public void setStyle(GStyle style) {
        if (style_ != null) {
            style_.removeListener(this);
        }

        style_ = style;

        if (style_ != null) {
            style_.addListener(this);
        }

        updateStyle();
    }

    /**
     * Return style for this segment. This is the style set by setStyle() and
     * not necesserily the style as it appears on screen as unset style elements
     * are inherited from parents.
     *
     * @return Style of this GSegment as specified with setStyle(), (or null if
     * no style has been provided).
     */
    public GStyle getStyle() {
        return style_;
    }

    /**
     * These are the actual style used for this GSegment when inheritance for
     * unset values are resolved. TODO: Make this public?
     *
     * @return Actual style for this segment.
     */
    GStyle getActualStyle() {
        return actualStyle_;
    }

    /**
     * Resolve unset values in segment style.
     */
    void updateStyle() {
        // Invalidate all style
        actualStyle_ = new GStyle();

        // Update with owner style
        if (owner_ != null) {
            actualStyle_.update(owner_.getActualStyle());
        }

        // Update (and possibly override) with present style
        if (style_ != null) {
            actualStyle_.update(style_);
        }

        // Update children object style
        if (texts_ != null) {
            for (Iterator<GText> i = texts_.iterator(); i.hasNext();) {
                GText text = i.next();
                text.updateStyle();
            }
        }

        // TODO: This might not be necessary for all style changes
        computeRectangle();
        updateContext();
    }

    /**
     * Find region of a set of positionals.
     *
     * @param positionals Positionals to find region of.
     * @return Region of specified positionals.
     */
    private GRegion findRegion(Collection<? extends GPositional> positionals) {
        GRegion region = new GRegion();

        for (Iterator<?> i = positionals.iterator(); i.hasNext();) {
            GPositional positional = (GPositional) i.next();
            if (positional.isVisible()) {
                region.union(positional.getRectangle());
            }
        }

        return region;
    }

    /**
     * Add a text element to this segment.
     * <p>
     * Text elements without line position hint will be associated with the n'th
     * segment coordinate according to the number of texts added.
     *
     * @param text Text element to add.
     */
    public void addText(GText text) {
        // Create if first text
        if (texts_ == null) {
            texts_ = new ArrayList<GText>();
        }

        // Add to list
        texts_.add(text);
        text.setSegment(this);

        // Flag owner region as invalid and annotation too
        if (owner_ != null) {
            owner_.flagRegionValid(false);
            if (owner_.getScene() != null) {
                owner_.getScene().setAnnotationValid(false);
            }
        }
    }

    /**
     * Set text element of this segment. Replaces all current text elements of
     * this segment.
     *
     * @param text Text element to set.
     */
    public void setText(GText text) {
        removeText();
        addText(text);
    }

    /**
     * Return all text elements of this segment.
     *
     * @return All text elements of this segment (or null if none).
     */
    public List<GText> getTexts() {
        return texts_;
    }

    /**
     * Return the first text element of this segment. Convenient when caller
     * knows that there are exactly one text element.
     *
     * @return First text elements of this segment (or null if none).
     */
    public GText getText() {
        return texts_ != null ? (GText) texts_.iterator().next() : null;
    }

    /**
     * Remove all text elements set on this segment.
     */
    public void removeText() {
        // Nullify texts
        texts_ = null;
    }

    /**
     * Set image to associate with every vertex of this GSegment.
     *
     * @param image Image to decorate every vertex of this polyline (or null to
     * turn off this feature).
     */
    public void setVertexImage(GImage image) {
        vertexImage_ = image;
    }

    /**
     * Return the image that is to be associated with all vertices of this
     * GSegment. Return null if none is specified.
     *
     * @return Image that decorates every vertex of this GSegment (or null if
     * not specified).
     */
    public GImage getVertexImage() {
        return vertexImage_;
    }

    /**
     * Check if this segment is filled. The <em>fill</em> property depends on
     * the style settings of the segment and it is used to determine segment
     * intersections.
     *
     * @return True of the segment is filled, false otherwise.
     */
    boolean isFilled() {
        return actualStyle_.isDefiningFill();
    }

    /**
     * Check if the geometry of this GSegment is inside the specified rectangle.
     *
     * @param x0 X coordinate of upper left corner of rectangle.
     * @param y0 Y coordinate of upper left corner of rectangle.
     * @param x1 X coordinate of lower right corner of rectangle.
     * @param y1 Y coordinate of lower right corner of rectangle.
     * @return True if the geometry of this GSegment is completely inside the
     * specified rectangle, false otherwise. If this GSegment has no geometry,
     * false is returned.
     */
    boolean isInsideRectangle(int x0, int y0, int x1, int y1) {
        if (rectangle_ == null) {
            return false;
        }
        GBox box = new GBox(rectangle_);
        return box.isInsideOf(new GBox(x0, y0, x1, y1));
    }

    /**
     * Check if the geometry of this GSegment intersects the specified
     * rectangle.
     *
     * @param x0 X coordinate of upper left corner of rectangle.
     * @param y0 Y coordinate of upper left corner of rectangle.
     * @param x1 X coordinate of lower right corner of rectangle.
     * @param y1 Y coordinate of lower right corner of rectangle.
     * @return True if the geometry of this GSegment intersects the specified
     * rectangle, false otherwise. If the GSegment has no geometry, false is
     * returned.
     */
    boolean isIntersectingRectangle(int x0, int y0, int x1, int y1) {
        if (x_ == null) {
            return false;
        }

        return (isFilled()
                && Geometry.isPolygonIntersectingRectangle(x_, y_, x0, y0, x1, y1))
                || (!isFilled()
                && Geometry.isPolylineIntersectingRectangle(x_, y_, x0, y0, x1, y1));
    }

    /**
     * Check if this GSegment intersects the specified point.
     *
     * @param x X coordinate of point.
     * @param y Y coordinate of point.
     * @return True if this GSegment intersects the specified point, false
     * otherwise. If the GSegment has no geometry, false is returned.
     */
    boolean isIntersectingPoint(int x, int y) {
        if (x_ == null) {
            return false;
        }

        return (isFilled()
                && Geometry.isPointInsidePolygon(x_, y_, x, y))
                || (!isFilled()
                && Geometry.isPolylineIntersectingRectangle(x_, y_,
                        x - 1, y - 1, x + 1, y + 1));
    }

    /**
     * Called when the style of this object is changed.
     *
     * @param style Style that has changed.
     */
    public void styleChanged(GStyle style) {
        updateStyle();
    }

}
