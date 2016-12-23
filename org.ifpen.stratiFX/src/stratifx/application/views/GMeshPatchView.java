package stratifx.application.views;


import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.mesh.Cell;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.implicit.MeshPatch;
import fr.ifp.kronosflow.polyline.Node;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.property.IPropertyAccessor;
import fr.ifp.kronosflow.property.IPropertyValue;
import fr.ifp.kronosflow.property.Property;
import fr.ifp.kronosflow.property.PropertyInfo;
import fr.ifp.kronosflow.property.PropertyLocation;
import fr.ifp.kronosflow.property.PropertyNoData;
import fr.ifp.kronosflow.uids.IHandle;
import fr.ifp.kronosflow.uids.UID;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GColorMap;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;


public class GMeshPatchView extends GPatchView {
	
	Property currentProp;
	GColorMap colormap;
		
	
	public GMeshPatchView(){
		
	}
	
	@Override
	public void setModel (Object object)
	{
		setUserData( object );
	
		MeshPatch patch = (MeshPatch)object;
		
		mesh = patch.getMesh();
		
		for( IHandle handle : mesh.getCells() ){
			addCell( mesh, (Cell)handle );
		}
		
		if ( null != patch.getBorder() ){
			addBorder( patch.getBorder() );
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

	
	
	private void addBorder( PolyLine line ) {
		
		border = new GPolyline( line );
		addSegment(border);
				
		GStyle style = new GStyle();
		style.setForegroundColor ( GColor.red );
	
		style.setFillPattern(GStyle.FILL_NONE);
		style.setLineWidth (2);
		border.setStyle (style);
		
	}
	
	
	@Override
	public void modelChanged( IControllerEvent<?> event ) {	
		
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
		
		
		//TODO
		/*Plot plot = getPlot();
		currentProp = plot.getCurrentProp();*/
		if ( null == currentProp ){
			return;
		}
		
		/*colormap = plot.getColorMap();
		*/
		if ( null == colormap ){
			return;
		}
				
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
				
				double[] values = new double[1];
				if ( (currentProp!=null) && ( mesh != null ) ){
					IPropertyAccessor accessor = currentProp.getAccessor();
					PropertyLocation location = cell.getLocation(mesh.getGeometryProvider());
					values[0] = accessor.getValue(location).real();
					gcell.setValues(values);
				}
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
					Node node =(Node)mesh.getNode(uids[i]);
					PropertyLocation location = node.getLocation();
					values[i] = accessor.getValue(location).real();
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
			
			
			PropertyLocation location = cell.getLocation(mesh.getGeometryProvider());
			IPropertyValue val = currentProp.getAccessor().getValue( location );
			
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
