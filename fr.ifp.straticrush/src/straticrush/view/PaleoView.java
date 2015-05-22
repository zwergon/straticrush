package straticrush.view;

import java.awt.Color;

import fr.ifp.kronosflow.controller.IControllerEvent;
import fr.ifp.kronosflow.geology.Paleobathymetry;
import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GStyle;

public class PaleoView extends View {
	
	GPolyline gline = null;
	
	@Override
	public void setUserData (Object object)
	{
		super.setUserData( object );
		
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
	public void objectChanged( IControllerEvent<?> event ) {
		if ( event.getObject() == getUserData() ){
			gline.updateGeometry();
		}

	}

}
