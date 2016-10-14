package no.geosoft.cc.graphics.awt;


import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import no.geosoft.cc.graphics.GImage;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.interfaces.IImageImpl;
import no.geosoft.cc.utils.Rect;



/**
 * Wrapper object for images used with GSegments. A GImage represent
 * both predefined images as well as client specified images.
 * <p>
 * Typical usage:
 *
 * <pre>
 *    GImage image = new GImage (new File (imageFileName),
 *                               GPosition.SOUTHEAST);
 *    GSegment anchor = new GSegment();
 *    anchor.setImage (image);
 * <pre>
 *
 * Images can also be associated with every vertex of a polyline.
 * If using one of the predefined images, a typical usage will be:
 *
 * <pre>
 *    GImage image = new GImage (SYMBOL_CIRCLE1);
 *    GSegment segment = new GSegment();
 *    segment.setVertexImage (image);
 * </pre>
 *   
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */   
public class GAwtImage implements IImageImpl
{
	protected Image  image_ = null;
	private GImage gimage_;
	
	/**
	   * Create a GImage from an AWT Image.
	   * @see GAwtImage#setPositionHint(int)   
	   * 
	   * @param image         Image.
	   * @param positionHint  Position hint.
	   */
	  public GAwtImage (GImage image)
	  {
	    gimage_ = image;
	  }
	  
	

	  /**
	   * Return the realized image of this GImage.
	   * 
	   * @return  Image of this GImage.
	   */
	  public Image getImage()
	  {

		  GStyle actualStyle_ = gimage_.getActualStyle();
		  Rect rectangle_ = gimage_.getRectangle();
		  int[] imageData_ = gimage_.getImageData();
		  
		  // Lazy image create
		  if (image_ == null && imageData_ != null) {
			  int width  = rectangle_.width;
			  int height = rectangle_.height;

			  int backgroundColor = actualStyle_.getBackgroundColor().getRGB();
			  int foregroundColor = actualStyle_.getForegroundColor().getRGB();

			  // Create the image that represent the pattern
			  BufferedImage image = new BufferedImage (width, height,
					  BufferedImage.TYPE_INT_ARGB);

			  // Put the data into the image
			  int pointNo = 0;
			  for (int i = 0; i < height; i++) {
				  for (int j = 0; j < width; j++) {
					  image.setRGB (i, j, imageData_[pointNo] == 0 ? backgroundColor :
						  foregroundColor);
					  pointNo++;
				  }
			  }

			  image_ = image;
		  }
		  return image_;
	  }

/**
   * Compute size of this image and update rectangle variable.
   */
  void computeSize()
  {
/*	TODO
  
  if ( rectangle_.width == 0 && rectangle_.height == 0) {
    	//TODO
    	
      rectangle_.width  = image_.getWidth (getWindow().getCanvas());
      rectangle_.height = image_.getHeight (getWindow().getCanvas());
            
    }
    
    // This computation is only done if the image is file based.
    // In this case we realize the image at this point.
    // Otherwise the size is already correctly set.
    else if (image_ == null && file_ != null) {
      try {
        InputStream stream = new BufferedInputStream (new FileInputStream
                                                      (file_.getPath()));
        BufferedImage image = ImageIO.read (stream);
        
        rectangle_.height = image.getHeight();
        rectangle_.width  = image.getWidth();

        image_ = image;
      }
      
      // There is something wrong with the image file. We don't
      // care about telling the client, as this will become evident
      // anyway. Just leave the rectangle as empty.
      catch (Exception exception) {
        exception.printStackTrace();
        image_ = null;
        file_  = null;

        rectangle_.height = 0;
        rectangle_.width  = 0;
      }
    }
    */
  }
  
}
