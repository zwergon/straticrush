package stratifx.application.views;

import java.awt.Color;

import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.model.geology.Paleobathymetry;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GStyle;

public class GPaleoView extends GView {
	
	GPolyline gline = null;
	
	@Override
	public void setModel (Object object)
	{
		setUserData( object );
		
		createLine( (Paleobathymetry)object );	
	}
	


	@Override
	public void modelChanged( IControllerEvent<?> event ) {
		if ( event.getObject() == getUserData() ){
			redraw();
		}
	}

	
	private void createLine( Paleobathymetry bathy ){
		
		Color bcolor = bathy.getColor();
	
		gline = new GPolyline( bathy.getPolyline() );
		
		GStyle style = new GStyle();
		style.setForegroundColor(  new GColor( bcolor.getRed(), bcolor.getGreen(), bcolor.getBlue() ) );
		style.setLineWidth(1);
		gline.setStyle(style);
		
		add(gline);
		
		setVisibility( GObject.DATA_VISIBLE );
			
	}
}
