package straticrush.view;

import straticrush.interaction.IViewListener;
import straticrush.interaction.StratiCrushServices;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GStyle;



public abstract class View extends GObject implements IViewListener {

	public void useSelectedStyle(){
		GStyle gstyle = getStyle();
		if ( gstyle != null ){
			if ( gstyle.getBackgroundColor() != null ) {
				gstyle.setBackgroundColor( gstyle.getBackgroundColor().brighter() );
			}
			if ( gstyle.getForegroundColor() != null ) {
				gstyle.setForegroundColor( gstyle.getForegroundColor().brighter() );
			}
			gstyle.setLineWidth( gstyle.getLineWidth() + 2 );
		}
		redraw();
	}
	
	@Override
	public void destroy() {
		StratiCrushServices.getInstance().removeListener(this);	
	}

	@Override
	public void setUserData (Object object)
	{
		super.setUserData(object);
		StratiCrushServices.getInstance().addListener(this);	
	}
	
}
