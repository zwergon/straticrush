package no.geosoft.cc.graphics;



import fr.ifp.kronosflow.geometry.Matrix2d;
import fr.ifp.kronosflow.geometry.Vector2D;



/**
 * World-to-device transformation object.
 * 
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */   
public class GTransformer
{
	private	double[] w_origin_; 
	private double[] d_origin_;
	private final Matrix2d  w2d_;
	private final Matrix2d  d2w_;



	/**
	 * Create a new transformer instance based on the specified viewport
	 * and world extent.
	 * 
	 * @param viewport     Viewport of transformer.
	 * @param worldExtent  World extent of transformer.
	 */
	public GTransformer (GViewport viewport, GWorldExtent worldExtent)
	{
		w_origin_ = new double[2];
		d_origin_ = new double[2];
		w2d_ = new Matrix2d();
		d2w_ = new Matrix2d();

		update (viewport, worldExtent);
	}



	/**
	 * Reset this transformer instance based on a specified viewport
	 * and world extent.
	 *  * <p>
	 * The world system is defined as follows:
	 *
	 * <pre>
	 *        w2 o 
	 *           |
	 *           |
	 *           |
	 *        w0 o-------o w1
	 * <pre>
	 * <p>
	 * Each point is defined with x,y so this system may in effect be
	 * arbitrary oriented in space, and may include sharing.
	 * <p>
	 * The device system is defined as follows:
	 *
	 * <pre>
	 *             width
	 *     x0,y0 o-------o
	 *           |
	 *    height |
	 *           |
	 *           o
	 * </pre>
	 * <p>
	 * The matrix maps w2 to (x0,y0), w0 to the lower left corner of the
	 * device rectangle, and w1 to the lower right corner of the device
	 * rectangle.
	 * 
	 * @param viewport     Viewport.
	 * @param worldExtent  World extent.
	 */
	void update (GViewport viewport, GWorldExtent worldExtent)
	{
		
		double[] w0    = worldExtent.get(0);
		double[] w1    = worldExtent.get(1);
		double[] w2    = worldExtent.get(2);
	
		//First base :  world(w)
		w_origin_     = new double[]{ w2[0], w2[1] };
		double[] p0w  = new double[]{ w1[0], w2[1] };
		double[] p1w  = new double[]{ w0[0], w0[1] };

		double[] e0w = Vector2D.substract(p0w, w_origin_);
		double[] e1w = Vector2D.substract(p1w, w_origin_);
		
		Matrix2d Mw = new Matrix2d();
		Mw.setValue(0, 0, e0w[0]);
		Mw.setValue(0, 1, e0w[1]);		
		Mw.setValue(1, 0, e1w[0]);
		Mw.setValue(1, 1, e1w[1]);
		Mw.invert();
		
		//Second base : device(d)
		d_origin_         = new double[]{ (double)viewport.getX0(), (double)viewport.getY0() };
		double[] p0d     = new double[]{ d_origin_[0] + viewport.getWidth(), d_origin_[1] };
		double[] p1d     = new double[]{ d_origin_[0], d_origin_[1] + viewport.getHeight() };
	
		double[] e0d = Vector2D.substract(p0d, d_origin_);
		double[] e1d = Vector2D.substract(p1d, d_origin_);
		
		
		/*search affine parameters of a transformation which matrix
		 * M = | a b |
		 *     | c d | 
		 * is computed so that
		 * 
		 * | xd[0] - od[0] | = M | xw[0] - ow[0] |
		 * | xd[1] - od[1] |     | xw[1] - ow[1] |
		 */
		double[] ab = new double[2];
		Mw.multiply( e0d, ab );
		
		double[] cd = new double[2];
		Mw.multiply( e1d, cd );
		
		
		w2d_.setValue(0, 0, ab[0]);
		w2d_.setValue(0, 1, ab[1]);		
		w2d_.setValue(1, 0, cd[0]);
		w2d_.setValue(1, 1, cd[1]);
		
		w2d_.copy (d2w_);
		d2w_.invert();
				
	}



	/**
	 * Convert a world coordinate to device.
	 * 
	 * @param wx  X of world coordinate to convert
	 * @param wy  Y of world coordinate to convert.
	 * @return    Device coordinate [x,y].
	 */
	public int[] worldToDevice (double wx, double wy)
	{
		double[] world = {wx, wy};
		return worldToDevice (world);
	}




	/**
	 * Convert a world coordinate to device.
	 * 
	 * @param world  World coordinate to convert [x,y].
	 * @return       Device coordinate [x,y]
	 */
	public int[] worldToDevice (double world[])
	{
		
		double[] wc = { 
				world[0] - w_origin_[0],
				world[1] - w_origin_[1]
		};
		
		double[] result = new double[2];
		w2d_.multiply(wc, result);
		int device[] = {
				(int) Math.round (result[0]+d_origin_[0]), 
				(int) Math.round (result[1]+d_origin_[1])
				};    
		return device;
	}



	/**
	 * Convert a device coordinate to world.
	 * 
	 * @param x  X of device coordinate to convert.
	 * @param y  Y of device coordinate to convert.   
	 * @return   World coordinate [x,y].
	 */
	public double[] deviceToWorld (int x, int y)
	{
		double[] dc = {
				(double)x - d_origin_[0],
				(double)y - d_origin_[1]
		};	

		double[] result = new double[2];
		d2w_.multiply(dc, result);
		
		double[] world = {
				result[0]+w_origin_[0], 
				result[1]+w_origin_[1]
		};  
		
		return world;
	}



	/**
	 * Convert a device coordinate to world.
	 * 
	 * @param device  Device coordinate [x,y].
	 * @return        World coordinate [x,y,z].
	 */
	public double[] deviceToWorld (int device[])
	{		
		return deviceToWorld(device[0], device[1]);
	}



	// TODO: Add methods for converting collection of points
}
