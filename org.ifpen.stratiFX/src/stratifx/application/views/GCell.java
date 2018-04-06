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

import fr.ifp.jdeform.deformation.Deformation;
import fr.ifp.kronosflow.mesh.Cell;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.polyline.IGeometryProvider;
import fr.ifp.kronosflow.uids.UID;
import fr.ifp.kronosflow.warp.IWarp;
import stratifx.canvas.graphics.GSegment;

public class GCell extends GSegment implements IMeshProvider, IDeformableGeometry {
	
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
	
	
	public void updateGeometry() {
		
		Cell cell = getCell();

		UID[] nodes = cell.getNodes();

		int npts = nodes.length+1;
		double[] xpts = new double[npts];
		double[] ypts = new double[npts];

		IGeometryProvider provider = mesh.getGeometryProvider();
		IWarp warp = (deformation != null ) ? deformation.getWarp() : null;	
		
		for( int i=0; i<npts; i++){
			double[] xy = provider.getPosition( cell.getNode(i % nodes.length) );
			if ( null != warp ){
				double[] dst = new double[2];		
				warp.getDeformed(xy, dst);
				xpts[i] = dst[0];
				ypts[i] = dst[1];
			}
			else {
				xpts[i] = xy[0];
				ypts[i] = xy[1];
			}
		}
		
		setWorldGeometry(xpts, ypts);
		
	}
	
	/*@Override
	public GTooltipInfo getTooltipInfo() {
		GTooltipInfo info = new GTooltipInfo();
		String msg = new String( "CellFX uid: " + cell.getUid() );
		double[] values = getValues();
		if ( values != null ){
			for( int i=0; i<values.length; i++ ){
				msg += "\n("+i+") " + String.format("%.3f",values[i]) ;
			}
		}
		info.setInfo( msg  );
		return info;
	}*/


};

