/* 
 * Copyright 2017 lecomtje.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
		
		UID[] nodes = cell.getNodes();

		int npts = nodes.length+1;
		double[] xpts = new double[npts];
		double[] ypts = new double[npts];

		IGeometryProvider provider = mesh.getGeometryProvider();
		
		for( int i=0; i<npts; i++){
			double[] xy = provider.getPosition( cell.getNode(i % nodes.length) );
		
				xpts[i] = xy[0];
				ypts[i] = xy[1];
		}
		
		gcell.setWorldGeometry(xpts, ypts);
	}
	
	private void updateCellGeometry( GSegment gcell, IWarp warp ){
		
		Mesh2D mesh = getMesh();
		Cell cell = (Cell)gcell.getUserData();
		
		UID[] nodes = cell.getNodes();

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
					UID uid = cell.getNode(i % nodes.length);
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
			double[] xy = provider.getPosition( cell.getNode(i % nodes.length) );

			warp.getDeformed(xy, dst);
			xpts[i] = dst[0];
			ypts[i] = dst[1];

		}
		
		gcell.setWorldGeometry(xpts, ypts);
		
		
	}


}
