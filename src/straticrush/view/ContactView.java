package straticrush.view;

import fr.ifp.kronosflow.controller.IControllerEvent;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.topology.Contact;
import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GStyle;

public class ContactView extends View {
	
	private GInterval gline;
	

	public void setUserData (Object object)
	{
		super.setUserData( object );
		
		Contact contact = (Contact)getUserData();

		PatchInterval interval = contact.getInterval();
			
		gline = new GInterval(interval);
		addSegment(gline);
		
		gline.updateGeometry();

		
		GStyle style = new GStyle();
		style.setForegroundColor ( new GColor( interval.getColor().getRGB() ) );
		style.setLineWidth (3);
		setStyle (style);	
	}
	
	private Patch getPatch(){
		Contact contact = (Contact)getUserData();
		PatchInterval interval = contact.getInterval();
		return interval.getPatch();
	}
	
	
	@Override
	public void draw() {
		if ( null != gline ){
			gline.updateGeometry();
		}
	}
	
	@Override
	public void objectChanged( IControllerEvent<?> event ) {
		if ( event.getObject() == getPatch() && null != gline ){
			gline.updateGeometry();
		}
	}

}
