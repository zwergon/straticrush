package no.geosoft.cc.graphics;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import no.geosoft.cc.interfaces.ICanvas;
import no.geosoft.graphics.factory.GFactory;
import fr.ifp.kronosflow.geometry.Geometry;
import fr.ifp.kronosflow.geometry.Region;



/**
 * GWindow is the top level graphics node and holder of GScene nodes
 * (node containing world-to-device transformation). The GWindow is
 * linked to the GUI through its canvas object.
 * <p>
 * Typical usage:
 *
 * <pre>
 *   // Some Swing component to hold the graphics 
 *   JPanel panel = new JPanel();
 *   panel.setLayout (new BorderLayout());
 * 
 *   // Create the window and attach to GUI
 *   GWindow window = new GWindow (Color.WHITE);
 *   panel.add (window.getCanvas(), BorderLayout.CENTER);
 * </pre>
 *
 * GWindow is also the holder of the current "interaction" object
 * communicating mouse events between the back-end AWT component and
 * the client application.
 * 
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */   
public class GWindow
{
 
  private final ICanvas  canvas_;
  
  private int           width_;
  private int           height_;
  private GInteraction  interaction_;
  private List<GScene>  scenes_;
  private GScene        interactionScene_;
  
  private boolean       isResizing = false;


  
  /**
   * Create a new graphic window with the specified background color.
   * <p>
   * The window contains a JComponent canvas which should be added
   * to a container widget in the GUI.
   */
  public GWindow (Object parent, GColor backgroundColor)
  {
    // Rendering engine
    //canvas_ = new GGLCanvas ((Composite)parent, this);
	//canvas_ = new GAwtCanvas(this);
	canvas_ = GFactory.createCanvas(parent, this);
    if (backgroundColor != null)
    	canvas_.setBackgroundColor (backgroundColor);
     
     
    
    interaction_  = null;
    scenes_       = new ArrayList();
   

    // Cannot set 0 initially as resize is computed relative to current
    width_  = 500;
    height_ = 500;
  }


  
  /**
   * Create a new graphics window with default background color.
   */
  public GWindow( Object parent )
  {
    this (parent, null);
  }
  

  
  /**
   * Return rendering canvas of this window. This is the component that
   * should be added to the client GUI hierarchy.
   * 
   * @return  Rendering canvas of this window.
   */
  public ICanvas getCanvas()
  {
    return canvas_;
  }
  


  /**
   * Return width of this window.
   * 
   * @return  Width of this window.
   */
  public int getWidth()
  {
    return width_;
  }


  
  /**
   * Return height of this window.
   * 
   * @return  Height of this window.
   */
  public int getHeight()
  {
    return height_;
  }
  


  /**
   * Return the current interaction of this window.
   * 
   * @return  Current interaction of this window (or null if none installed).
   */
  GInteraction getInteraction()
  {
    return interaction_;
  }

  

  /**
   * Add a scene to this window. A window may have more than one scene.
   * The first scene added is rendered first (i.e. it appears in the
   * background of the screen) and so on.
   * 
   * @param scene  Scene to add.
   */
  void addScene (GScene scene)
  {
    scenes_.add (scene);
  }
  
  
  public void removeScene( GScene scene ){
	  scenes_.remove(scene);
  }


  
  /**
   * Return all scenes of this window. If no scenes are attached to this
   * window, an empty (non-null) list is returned.
   * 
   * @return  All scenes of this window.
   */
  public List<GScene> getScenes()
  {
    return scenes_;
  }


  
  /**
   * Return the first scene of this window (or null if no scenes are
   * attached to this window). This method is a convenience where the
   * client application knows that are exactly one scene in the window
   * (which in many practical cases will be the case).
   * 
   * @return  The first scene of this window (or null if none).
   */
  public GScene getScene()
  {
    return scenes_.size() > 0 ? (GScene) scenes_.get (0) : null;
  }
  

  
  /**
   * Find scene at the specified location. If there are more than one scene
   * at the specified location, select the front most.
   * 
   * @param x  X coordinate of location of scene.
   * @param y  Y coordinate of location of scene.
   * @return   Front most scene at specfied location (or null if none).
   */
  private GScene getScene (int x, int y)
  {
    for (int i = scenes_.size()-1; i >= 0; i--) {
      GScene scene = (GScene) scenes_.get (i);
      GViewport viewport = scene.getViewport();
      if (Geometry.isPointInsidePolygon (new int[] {viewport.getX0(),
                                                    viewport.getX1(),
                                                    viewport.getX3(),
                                                    viewport.getX2()},
                                         new int[] {viewport.getY0(),
                                                    viewport.getY1(),
                                                    viewport.getY3(),
                                                    viewport.getY2()},
                                         x, y))
        return scene;
    }
                                                
    return null;
  }



