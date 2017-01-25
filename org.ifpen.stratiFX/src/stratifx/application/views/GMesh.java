package stratifx.application.views;

import fr.ifp.kronosflow.mesh.Cell;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.mesh.warp.MeshWarp;
import fr.ifp.kronosflow.polyline.IGeometryProvider;
import fr.ifp.kronosflow.uids.IHandle;
import fr.ifp.kronosflow.uids.UID;
import fr.ifp.kronosflow.warp.IWarp;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;

public class GMesh 
	extends 
		GDeformableObject
	implements 
		IMeshProvider 
{
	
	public GMesh( Mesh2D mesh ) {
		super(mesh);
		
		for( IHandle handle : mesh.getCells() ){
			addCell( mesh, (Cell)handle );
		}
	}
	
	@Override
	public Mesh2D getMesh(){
		return (Mesh2D)getUserData();
	}

	@Override
	protected void warpedDraw(IWarp warp) {
		for( GSegment segment : getSegments() ){
			Object userData = segment.getUserData();
			if ( userData instanceof Cell ){
				updateCellGeometry(segment, warp);
			}
		}	
	}

	@Override
	protected void directDraw() {
		for( GSegment segment : getSegments() ){
			Object userData = segment.getUserData();
			if ( userData instanceof Cell ){
				updateCellGeometry(segment);
			}
		}
	}
	
	private GSegment addCell( Mesh2D mesh, Cell cell ) {

		GSegment gcell = new GSegment(  );
		gcell.setUserData(cell);

		GStyle style = new GStyle();
		style.setForegroundColor ( GColor.BLUE );
		style.setBackgroundColor ( null );
		style.setFillPattern(GStyle.FILL_NONE);
		style.setLineWidth (1);
		gcell.setStyle (style);

		addSegment(gcell);

		return gcell;
	}
	
	private void updateCellGeometry( GSegment gcell ){
	
		Mesh2D mesh = getMesh();
		Cell cell = (Cell)gcell.getUserData();
		
		UID[] nodes = cell.getNodeIds();

		int npts = nodes.length+1;
		double[] xpts = new double[npts];
		double[] ypts = new double[npts];

		IGeometryProvider provider = mesh.getGeometryProvider();
		
		for( int i=0; i<npts; i++){
			double[] xy = provider.getPosition( cell.getNodeId(i % nodes.length) );
		
				xpts[i] = xy[0];
				ypts[i] = xy[1];
		}
		
		gcell.setWorldGeometry(xpts, ypts);
	}
	
	private void updateCellGeometry( GSegment gcell, IWarp warp ){
		
		Mesh2D mesh = getMesh();
		Cell cell = (Cell)gcell.getUserData();
		
		UID[] nodes = cell.getNodeIds();

		int npts = nodes.length+1;
		double[] xpts = new double[npts];
		double[] ypts = new double[npts];

		IGeometryProvider provider = mesh.getGeometryProvider();
		
		
		//speedup for specific case when warp is build upon the mesh
		//this graphic is representing.
		if ( warp instanceof MeshWarp ){
			MeshWarp meshWarp = (MeshWarp)warp; 
			if ( meshWarp.getMesh() == mesh ){
				
				for( int i=0; i<npts; i++){
					UID uid = cell.getNodeId(i % nodes.length);
					double[] xy = meshWarp.getTargetPosition(uid);
					xpts[i] = xy[0];
					ypts[i] = xy[1];
				}
				
				gcell.setWorldGeometry(xpts, ypts);
				return;			
			}
		}
		
		//default drawing.
		double[] dst = new double[2];	
		for( int i=0; i<npts; i++){
			double[] xy = provider.getPosition( cell.getNodeId(i % nodes.length) );

			warp.getDeformed(xy, dst);
			xpts[i] = dst[0];
			ypts[i] = dst[1];

		}
		
		gcell.setWorldGeometry(xpts, ypts);
		
		
	}


}
