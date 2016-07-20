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
import fr.ifp.kronosflow.property.IPropertyAccessor;
import fr.ifp.kronosflow.property.IPropertyValue;
import fr.ifp.kronosflow.property.Property;
import fr.ifp.kronosflow.property.PropertyInfo;
import fr.ifp.kronosflow.property.PropertyNoData;
import fr.ifp.kronosflow.property.PropertyStatistic;
import fr.ifp.kronosflow.property.PropertyStyle;
import fr.ifp.kronosflow.utils.UID;


public class MeshPatchView extends PatchView {
	
	GColorMap colormap;
	
	Property currentProp;
	
	Section section;
	
	public MeshPatchView(){
		colormap = new GColorMap();
		colormap.createDefaultColormap();
	}
	
	@Override
	public void setModel (Object object)
	{
		setUserData( object );
	
		MeshPatch patch = (MeshPatch)object;
		
		section = patch.getPatchLibrary().getSection();
		
		currentProp = getCurrentProp();
		
		Mesh2D mesh = patch.getMesh();
		
		
		for( IHandle handle : mesh.getCells() ){
			addCell( mesh, (Cell)handle );
		}
		
		if ( null != patch.getBorder() ){
			addBorder( patch.getBorder() );
		}
		
	}

	private Property getCurrentProp() {
		
		Property property = null;
		PropertyStyle propStyle = new PropertyStyle(section.getStyle());
		UID currentPropUID = propStyle.getCurrent();
		if ( currentPropUID != null ){
			property = section.getPropertyDB().findByUID(currentPropUID);
			
			if ( null != property ){
				updateColormap(property);
			}
		}
		
		return property;
	}

	private void updateColormap(Property property ) {
		
		PropertyStatistic stat = PropertyStatistic.create(property);
		
		if ( stat == null ){
			return;
		}
		
		stat.compute();
		
		IPropertyValue min = stat.min();
		IPropertyValue max = stat.max();
		
		colormap.setMinMax( min.real(), max.real() );
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
		
		currentProp = getCurrentProp();
		
		PropertyInfo pinfo = currentProp.getPropertyInfo();
		switch( pinfo.getSupport() ){
		case NodeProperty:
			updateColorsFromMap();
			break;
		case BackgroundProperty:
		default:
			updateColorsFromBg();
			break;
			
		}
	}
				
	private void updateColorsFromBg() {
		for( Object segment : getSegments() ){
			if ( segment instanceof GCell ){
				GCell gcell = (GCell)segment;
				GStyle style = gcell.getStyle();
				Cell cell = gcell.getCell();
				GColor color = getColor(cell);
				style.unsetColormap();
				style.setBackgroundColor(color);
			}
		}
	}

	private void updateColorsFromMap() {
		for( Object segment : getSegments() ){
			if ( segment instanceof GCell ){
				GCell gcell = (GCell)segment;
				
				GStyle style = gcell.getStyle();
				
				Cell cell = gcell.getCell();
				
				IPropertyAccessor accessor = currentProp.getAccessor();
				UID[] uids = cell.getNodeIds();
				double[] values = new double[uids.length];
				for( int i =0; i<uids.length; i++ ){
					values[i] = accessor.getValue(uids[i]).real();
				}
				
				gcell.setValues(values);
				style.setColormap(colormap);
				style.unsetBackgroundColor();

				
			}
		}
	}
	
	private GColor getColor(Cell cell) {
		GColor color = null;
		if ( null != currentProp ){
			IPropertyValue val = currentProp.getAccessor().getValue( cell.getUID() );
			
			if ( val instanceof PropertyNoData ){
				color = new GColor(0,0,0,0);
			}
			else {
				color = colormap.getColor( val.real() );
			}
		}
		else {
			color = getPatchColor();
		}
		return color;
	}

}