  /**
   * Find a GObject based on specified name. Search depth first.
   * 
   * @param name  Name of object to search for.
   * @return      First object with matching name, or null if none found.
   */
  public GObject find (String name)
  {
    for (Iterator i = scenes_.iterator(); i.hasNext(); ) {
      GScene scene = (GScene) i.next();
      GObject object = scene.find (name);
      if (object != null) return object;
    }

    return null;
  }
  

  
  /**
   * Find a GObject based on user data. Search depth first.
   * 
   * @param name  User data of object to search for.
   * @return      First object with matching user data, or null if none found.
   */
  public GObject find (Object userData)
  {
    for (Iterator i = scenes_.iterator(); i.hasNext(); ) {
      GScene scene = (GScene) i.next();
      GObject object = scene.find (userData);
      if (object != null) return object;
    }

    return null;
  }
  

  /**
   * Install the specified interaction on this window. As a window
   * can administrate only one interaction at the time, the current
   * interaction (if any) is first stopped.
   * 
   * @param interaction  Interaction to install and start.
   */
  public void startInteraction (GInteraction interaction)
  {
    if (interaction_ != null)
      stopInteraction();

    interaction_      = interaction;
    interactionScene_ = null;
  }


  
  /**
   * Stop the current interaction. The current interaction will get
   * an ABORT event so it has the possibility to do cleanup. If no
   * interaction is installed, this method has no effect. 
   */
  public void stopInteraction()
  {
    // Nothing to do if no current interaction
    if (interaction_ == null) return;
    
    interaction_.event (null, new GMouseEvent(GMouseEvent.ABORT, 0, 0) );
    interaction_      = null;
    interactionScene_ = null;    
  }
  

  
  /**
   * Ensure correct regions for all objects. Only objects with its
   * isRegionValid_ flag set to false (and their parents) will be
   * recomputed.
   */
  void computeRegion()
  {
    // This is default setting from window point of view
    int visibilityMask = GObject.DATA_VISIBLE       | 
                         GObject.ANNOTATION_VISIBLE |
                         GObject.SYMBOLS_VISIBLE;

    for (Iterator i = scenes_.iterator(); i.hasNext(); ) {
      GScene scene = (GScene) i.next();
      scene.computeRegion (visibilityMask);
    }
  }
  


  /**
   * Force a complete redraw of all visible elements.
   * <p>
   * Normally this method is called automatically when needed
   * (typically on retransformations).
   * A client application <em>may</em> call this method explicitly
   * if some external factor that influence the graphics has been
   * changed. However, beware of the performance overhead of such
   * an approach, and consider calling GObject.redraw() on the
   * affected objects instead.
   */
  public void redraw()
  {
    // This is default setting from window point of view
    int visibilityMask = GObject.DATA_VISIBLE       | 
                         GObject.ANNOTATION_VISIBLE |
                         GObject.SYMBOLS_VISIBLE    |
                         GObject.WIDGETS_VISIBLE;

    for (Iterator i = scenes_.iterator(); i.hasNext(); ) {
      GScene scene = (GScene) i.next();
      scene.redraw (visibilityMask);
    }
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

    // Compute positions of all annotations
    computeTextPositions();

    // Compute positions of all integrated AWT components
    computeComponentPositions();

    // Compute region for all elements
    computeRegion();
    
    canvas_.initRefresh();

    // Compute viewPort Region of all GScene and set it as the damage Region
    Region damageRegion = new Region();
    for (Iterator i = scenes_.iterator(); i.hasNext(); ) {
      GScene scene = (GScene) i.next();
      damageRegion.union (scene.getRegion());
    }
    

    // Clear the viewport area in the canvas
    canvas_.clear (damageRegion.getExtent());

   
    for (Iterator i = scenes_.iterator(); i.hasNext(); ) {
        GScene scene = (GScene) i.next();
        canvas_.setClipArea (scene.getRegion());
        
        // Rendering pass 1: DATA clippend by scene viewport.
        scene.refreshData (visibilityMask);

        // Rendering pass 2: ANNOTATION
        scene.refreshAnnotation (visibilityMask);
    }

   
    canvas_.refresh();
 
  }



  /**
   * Compute all text positions in entire window.
   */
  void computeTextPositions()
  {
    for (Iterator i = scenes_.iterator(); i.hasNext(); ) {
      GScene scene = (GScene) i.next();
      if (!scene.isAnnotationValid())
        scene.computeTextPositions();
    }
  }



