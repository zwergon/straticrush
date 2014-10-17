package no.geosoft.cc.graphics;



import fr.ifp.kronosflow.geometry.Geometry;
import fr.ifp.kronosflow.geometry.Vector2D;



/**
 * This class represent a client 3D plane world extent. The world extent
 * is defined by three points as follows:
 *
 * <pre>
 *        w2 o 
 *           |
 *           |
 *           |
 *        w0 o-------o w1
 * </pre>
 *
 * Each point is a 3D coordinate [x,y,z]. A typical world extent will have
 * w0[y] == w1[y], w0[x] == w2[x] and all z coordinates == 0, but the
 * definition makes it possible to use any planar suerface in 3D space
 * as world extent.
 * <p>
 * Clients sets world extent on a scene through the
 * <tt>GScene.setWorldExtent()</tt> methods.
 *
 * @see GScene#setWorldExtent (double[], double[], double[]).
 * @see GScene#setWorldExtent (double, double, double, double). 
 * 
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */   
public class GWorldExtent
{
  // To enhance indexing readability
  private static final int X = 0;
  private static final int Y = 1;
  
  double  w0_[], w1_[], w2_[];  // [x,y]



  /**
   * Create a world extent specified by three coordinates as follows:
   *
   * <pre>
   *        w2 o 
   *           |
   *           |
   *           |
   *        w0 o-------o w1
   * </pre>
   * 
   * @param w0  First world extent coordinate [x,y].
   * @param w1  Second world extent coordinate [x,y].
   * @param w2  Third world extent coordinate [x,y].
   */
  public GWorldExtent (double w0[], double w1[], double w2[])
  {
    w0_ = new double[2];
    w1_ = new double[2];
    w2_ = new double[2];

    set (w0, w1, w2);
  }

  
  
  /**
   * Create a default (normalized) world extent. The normalized
   * world extent with X/Y extents [0.0 - 1.0].
   */
  public GWorldExtent()
  {
    this (new double[] {0.0, 0.0},
          new double[] {1.0, 0.0},
          new double[] {0.0, 1.0});
  }
  


  /**
   * Create a world extent as a copy of the specified world extent.
   * 
   * @param worldExtent  World extent to copy.
   */
  public GWorldExtent (GWorldExtent worldExtent)
  {
    this (worldExtent.get(0), worldExtent.get(1), worldExtent.get(2));
  }

  

  /**
   * Set the three world extent coordinates.
   * 
   * @param w0  Coordinate 0 of world extent [x,y].
   * @param w1  Coordinate 1 of world extent [x,y].
   * @param w2  Coordinate 2 of world extent [x,y].
   */
  public void set (double w0[], double w1[], double w2[])
  {
    for (int i = 0; i < 2; i++) {
      w0_[i] = w0[i];
      w1_[i] = w1[i];
      w2_[i] = w2[i];      
    }
  }


  
  /**
   * Return world extent coordinate of specified index.
   * 
   * @param index  index of coordinate to get.
   * @return       Coordinate of point at index [x,y].
   */
  public double[] get (int index)
  {
    switch (index) {
      case 0  : return w0_;
      case 1  : return w1_;
      case 2  : return w2_;
    }

    throw new ArrayIndexOutOfBoundsException (index);
  }



  /**
   * Resize this viewport a specified fraction in X and Y direction.
   * 
   * @param dx  Fraction to resize in X direction.
   * @param dy  Fraction to resize in Y direction.
   */
  void resize (double dx, double dy)
  {
    double newWidth  = getWidth()  * dx;
    double newHeight = getHeight() * dy;

    extendWidth (newWidth);
    extendHeight (newHeight);
  }



  

  /**
   * Return width of this world extent.
   * 
   * @return   Width of this world extent.
   */
  public double getWidth()
  {
    return Vector2D.length (w0_, w1_);
  }

  

  /**
   * Return height of this world extent.
   * 
   * @return   Height of this world extent.
   */
  public double getHeight()
  {
    return Vector2D.length (w0_, w2_);
  }


  
  /**
   * Recompute this world extent to the specified width. Width is defined
   * as the length between w0 and w1.
   * The world extent is resized equally in each direction.
   * 
   * @param newWidth  New width of this world extent.
   */
  void extendWidth (double newWidth)
  {
    double oldW0[] = new double[2];
    oldW0[X] = w0_[X];
    oldW0[Y] = w0_[Y];
    
    Geometry.extendLine (w0_, w1_, newWidth, 0.5);

    double dx = w0_[X] - oldW0[X];
    double dy = w0_[Y] - oldW0[Y];
    
    
    w2_[X] += dx;
    w2_[Y] += dy;
    }


  
  /**
   * Recompute this world extent to the specified height. Height is defined
   * as the length between w0 and w2.
   * The world extent is resized equally in each direction.
   * 
   * @param newHeight  New height of this world extent.
   */
  void extendHeight (double newHeight)
  {
    double oldW0[] = new double[2];
    oldW0[X] = w0_[X];
    oldW0[Y] = w0_[Y];
    
    Geometry.extendLine (w0_, w2_, newHeight, 0.5);

    double dx = w0_[X] - oldW0[X];
    double dy = w0_[Y] - oldW0[Y];
    
    w1_[X] += dx;
    w1_[Y] += dy;
  }
}
