package stratifx.application.manipulator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import fr.ifp.jdeform.deformation.Deformation;
import fr.ifp.kronosflow.controllers.events.EnumEventAction;
import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.extensions.IExtension;
import fr.ifp.kronosflow.geology.BoundaryFeature;
import fr.ifp.kronosflow.mesh.Cell;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.FeatureInterval;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.polyline.IPolyline;
import fr.ifp.kronosflow.polyline.PolyLineGeometry;
import fr.ifp.kronosflow.uids.IHandle;
import stratifx.application.views.GCell;
import stratifx.application.views.GExtension;
import stratifx.application.views.GInterval;
import stratifx.application.views.GPolyline;
import stratifx.application.views.GView;
import stratifx.application.views.IUpdateGeometry;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GImage;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;

public class GPatchObject extends GView  {
	
	boolean withPatchGrid = true;
	
	Deformation deformation = null;
	
	
	public GPatchObject(){
		super("Interaction");
		setVisibility( DATA_VISIBLE | SYMBOLS_VISIBLE );	
	}
	
	
	public void enableGrid( boolean withGrid ){
		withPatchGrid = withGrid;
	}
	
	@Override
	public void setModel(Object object) {
		// TODO Auto-generated method stub	
	}
	
	public void setDeformation(Deformation deformation ){
		this.deformation = deformation; 
		List<GSegment> segments =  getSegments();
		if ( null != segments ){
			for( GSegment segment : segments ){
				if ( segment instanceof IUpdateGeometry ){
					((IUpdateGeometry)segment).setDeformation(deformation);
				}
			}
		}
	}
	
	public void addInterval( PatchInterval interval ){
		
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
		
		FeatureInterval inter = interval.getInterval();
		PolyLineGeometry geom = new PolyLineGeometry( inter );
		
		for( int i = IExtension.BEFORE; i< IExtension.LAST; i++ ){
			IExtension extension = inter.getExtension(i);
			GExtension gextension =  new GExtension(extension, geom.length()/5. );
			addSegment( gextension );
			
			gextension.setStyle( style );
			gextension.updateGeometry();
		}
		
	}
	
	
	public void addOutline( Patch patch, boolean surrounding ){
		
		GPolyline borderLine = new GPolyline(patch.getBorder());
		addSegment( borderLine );
		
		GStyle style = new GStyle();
		if ( !surrounding ){
			style.setBackgroundColor(GColor.CYAN);
		}
		else {
			style.setBackgroundColor(GColor.ORANGE);
			borderLine.enableDeformation(false);
		}
		//style.setLineWidth (4);
		borderLine.setStyle (style);
		
		borderLine.updateGeometry();
		
		if ( withPatchGrid &&  !surrounding & ( patch instanceof IMeshProvider ) ){
				Mesh2D mesh = ((IMeshProvider)patch).getMesh();
				for( IHandle handle : mesh.getCells() ){
					addCell( mesh, (Cell)handle );
				}
		}
		
	}
	
	@Override
	public void draw() {
		updateGeometry();
	}
	
	public void updateGeometry() {
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

	public void addLine(IPolyline targetLine) {
		
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


	private GSegment addCell( Mesh2D mesh, Cell cell ) {
		
		GCell gcell = new GCell( mesh, cell );
		
		gcell.setDeformation(deformation);
		GStyle style = new GStyle();
		style.setForegroundColor ( GColor.BLUE );
		style.setBackgroundColor ( null );
		style.setFillPattern(GStyle.FILL_NONE);
		style.setLineWidth (1);
		gcell.setStyle (style);
		
		addSegment(gcell);
		
		gcell.updateGeometry();
		
		return gcell;
	}

	@Override
	public void modelChanged(IControllerEvent<?> event) {		
		switch ((EnumEventAction) event.getEventAction()) {
		case MOVE:
			updateGeometry();
			break;
		default:
			break;	 
		}
	}


}
