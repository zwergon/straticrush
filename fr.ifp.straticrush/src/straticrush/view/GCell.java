package straticrush.view;

import no.geosoft.cc.graphics.GSegment;
import fr.ifp.kronosflow.mesh.Cell;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.mesh.Node;
import fr.ifp.kronosflow.utils.UID;

public class GCell extends GSegment implements IMeshProvider, IUpdateGeometry {
	
	private Cell cell;
	private Mesh2D mesh;
	
	public GCell( Mesh2D mesh, Cell cell ){
		this.cell = cell;
		this.mesh = mesh;
	}
	
	public Cell getCell(){
		return cell;
	}
	
	@Override
	public Mesh2D getMesh() {
		return mesh;
	}
	
	@Override
	public void updateGeometry() {
		
		Cell cell = getCell();

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


};

