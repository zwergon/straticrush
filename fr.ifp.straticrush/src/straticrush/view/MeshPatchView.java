package straticrush.view;


import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GColorMap;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import fr.ifp.kronosflow.mesh.Cell;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.IHandle;
import fr.ifp.kronosflow.model.PolyLine;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.implicit.MeshPatch;
import fr.ifp.kronosflow.newevents.IControllerEvent;
import fr.ifp.kronosflow.property.Property;
import fr.ifp.kronosflow.property.PropertyDouble;
import fr.ifp.kronosflow.property.PropertyStatistic;
import fr.ifp.kronosflow.property.PropertyStyle;
import fr.ifp.kronosflow.utils.UID;


public class MeshPatchView extends PatchView {
	
	GColorMap colormap;
	
	Property currentProp;
	
	Section section;
	
	public MeshPatchView(){
		colormap = new GColorMap();
		colormap.createFromName("Africa");
	}
	
	@Override
	public void setModel (Object object)
	{
		setUserData( object );
	
		MeshPatch patch = (MeshPatch)object;
		
		section = patch.getPatchLibrary().getSection();
		
		currentProp = null;
		getCurrentProp();
		
		
		
				
		Mesh2D mesh = patch.getMesh();
		
		
		for( IHandle handle : mesh.getCells() ){
			addCell( mesh, (Cell)handle );
		}
		
		if ( null != patch.getBorder() ){
			addBorder( patch.getBorder() );
		}
		
	}

	private void getCurrentProp() {
		PropertyStyle propStyle = new PropertyStyle(section.getStyle());
		UID currentPropUID = propStyle.getCurrent();
		if ( currentPropUID != null ){
			currentProp = section.getPropertyDB().findByUID(currentPropUID);
			
			if ( null != currentProp ){
				PropertyStatistic stat = PropertyStatistic.create(currentProp);
				stat.compute();
				
				PropertyDouble min = (PropertyDouble)stat.min();
				PropertyDouble max = (PropertyDouble)stat.max();
				colormap.setMinMax( min.getValue(), max.getValue() );
			}
		}
	}
	
	public MeshPatch getObject(){
		return (MeshPatch)getUserData();
	}
	

	private GSegment addCell( Mesh2D mesh, Cell cell) {
		
		GSegment gcell = new GCell( mesh, cell );
		addSegment(gcell);
		
		
		GColor color = getColor(cell);
		
		GStyle style = new GStyle();
		style.setForegroundColor ( GColor.black );
		style.setBackgroundColor(  color );
		style.setFillPattern(GStyle.FILL_NONE);
		style.setLineWidth (1);
		gcell.setStyle (style);
		
		return gcell;
	}

	private GColor getColor(Cell cell) {
		GColor color = null;
		if ( null != currentProp ){
			PropertyDouble val = (PropertyDouble)currentProp.getAccessor().getValue( cell.getUID() );
			color = colormap.getColor( val.getValue() );
		}
		else {
			color = getPatchColor();
		}
		return color;
	}
	
	private void addBorder( PolyLine line ) {
		
		border = new GPolyline( line );
		addSegment(border);
		
			
		GStyle style = new GStyle();
		style.setForegroundColor ( GColor.red );
	
		style.setFillPattern(GStyle.FILL_NONE);
		style.setLineWidth (1);
		border.setStyle (style);
		
	}
	
	
	@Override
	public void objectChanged( IControllerEvent<?> event ) {	
		
		switch( event.getEventAction() ){
		case MOVE:
			 updateGeometry();
			break;
		case UPDATE:
			updateColors();
			break;
		default:
			break;		
		}
		
		 
	}

	private void updateColors() {
		
		getCurrentProp();

		for( Object segment : getSegments() ){
			if ( segment instanceof GCell ){
				GCell gcell = (GCell)segment;
				GStyle style = gcell.getStyle();
				
				Cell cell = gcell.getCell();
				GColor color = getColor(cell);
				style.setBackgroundColor(color);
			}
		}
		
	}
	

	

	
}
