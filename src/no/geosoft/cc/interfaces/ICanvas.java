package no.geosoft.cc.interfaces;

import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GFont;
import no.geosoft.cc.graphics.GImage;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GText;
import fr.ifp.kronosflow.geometry.Rect;
import fr.ifp.kronosflow.geometry.Region;

public interface ICanvas {
	
	public int getWidth();
	public int getHeight();
	
	public void initRefresh();
	public void refresh();
	public void setClipArea(Region damageRegion);
	public void clear(Rect extent);
	
	public void setBackgroundColor( GColor color );
	
	public void render (GSegment segment, GStyle style );
	public void render (GText text, GStyle style);
	public void render (GImage image);
	public void render (int[] x, int[] y, GImage image);
	
	public Rect getStringBox (String string, GFont font);

	
	  
	  
}