  /**
   * Compute all component (Swing widgets) positions in entire window.
   */
  void computeComponentPositions()
  {
    for (Iterator i = scenes_.iterator(); i.hasNext(); ) {
      GScene scene = (GScene) i.next();
      // TODO: if (!scene.isAnnotationValid())
      scene.computeComponentPositions();
    }
  }



  /**
   * Method called when the pointer enters this window. If an interaction
   * is installed, pass a FOCUS_IN event to it.
   * 
   * @param x  X position of mouse.
   * @param y  Y position of mouse.   
   */
  public void mouseEntered (int x, int y)
  {
    if (interaction_ == null) return;
    interaction_.event (getScene (x, y), new GMouseEvent( GMouseEvent.FOCUS_IN, x, y ) );
  }


  
  /**
   * Method called when the pointer exits this window. If an interaction
   * is installed, pass a FOCUS_OUT event to it.
   * 
   * @param x  X position of mouse.
   * @param y  Y position of mouse.   
   */
  public void mouseExited (int x, int y)
  {
    if (interaction_ == null) return;
    interaction_.event (getScene (x, y), new GMouseEvent(GMouseEvent.FOCUS_OUT, x, y) );
  }


  
  /**
   * Method called when a mouse pressed event occurs in this window.
   * If an interaction is installed, pass a BUTTON*_DOWN event to it.
   * 
   * @param event trigging this method.
   */
  public void mousePressed ( GMouseEvent event )
  {
    if (interaction_ == null) return;
    interactionScene_ = getScene (event.x, event.y);
    interaction_.event (interactionScene_, event );
  }


  
  /**
   * Method called when a mouse release event occurs in this window.
   * If an interaction is installed, pass a BUTTON*_UP event to it.
   * 
   * @param event trigging this method.
   */
  public void mouseReleased ( GMouseEvent event )
  {
    if (interaction_ == null) return;
    interaction_.event (interactionScene_,  event );
  }

  

  /**
   * Method called when the mouse is dragged (moved with button pressed) in
   * this window. If an interaction is installed, pass a BUTTON*_DRAG
   * event to it.
   * 
   * @param event trigging this method.
   */
  public void mouseDragged ( GMouseEvent event )
  {
    if (interaction_ == null) return;
    interaction_.event (interactionScene_, event );
  }
  
  /**
   * Method called when the wheel mouse is turned in
   * this window. If an interaction is installed, pass a WHEEL_**
   * event to it.
   * 
   * @param event trigging this method.
   */
  public void wheelMoved ( GMouseEvent event )
  {
	  if (interaction_ == null) return;
	  interaction_.event (interactionScene_, event );
  }


  public void keyPressed( GKeyEvent event ){
	  if ( interaction_ == null ) return;
	  interaction_.keyEvent( event);
  }


  /**
   * Method called when the mouse is moved inside this window.
   * If an interaction is installed, pass a MOTION event to it.
   * 
   * @param x  X position of mouse.
   * @param y  Y position of mouse.   
   */
  public void mouseMoved (int x, int y)
  {
    if (interaction_ == null) return;
    interaction_.event (getScene (x, y), new GMouseEvent(GMouseEvent.MOTION, x, y) );
  }


  public void update(){
	  resize();
  }
  
  /**
   * Called when the window is resized. Reset the dimension variables
   * and resize scenes accordingly.
   */
  public synchronized void resize()
  {

	  if ( isResizing ){
		  System.out.println("Already resizing, exit");
		  return;
	  }

	  isResizing = true;

	  // Get the new window size
	  int width  = canvas_.getWidth();
	  int height = canvas_.getHeight();

	  // Refuse to resize to zero as we cannot possible resize back
	  if (width == 0 || height == 0) return;

	  // Compute resize factors
	  double dx = (double) width  / (double) width_;
	  double dy = (double) height / (double) height_;

	  // Set new window size
	  width_  = width;
	  height_ = height;


	  // Resize every scene accordingly
	  for (Iterator i = scenes_.iterator(); i.hasNext(); ) {
		  GScene scene = (GScene) i.next();
		  scene.resize (dx, dy);
	  }


	  // Recompute geometry
	  redraw();

	  // Render graphics
	  refresh();

	  isResizing = false;
  }
  
  
  
  /**
   * Print the current image.
   * 
   * @return  True if no exception was caught, false otherwise.
   */
  public boolean print()
  {
    boolean isOk = true; //TODO canvas_.print();
    return isOk;
  }
  


  /**
   * Store the current graphic image as a GIF file.
   * 
   * @param file  File to store in.
   */
  public void saveAsGif (File file)
    throws IOException
  {
    //TODO canvas_.saveAsGif (file);
  }
}
