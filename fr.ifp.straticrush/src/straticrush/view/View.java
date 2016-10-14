package straticrush.view;

import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GStyle;


/**
 * This is an abstract object to handle specific {@link GObject}
 * that are displayed in {@link Plot}.
 * @author lecomtje
 *
 */
public abstract class View extends GObject {
	
	public View(String name){
		super(name);
	}
	
	public View(){
		super();
	}
		
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
	
	public Plot getPlot(){
		return (Plot)getScene();
	}
		
	public abstract void setModel( Object object );
	
	public abstract void modelChanged( IControllerEvent<?> event );

}
