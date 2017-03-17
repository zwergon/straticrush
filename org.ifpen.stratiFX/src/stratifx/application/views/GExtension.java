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

import fr.ifp.kronosflow.extensions.IExtension;
import fr.ifp.kronosflow.extensions.IExtensionPoint;
import fr.ifp.kronosflow.extensions.ray.RayExtension;
import fr.ifp.kronosflow.polyline.LinePoint;
import stratifx.canvas.graphics.GSegment;

public class GExtension extends GSegment {
		
		private double length;
	
		IExtensionPoint ep;
		
		public GExtension( IExtension extension, double length ){
			setUserData(extension);
			this.length = length;
		}

		public IExtension getExtension(){
			return (IExtension)getUserData();
		}

		
		public void draw(){

			IExtension extension = getExtension();
			
			if ( extension instanceof RayExtension ){
				
				RayExtension re = (RayExtension)extension;
				
				double[] dir = re.getDirection();
				LinePoint lp = re.getStartPoint();
				
				int npts =  2;
				double[] xpts = new double[npts];
				double[] ypts = new double[npts];
				
				double[] pos = new double[2];
				lp.getPosition(pos);
				
				xpts[0] = pos[0];
				ypts[0] = pos[1];
				
				if ( ep != null ){
					double[] exy = new double[2];
					extension.getPosition(ep, exy);
					xpts[1] = exy[0];
					ypts[1] = exy[1];
				}
				else {
					xpts[1] = pos[0] + dir[0]*length;
					ypts[1] = pos[1] + dir[1]*length;
				}
				
				setWorldGeometry(xpts, ypts);
			}
		
		
			
		

		}

		public void addExtendedPoint(IExtensionPoint ep) {
			this.ep = ep;
		}

}
