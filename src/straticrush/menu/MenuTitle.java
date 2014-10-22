package straticrush.menu;

import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GPosition;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GText;

public class MenuTitle extends GObject {
	
	private GSegment background_;
	

	public MenuTitle( String text, GColor color ){
		
		background_ = new GSegment();
		
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
		 
		  // Draw background
		  background_.setGeometryXy (  menu.getTitleArea() );
	  }

	
}
