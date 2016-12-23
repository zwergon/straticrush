package stratifx.application.views;

import java.awt.Color;

import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.geology.Paleobathymetry;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GStyle;

public class GPaleoView extends GView {
	
	GPolyline gline = null;
	
	@Override
	public void setModel (Object object)
	{
		setUserData( object );
		
		createGSegment( (Paleobathymetry)object );	
	}
	
	public void createGSegment( Paleobathymetry bathy ){
		
		Color bcolor = bathy.getColor();
		GStyle style = new GStyle();
		style.setForegroundColor ( new GColor( bcolor.getRed(), bcolor.getGreen(), bcolor.getBlue() ) );
		style.setLineWidth (1);
		setStyle (style);
		
		gline = new GPolyline(bathy.getPolyline() );
		
		addSegment(gline);
		
		setVisibility( GObject.DATA_VISIBLE );
			
	}
	
	public void draw()
	{
		if ( gline != null ){
			gline.updateGeometry();
		}
	}

	@Override
	public void modelChanged( IControllerEvent<?> event ) {
		if ( event.getObject() == getUserData() ){
			gline.updateGeometry();
		}

	}

}
