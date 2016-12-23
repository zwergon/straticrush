package stratifx.application.views;

import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GStyle;

public class GPatchIntervalView extends GView {
	
	private GInterval gline;
	
	
	public void setModel (Object object)
	{
		setUserData( object );
		
		PatchInterval interval = getPatchInterval();
			
		gline = new GInterval(interval);
		addSegment(gline);
		
		gline.updateGeometry();

		
		GStyle style = new GStyle();
		style.setForegroundColor ( new GColor( interval.getColor().getRGB() ) );
		style.setLineWidth (3);
		setStyle (style);	
	}
	
	
	PatchInterval getPatchInterval(){
		return (PatchInterval)getUserData();
	}
	
	private Patch getPatch(){
		PatchInterval interval = getPatchInterval();
		return interval.getPatch();
	}
	
	
	@Override
	public void draw() {
		if ( null != gline ){
			gline.updateGeometry();
		}
	}
	
	@Override
	public void modelChanged( IControllerEvent<?> event ) {
		if ( event.getObject() == getPatch() && null != gline ){
			gline.updateGeometry();
		}
	}

}
