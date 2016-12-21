package stratifx.canvas.graphics;



import java.util.Collection;
import java.util.Iterator;

import fr.ifp.kronosflow.geometry.RectD;



/**
 * The GScene is the link between a GWindow and the graphics objects.
 * <p>
 * The GScene defines the viewport and the world extent and holds
 * device-to-world transformation objects. The scene is itself a
 * graphics object (GObject) and as such it may contain geometry.
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
 * Setting world extent is optional. If unset it will have the same
 * extent (in floating point coordinates) as the device.
 * <p>
 * When geometry is specified (in GSegments), coordinates are specified
 * in either device coordinates or in world coordinates. Integer coordinates
 * are assumed to be device relative, while floating point coordinates are
 * taken to be world extent relative.
 * 
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */   
public class GScene extends GObject 
{
	
  protected ICanvas          canvas_;
  
  protected GViewport         viewport_;
  protected GWorldExtent      worldExtent_;
  protected GTransformer      transformer_;
  
  private boolean             isAnnotationValid_;
  private GAnnotator    annotator_;
  

  
  public GScene ( ){
	  super();
  }
  
  public GScene( String name ) {
	  super(name);
  }

  protected void initialize( ICanvas canvas, GRect screen, GWorldExtent extent) {

	  canvas_      = canvas;
	  viewport_    = new GViewport ( screen.x, screen.y, screen.width, screen.height);
	  worldExtent_ = extent;
	  annotator_   = new GAnnotator (this);


	  // Create transformer instance
	  transformer_ = new GTransformer (viewport_, worldExtent_);


	  // Initiate region
	  updateRegion();
  }
  
  protected void initialize( ICanvas canvas, GRect screen) {
	  // Default world extent equals window
	  double w0[] = {screen.x,                    screen.y + screen.height };
	  double w1[] = {screen.x+ screen.width,      screen.y + screen.height };
	  double w2[] = {screen.x,                    screen.y };
	
	  initialize(  canvas, screen, new GWorldExtent (w0, w1, w2) );
  }

  
 
  @Override
  public GScene getScene(){
	  return this;
  }
  
  
  public ICanvas getCanvas(){
	  return canvas_;
  }
  
  
 
  
  /**
   * Return the transformation object of this scene. The transformer
   * object can be used for client-side world-to-device and device-to-world
   * coordinate transformations.
   * 
   * @return  Current transformation object of this scene.
   */
  public GTransformer getTransformer()
  {
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
   * It is thus possible to create a skewed viewport, which may be handy
   * in some situations.
   * <p>
   * If the viewport is not set by a client, it will fit the canvas and
   * adjust to it during resize. If it is set by client, it will stay
   * fixed and not adjusted on resize.
   */
  public void setViewport (int x0, int y0, int x1, int y1, int x2, int y2)
  {

    // Update viewport
    viewport_.set (x0, y0, x1, y1, x2, y2);

    // Set the new region for this scene
    updateRegion();
    
   
    transformer_.update (viewport_, worldExtent_);

    // Redraw
    annotator_.reset();
    redraw (getVisibility());
  }



  /**
   * Set viewport to a rectangular area of the screen. The viewport
   * layout is as follows:
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
   * @param x0      X coordinate of upper left corner of viewport.
   * @param y0      Y coordinate of upper left corner of viewport.   
   * @param width   Width of viewport.
   * @param height  Height of viewport.
   */
  public void setViewport (int x0, int y0, int width, int height)
  {
    setViewport (x0, y0, x0+width-1, y0, x0, y0+height-1);
  }
  

  
  /**
   * Return current viewport.
   * 
   * @return  Current viewport of this scene.
   */
  public GViewport getViewport()
  {
    return viewport_;
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
   * Thus w2 is mapped to viewport (x0,y0), w0 is mapped to (x2,y2) and
   * w1 is mapped to lower right corner of viewport.
   * <p>
   * w0,w1 and w2 are three dimensions, and the world extent can thus be
   * any plane in a 3D space, and the plane may be skewed.
   * 
   * @param w0  Point 0 of the new world extent [x,y].
   * @param w1  Point 1 of the new world extent [x,y].
   * @param w2  Point 2 of the new world extent [x,y].
   */
  public void setWorldExtent (double w0[], double w1[], double w2[])
  {
    worldExtent_.set (w0, w1, w2);
    
  
    transformer_.update (viewport_, worldExtent_);
    
    redraw (getVisibility());

  }



  /**
   * A convenience method for specifying a orthogonal world extent. 
   * The layout is as follows:
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
   * @param x0      X coordinate of world extent origin.
   * @param y0      Y coordinate of world extent origin.
   * @param width   Width of world extent.
   * @param height  Height of world extent.
   */
  public void setWorldExtent (double x0, double y0, double width, double height)
  {
    double w0[] = {x0,         y0};
    double w1[] = {x0 + width, y0};
    double w2[] = {x0,         y0 + height};

    setWorldExtent (w0, w1, w2);
    
    
  }
  
  

  /**
   * Return the world extent as specified by the application.
   * 
   * @return  The world extent as it was specified through setWorldExtent().
   */
  public GWorldExtent getWorldExtent()
  {
    return worldExtent_;
  }

  
  /**
   * Update region for this GObject. The region of a GScene is always the
   * viewport extent.
   */
  private void updateRegion()
  {
	  getRegion().set(
			  new GRect (viewport_.getX0(),
					     viewport_.getY0(),
					  (int) viewport_.getWidth(),
					  (int) viewport_.getHeight())
			  );
	  flagRegionValid (true);
  }

  
  
  /**
   * Flag the annotation of this scene as valid or invalid. Annotation
   * is set to invalid if annotation is changed somewhere down the tree.
   * This is an instruction to the GWindow to redo the annotation on this
   * scene. When the annotation is redone, this flag is set to valid.
   * 
   * @param isAnnotationValid  True if the annotation of this scene is valid
   *                           false otherwise.
   */
  void setAnnotationValid (boolean isAnnotationValid)
  {
    isAnnotationValid_ = isAnnotationValid;
  }


  
  /**
   * Check if annotation in this scene is valid.
   * 
   * @return  True if the annotation is valid, false otherwise.
   */
  boolean isAnnotationValid()
  {
    return isAnnotationValid_;
  }
  

  /**
   * Compute positions of the specified positionals.
   * 
   * @param positionals  Positionals to compute positions of.
   */
  void computePositions (Collection positionals)
  {
    annotator_.computePositions (positionals);
  }
  
  /**
   * Compute positions for positional object that are attached
   * to every vertex of its owner.
   * 
   * @param positional  Positional to compute position for.
   */
  void computeVertexPositions (GPositional positional)
  {
    annotator_.computeVertexPositions (positional);    
  }
  
  /**
   * Refresh the graphics scene. Only elements that has been changed 
   * since the last refresh are affected.
   */
  public void refresh()
  {
	  // This is default setting from window point of view
	  int visibilityMask = GObject.DATA_VISIBLE       | 
			  GObject.ANNOTATION_VISIBLE |
			  GObject.SYMBOLS_VISIBLE    |
			  GObject.WIDGETS_VISIBLE;


	  refresh(visibilityMask);

  }

  /**
   * Refresh the graphics scene. Only elements that has been changed 
   * since the last refresh are affected.
   */
  public void refresh( int visibilityMask )
  {
   
	  computeTextPositions();

	  updateRegion();
	  
	  computeRegion(visibilityMask);

	  // Rendering pass 1: DATA clippend by scene viewport.
	  refreshData (visibilityMask);

	  // Rendering pass 2: ANNOTATION
	  refreshAnnotation (visibilityMask);


  }


  
}