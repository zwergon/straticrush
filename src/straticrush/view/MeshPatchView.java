package straticrush.view;


import java.util.List;

import straticrush.interaction.IViewListener;
import straticrush.interaction.StratiCrushServices;
import fr.ifp.kronosflow.controller.Event;
import fr.ifp.kronosflow.model.CtrlNode;
import fr.ifp.kronosflow.model.CurviPoint;
import fr.ifp.kronosflow.model.PolyLine;
import fr.ifp.kronosflow.model.implicit.Cell;
import fr.ifp.kronosflow.model.implicit.Mesh2D;
import fr.ifp.kronosflow.model.implicit.MeshPatch;
import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;


public class MeshPatchView extends View {
	
	public MeshPatchView(){
	}
	
	public void setUserData (Object object)
	{
		super.setUserData( object );
	
		MeshPatch patch = (MeshPatch)object;
				
		Mesh2D mesh = patch.getMesh();
		
		for( Cell cell : mesh.getCells() ){
			addCell(cell);
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
				
				int[] nodes = cell.getNodes();
				
				int npts = nodes.length+1;
				double[] xpts = new double[npts];
				double[] ypts = new double[npts];
				
				for( int i=0; i<npts; i++){
					CtrlNode node = mesh.getNode( cell.getNode(i % nodes.length) );
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
	public void objectChanged( Object shape, Event event) {
		
		if ( shape == getObject() ){
			update_geometry();
		}
	}
	

	

}
