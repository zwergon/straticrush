package no.geosoft.cc.graphics;



import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.Timer;



/**
 * A default zoom interaction. The following actions are implemented:
 *
 * <ol>
 * <li>Button 1 click at a point in the scene: The scene is zoomed in
 *     a certain factor with that point fixed.
 * <li>Button 1 press and keep pressed: The scene is zoomed in
 *     continously until the button is released.
 * <li>Button 3 click at a point in the scene: The scene is zoomed out
 *     a certain factor with that point fixed.
 * <li>Button 3 press and keep pressed: The scene is zoomed out
 *     continously until the button is released.
 * <li>Button 2 click: Unzoom.
 * <li>Button 1 press, drag and release: User creates a rubber band
 *     and the scene is zoomed to the specified area.
 * </ol>
 *
 * The <code>ZoomInteraction</code> class lacks the <em>G</em>
 * prefix as it is an optional extension to G and not part of the core
 * classes.
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */   
public class ZoomInteraction
  implements GInteraction, ActionListener
{
  private static final double  ZOOM_FACTOR = 0.9;
  
  private final GScene    scene_;
  private final GObject   interaction_;
  private final GSegment  rubberBand_;

  private int     x_[], y_[];
  private int     x0_, y0_;
  private Timer   timer_;
  private double  zoomFactor_;



  /**
   * Create a new zoom interaction on the specifed scene.
   * The interaction is activated by GWindow.setInteraction().
   * @see GWindow#startInteraction(GInteraction)
   * @see GWindow#stopInteraction()
   * 
   * @param scene  Scene of this zoom interaction.
   * @param style  Style for the interaction rubber band
   */
  public ZoomInteraction (GScene scene, GStyle style)
  {
    scene_ = scene;

    // Create a graphic node for holding the interaction graphics
    interaction_ = new GObject ("Interaction");

    // Default rubberband style if none provided
    if (style == null) {
      style = new GStyle();
      style.setLineWidth (1);
      style.setForegroundColor (new GColor (0, 0, 0));
      style.setBackgroundColor (null);
    }
    interaction_.setStyle (style);

    // Create and attach rubberband segment
    rubberBand_ = new GSegment();
    interaction_.addSegment (rubberBand_);

    // For the rubberband geometry
    x_ = new int[5];
    y_ = new int[5];
  }


  
  /**
   * Create a new zoom interaction on the specifed scene.
   * @see GWindow#startInteraction(GInteraction)
   * @see GWindow#stopInteraction()
   * 
   * @param scene  Scene of this zoom interaction.
   */
  public ZoomInteraction (GScene scene)
  {
    this (scene, null);
  }



  /**
   * Timer trigged event when the user keeps pressing a mouse button.
   * 
   * @param event  Not used.
   */
  public void actionPerformed (ActionEvent event)
  {
    scene_.zoom (x0_, y0_, zoomFactor_);
  }
  
  

  /**
   * Handle mouse events in the canvas.
   * 
   * @param eventType  Event trigging this method.
   * @param x,y        Pointer location.         
   */
  public void event (GScene scene,  GEvent event )
  {
    switch (event.type) {
      case GEvent.BUTTON1_DOWN :
        x0_ = event.x;
        y0_ = event.y;

        zoomFactor_ = ZOOM_FACTOR;
        
        scene_.add (interaction_);  // Front

        // Cause actionPerformed() to be called every 90ms as long
        // as button is pressed.
        timer_ = new Timer (90, this);
        timer_.setInitialDelay (500);
        timer_.start();
        break;
          
      case GEvent.BUTTON1_UP :
        interaction_.remove();
        rubberBand_.setGeometry ((int []) null);        

        timer_.stop();
        timer_.removeActionListener (this);
        
        int dx = Math.abs (event.x - x0_);
        int dy = Math.abs (event.y - y0_);

        // If the rubber band is very small, interpret it as a click
        if (dx < 3 || dy < 3)
          scene_.zoom (event.x, event.y, zoomFactor_);

        // Else we zoom into the area defined by the rubberband
        else
          scene_.zoom (x0_, y0_, event.x, event.y);
        break;
          
      case GEvent.BUTTON2_DOWN :
    	  x0_ = event.x;
          y0_ = event.y;
        break;
       
      case GEvent.BUTTON2_DRAG :

    	  dx = event.x - x0_;
    	  dy = event.y - y0_;

    	  scene_.pan( dx, dy);
    	  
    	  x0_ = event.x;
          y0_ = event.y;
    	  
    	  break;
      
          
      case GEvent.BUTTON1_DRAG :
        timer_.stop();
        timer_.removeActionListener (this);

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

        rubberBand_.setGeometry (x_, y_);
        scene_.refresh();
        break;

      case GEvent.BUTTON3_DOWN :
        x0_ = event.x;
        y0_ = event.y;

        zoomFactor_ = 1.0 / ZOOM_FACTOR;
        
        // Cause actionPerformed() to be called every 90ms as long
        // as button is pressed.
        timer_ = new Timer (90, this);
        timer_.setInitialDelay (500);
        timer_.start();

        break;
        
      case GEvent.BUTTON3_UP :
        timer_.stop();
        timer_.removeActionListener (this);

        scene_.zoom (event.x, event.y, zoomFactor_);
        break;
        
      case GEvent.WHEEL_MOUSE_DOWN:
    	  scene_.zoom(ZOOM_FACTOR);
    	  break;
    	  
      case GEvent.WHEEL_MOUSE_UP:
    	  scene_.zoom(1./ZOOM_FACTOR);
    	  break;
    }
  }
}
