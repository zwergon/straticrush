package straticrush.menu;


import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GPosition;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GText;
import fr.ifp.kronosflow.geometry.RectD;


public class MenuItem extends GObject {
	
	private GSegment background_;
	private int offset;

	public MenuItem( String text, GColor color ){
		
		background_ = new GSegment();
		offset = 1;
		
		GText gtext = new GText( text, GPosition.MIDDLE | GPosition.STATIC );
		background_.setText( gtext  );
		
		GStyle backgroundStyle = new GStyle();
		backgroundStyle.setBackgroundColor (color);
		backgroundStyle.setForegroundColor( GColor.WHITE );
		background_.setStyle (backgroundStyle);
				
		addSegment (background_);

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
		
		
		
		double width  =  1.0;
		double height =  0.1 ;
		double x0 = menu.getX0() ;
		double y0 = menu.getY0() + index*height;
		
		RectD rect = new RectD(x0,y0,x0+width,y0+height);
		if ( menu.getScrollableArea().contains( rect  ) ){
			setVisibility(VISIBLE);
		}
		else {
			setVisibility(INVISIBLE);
		}


		// Draw background
		background_.setGeometryXy( Menu.createRectangle (x0, y0, width, height) );
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
