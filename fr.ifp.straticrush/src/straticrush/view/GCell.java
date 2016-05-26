package straticrush.view;

import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GTooltipInfo;
import fr.ifp.jdeform.continuousdeformation.Deformation;
import fr.ifp.kronosflow.mesh.Cell;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.Node;
import fr.ifp.kronosflow.utils.UID;
import fr.ifp.kronosflow.warp.IWarp;

public class GCell extends GSegment implements IMeshProvider, IUpdateGeometry {
	
	private Cell cell;
	private Mesh2D mesh;
	private Deformation deformation = null;
	
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
	public boolean canDeform() {
		return true;
	}
	
	@Override
	public void setDeformation( Deformation deformation ){
		this.deformation = deformation;
	}
	
	@Override
	public void updateGeometry() {
		
		Cell cell = getCell();

		UID[] nodes = cell.getNodeIds();

		int npts = nodes.length+1;
		double[] xpts = new double[npts];
		double[] ypts = new double[npts];

		IWarp warp = (deformation != null ) ? deformation.getWarp() : null;	
		if ( null == warp ){
			for( int i=0; i<npts; i++){
				Node node = (Node) mesh.getNode( cell.getNodeId(i % nodes.length) );
				xpts[i] = node.x();
				ypts[i] = node.y();
			}
		}
		else {
			double[] dst = new double[2];
			for( int i=0; i<npts; i++){
				Node node = (Node) mesh.getNode( cell.getNodeId(i % nodes.length) );
				warp.getDeformed(node.getPosition(), dst);
				xpts[i] = dst[0];
				ypts[i] = dst[1];
			}
		}
		setGeometry(xpts, ypts);
		
	}
	
	@Override
	public GTooltipInfo getTooltipInfo() {
		GTooltipInfo info = new GTooltipInfo();
		info.setInfo( new String( "Cell uid: " + cell.getUID() + "\n toto meuh") );
		return info;
	}


};

