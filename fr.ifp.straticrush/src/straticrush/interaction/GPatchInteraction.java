package straticrush.interaction;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GImage;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import straticrush.view.GExtension;
import straticrush.view.GInterval;
import straticrush.view.GPolyline;
import straticrush.view.IUpdateGeometry;
import fr.ifp.kronosflow.controller.IControllerEvent;
import fr.ifp.kronosflow.geology.BoundaryFeature;
import fr.ifp.kronosflow.model.IExtension;
import fr.ifp.kronosflow.model.Interval;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.PolyLine;
import fr.ifp.kronosflow.model.PolyLineGeometry;

class GPatchInteraction extends GObject implements IViewListener {
	
	public GPatchInteraction(){
		super("Interaction");
		setVisibility( DATA_VISIBLE | SYMBOLS_VISIBLE );
	}
	
	void addInterval( PatchInterval interval ){
		
		GInterval selectedSegment = new GInterval(interval);
		addSegment( selectedSegment );
		
		BoundaryFeature feature = interval.getInterval().getFeature();
		GColor color = null;
		if ( null != feature ){
			Color bcolor = feature.getColor();
			color = new GColor( bcolor.getRed(), bcolor.getGreen(), bcolor.getBlue() );
		}
		else {
			color = GColor.CYAN;
		}

		GStyle style = new GStyle();
		style.setForegroundColor (color);
		style.setLineWidth (4);
		selectedSegment.setStyle (style);


		selectedSegment.updateGeometry();
		
		Interval inter = interval.getInterval();
		PolyLineGeometry geom = new PolyLineGeometry( inter );
		
		for( int i = IExtension.BEFORE; i< IExtension.LAST; i++ ){
			IExtension extension = inter.getExtension(i);
			GExtension gextension =  new GExtension(extension, geom.length()/5. );
			addSegment( gextension );
			
			gextension.setStyle( style );
			gextension.updateGeometry();
		}
		
	}
	
	
	void addOutline( Patch patch, boolean surrounding ){
		
		GPolyline borderLine = new GPolyline(patch.getBorder());
		addSegment( borderLine );
		
		GStyle style = new GStyle();
		if ( !surrounding ){
			style.setBackgroundColor(GColor.CYAN);
		}
		else {
			style.setBackgroundColor(GColor.ORANGE);
		}
		//style.setLineWidth (4);
		borderLine.setStyle (style);
		
		borderLine.updateGeometry();
	}
	
	@Override
	public void draw() {
		updateGeometry();
	}

	private void updateGeometry() {
		for( GSegment gsegment : getSegments() ){
			if ( gsegment instanceof IUpdateGeometry ){
				((IUpdateGeometry) gsegment).updateGeometry();
			}
		}
	}
	
	public void clearLines(){
		for ( GPolyline line : lines ){
			removeSegment(line);
		}
		lines.clear();
	}

	public void addLine(PolyLine targetLine) {
		
		GPolyline gline  = new GPolyline(targetLine);
		addSegment( gline );
		
		GStyle style = new GStyle();
		style.setForegroundColor(GColor.BLUE);
		style.setLineWidth (2);
		gline.setStyle (style);
		
		
		GStyle symbolStyle = new GStyle();
		symbolStyle.setForegroundColor (new GColor (0, 0, 255));
		symbolStyle.setBackgroundColor (new GColor (0, 0, 255));
		GImage square = new GImage (GImage.SYMBOL_SQUARE1);
		square.setStyle (symbolStyle);

		gline.setVertexImage (square);
		
		gline.updateGeometry();
	
		
		lines.add( gline );
		
	}
	
	List<GPolyline> lines = new ArrayList<GPolyline>();

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void objectChanged(IControllerEvent<?> event) {
		updateGeometry();
	}

}
