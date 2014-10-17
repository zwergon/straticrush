package straticrush.menu;


import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GPosition;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GText;


public class MenuItem extends GObject {
	
	private GSegment background_;
	private int offset;

	public MenuItem( String text, GColor color ){
		
		background_ = new GSegment();
		offset = 0;
		
		GText gtext = new GText( text, GPosition.MIDDLE | GPosition.STATIC );
		background_.setText( gtext  );
		
		GStyle backgroundStyle = new GStyle();
		backgroundStyle.setBackgroundColor (color);
		backgroundStyle.setForegroundColor( GColor.WHITE );
		background_.setStyle (backgroundStyle);
				
		addSegment (background_);

	}
	
	 public static int[] createRectangle (int x0, int y0, int width, int height)
	  {
	    return new int[] {x0,               y0,
	                      x0 + (width - 1), y0,
	                      x0 + (width - 1), y0 + (height - 1),
	                      x0,               y0 + (height - 1),
	                      x0,               y0};
	  }
	 
	 public int[] getBorder(){
		 return background_.getGeometry();
	 }

	  public void draw() {
		  updateGeometry();
	  }

	private void updateGeometry() {
		
		 Menu menu = (Menu)getParent();
		int index = getPosition() + offset;
		  int width  =  menu.getWidth() - 6;
		  int height =  40 ;
		  int x0 = menu.getX0() + 3 ;
		  int y0 = menu.getY0() + 3 + index*height;


		  // Draw background
		  background_.setGeometry (createRectangle (x0, y0, width, height));
	}

	public void scrollDown() {
		offset++;
		updateGeometry();
	}
	
	public void scrollUp() {
		offset--;
		updateGeometry();
	}

}
