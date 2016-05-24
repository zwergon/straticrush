package straticrush.view;


import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GTooltipInfo;
import fr.ifp.jdeform.continuousdeformation.Deformation;
import fr.ifp.kronosflow.mesh.Cell;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.IHandle;
import fr.ifp.kronosflow.model.Node;
import fr.ifp.kronosflow.model.PolyLine;
import fr.ifp.kronosflow.model.implicit.MeshPatch;
import fr.ifp.kronosflow.utils.UID;


public class MeshPatchView extends PatchView {
	
	public MeshPatchView(){
	}
	
	@Override
	public void setModel (Object object)
	{
		setUserData( object );
	
		MeshPatch patch = (MeshPatch)object;
				
		Mesh2D mesh = patch.getMesh();
		
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
	
	

	
	private class GCell extends GSegment implements IUpdateGeometry {
		
		private Mesh2D mesh;

		private Cell cell;

		public GCell( Mesh2D mesh, Cell cell ){
			this.mesh = mesh;
			this.cell = cell;
		}
		
		@Override
		public boolean canDeform() {
			return false;
		}

		@Override
		public void setDeformation(Deformation deformation) {
			// TODO Auto-generated method stub
		}

		@Override
		public void updateGeometry() {
			
			
			UID[] nodes = cell.getNodeIds();
			
			int npts = nodes.length+1;
			double[] xpts = new double[npts];
			double[] ypts = new double[npts];
			
			for( int i=0; i<npts; i++){
				Node node = (Node) mesh.getNode( cell.getNodeId(i % nodes.length) );
				xpts[i] = node.x();
				ypts[i] = node.y();
			}
			
			setGeometry(xpts, ypts);
			
		}
		
		@Override
		public GTooltipInfo getTooltipInfo() {
			GTooltipInfo info = new GTooltipInfo();
			info.setInfo( new String( "Cell uid: " + cell.getUID() ) );
			return info;
		}
		
	};
	
	

	private GSegment addCell( Mesh2D mesh, Cell cell ) {
		
		GSegment gcell = new GCell( mesh, cell );
		addSegment(gcell);
		
		GStyle style = new GStyle();
		style.setForegroundColor ( GColor.black );
		GColor color = getPatchColor();
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
	

	

	
}
