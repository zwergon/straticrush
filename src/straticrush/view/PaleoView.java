package straticrush.view;

import java.awt.Color;

import fr.ifp.kronosflow.controller.IControllerEvent;
import fr.ifp.kronosflow.geology.Paleobathymetry;
import no.geosoft.cc.graphics.GColor;
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
		gline = new GPolyline(bathy.getPaleoLine() );
		
		addSegment(gline);
			
		Color bcolor = bathy.getColor();
		GStyle style = new GStyle();
		style.setForegroundColor ( new GColor( bcolor.getRed(), bcolor.getGreen(), bcolor.getBlue() ) );
		style.setLineWidth (1);
		gline.setStyle (style);
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
