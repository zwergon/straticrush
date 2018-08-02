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

import fr.ifp.kronosflow.deform.deformation.Deformation;
import fr.ifp.kronosflow.kernel.warp.IWarp;
import stratifx.canvas.graphics.GObject;

public abstract class GDeformableObject  
	extends 
		GObject 
	implements 
		IDeformableGeometry 
{
	
	Deformation deformation = null;
	
	boolean enabledDeformation = true;
	
	
	public GDeformableObject( Object object ) {
		setUserData(object);
	}
	
	@Override
	public boolean canDeform() {
		return enabledDeformation;
	}
	
	public void enableDeformation( boolean canDeform ){
		enabledDeformation = canDeform;
	}
	
	@Override
	public void setDeformation(Deformation deformation){
		this.deformation = deformation;
	}
	
	@Override
	protected void draw() {
		
		IWarp warp = (deformation != null ) ? deformation.getWarp() : null;	
		if ( enabledDeformation && ( null != warp ) ){	
			warpedDraw(warp);
		}
		else {
			directDraw();
		}
	}
	
	
	protected abstract void warpedDraw( IWarp warp );
	protected abstract void directDraw();

}
