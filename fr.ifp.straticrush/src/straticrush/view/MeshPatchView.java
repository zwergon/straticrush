package straticrush.view;


import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import fr.ifp.kronosflow.controller.IControllerEvent;
import fr.ifp.kronosflow.mesh.Cell;
import fr.ifp.kronosflow.mesh.Node;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.IHandle;
import fr.ifp.kronosflow.model.PolyLine;
import fr.ifp.kronosflow.model.implicit.MeshPatch;
import fr.ifp.kronosflow.utils.UID;


public class MeshPatchView extends View {
	
	public MeshPatchView(){
	}
	
	public void setUserData (Object object)
	{
		super.setUserData( object );
	
		MeshPatch patch = (MeshPatch)object;
				
		Mesh2D mesh = patch.getMesh();
		
		for( IHandle handle : mesh.getCells() ){
			addCell( (Cell)handle);
		}
		
		if ( null != patch.getBorder() ){
			addBorder( patch.getBorder() );
		}
		
	}
	
	
	
	private class GCell extends GSegment {
		public GCell( Cell cell ){
			cell_ = cell;
		}
		
		public Cell getCell(){
			return cell_;
		}
		
		private Cell cell_;
	};
	
	

	private GSegment addCell(Cell cell) {
		
		GSegment gcell = new GCell( cell );
		addSegment(gcell);
		
		GStyle style = new GStyle();
		style.setForegroundColor ( GColor.black );
		style.setBackgroundColor ( GColor.white );
		style.setFillPattern(GStyle.FILL_NONE);
		style.setLineWidth (1);
		gcell.setStyle (style);
		
		return gcell;
	}
	
	private GSegment addBorder( PolyLine line ) {
		
		GSegment gborder = new GPolyline( line );
		addSegment(gborder);
		
			
		GStyle style = new GStyle();
		style.setForegroundColor ( GColor.red );
	
		style.setFillPattern(GStyle.FILL_NONE);
		style.setLineWidth (1);
		gborder.setStyle (style);
		
		return gborder;
	}
	
	
	public void draw()
	{
		update_geometry();
	}


	public void update_geometry() {
		Mesh2D mesh = getObject().getMesh();
		
		for( Object segment : getSegments() ){
			if ( segment instanceof GCell ){
				
				GCell gcell = (GCell)segment;
				Cell cell = gcell.getCell();
				
				UID[] nodes = cell.getNodeIds();
				
				int npts = nodes.length+1;
				double[] xpts = new double[npts];
				double[] ypts = new double[npts];
				
				for( int i=0; i<npts; i++){
					Node node = (Node) mesh.getNode( cell.getNodeId(i % nodes.length) );
					xpts[i] = node.x();
					ypts[i] = node.y();
				}	
				gcell.setGeometry(xpts, ypts);
			}
			else if ( segment instanceof GPolyline ){
				
				GPolyline gborder = (GPolyline)segment;
				gborder.updateGeometry();
			}
		}
	}
	
	public MeshPatch getObject(){
		return (MeshPatch)getUserData();
	}
	
	@Override
	public void objectChanged( IControllerEvent<?> event) {
		
		if ( event.getObject() == getObject() ){
			update_geometry();
		}
	}
	

	

}
