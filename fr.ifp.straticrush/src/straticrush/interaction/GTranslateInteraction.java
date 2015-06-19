package straticrush.interaction;

import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GImage;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;

public class GTranslateInteraction extends GObject {

	GSegment line;
	
	int[] xy = new int[4];
	
	public GTranslateInteraction(){
		super("Translate");
		setVisibility( DATA_VISIBLE | SYMBOLS_VISIBLE );
	}
	
	int[] getDisplacement(){
		int[] displacement = new int[2];
		displacement[0] = xy[2]-xy[0];
		displacement[1] = xy[3]-xy[1];
		return displacement;
	}
	
	
	public void createMarker( int x, int y ){
		
		line = new GSegment();
		addSegment(line);
		
		GStyle style = new GStyle();
		style.setForegroundColor(GColor.BLUE);
		style.setLineWidth (2);
		line.setStyle (style);
		
		
		GStyle symbolStyle = new GStyle();
		symbolStyle.setForegroundColor (new GColor (0, 0, 255));
		symbolStyle.setBackgroundColor (new GColor (0, 0, 255));
		GImage square = new GImage (GImage.SYMBOL_SQUARE1);
		square.setStyle (symbolStyle);

		line.setVertexImage (square);
		
		xy[0] = xy[2] = x;
		xy[1] = xy[3] = y;
		line.setGeometry(xy);
			
	}
	
	
	public void moveTo( int x, int y ){
		if ( null != line ){
			xy[2] = x;
			xy[3] = y;
			line.setGeometry(xy);
		}
	}
}
