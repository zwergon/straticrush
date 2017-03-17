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

import java.util.List;

import fr.ifp.kronosflow.extensions.IExtension;
import fr.ifp.kronosflow.extensions.ray.ExtensionPoint;
import fr.ifp.kronosflow.model.FeatureInterval;
import fr.ifp.kronosflow.polyline.CurviPoint;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.PolyLineGeometry;
import fr.ifp.kronosflow.warp.IWarp;
import stratifx.canvas.graphics.GSegment;

public class GInterval extends GDeformableObject {
	
	
	
	GSegment gIntervalSegment;
	GExtension gExtension[] = new GExtension[IExtension.LAST];	
	
	public GInterval( FeatureInterval interval ){
		super(interval);
	
		
		gIntervalSegment = new GSegment();
		addSegment( gIntervalSegment );
		
		PolyLineGeometry geom = new PolyLineGeometry( interval );
		
		for( int i = IExtension.BEFORE; i< IExtension.LAST; i++ ){
			IExtension extension = interval.getExtension(i);
			gExtension[i] =  new GExtension( extension, geom.length()/5. );
			addSegment( gExtension[i] );
		}
		
	}
	

	public FeatureInterval getFeatureInterval(){
		return (FeatureInterval)getUserData();
	}
	

	@Override
	protected void warpedDraw(IWarp warp) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void directDraw() {
		
		drawInterval();
		for( int i = IExtension.BEFORE; i< IExtension.LAST; i++ ){
			gExtension[i].draw();
		}
	}


	private void drawInterval() {
		FeatureInterval interval = getFeatureInterval();
		
		List<ICurviPoint> pts = interval.getPoints();
		int npts =  pts.size();
		double[] xpts = new double[npts];
		double[] ypts = new double[npts];

		double[] w_pt = new double[2];
		int i = 0;
	
		for( ICurviPoint ip : pts ){
			CurviPoint tp = (CurviPoint)ip;
			interval.getPosition(tp, w_pt);
			xpts[i] = w_pt[0];
			ypts[i] = w_pt[1];
			i++;
		}	
		
		gIntervalSegment.setWorldGeometry(xpts, ypts);
	}


	public void addExtendedPoint(ExtensionPoint ep) {
		gExtension[ep.getExtensionType()].addExtendedPoint( ep );
		
	}



}
