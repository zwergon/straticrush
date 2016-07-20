package straticrush.view;


import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GStyle;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.newevents.IControllerEvent;
import fr.ifp.kronosflow.topology.Contact;
import fr.ifp.kronosflow.topology.PartitionLine;

public class PartitionLineView extends View {
	
	private GInterval gline;
	

	public void setModel (Object object)
	{
		setUserData( object );
		
		PartitionLine partition = (PartitionLine)getUserData();

		PatchInterval interval = partition.getPatchInterval();
			
		gline = new GInterval(interval);
		addSegment(gline);
		
		gline.updateGeometry();

		
		GStyle style = new GStyle();
		style.setForegroundColor ( new GColor( interval.getColor().getRGB() ) );
		style.setLineWidth (3);
		setStyle (style);	
	}
	
	private Patch getPatch(){
		PartitionLine pLine = (PartitionLine)getUserData();
		PatchInterval interval = pLine.getPatchInterval();
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
