package stratifx.application.views;

import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GStyle;


/**
 * This is an abstract object to handle specific {@link GObject}
 * that are displayed in {@link Plot}.
 * @author lecomtje
 *
 */
public abstract class GView extends GObject {
	
	public GView(String name){
		super(name);
	}
	
	public GView(){
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
        
        public Object getModel(){
            return getUserData();
        }
        
	public abstract void setModel( Object object );
	
	public abstract void modelChanged( IControllerEvent<?> event );

}
