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

import java.util.ArrayList;
import java.util.List;

import fr.ifp.jdeform.deformation.Deformation;
import fr.ifp.kronosflow.controllers.events.EnumEventAction;
import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.extensions.ray.ExtensionPoint;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.model.FeatureInterval;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.ICurviPoint.CoordType;
import fr.ifp.kronosflow.polyline.IPolyline;
import fr.ifp.kronosflow.polyline.LinePoint;
import fr.ifp.kronosflow.polyline.LinePointPair;
import fr.ifp.kronosflow.polyline.PolyLineGeometry;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GStyle;

public class GPatchObject extends GView  {
	
	boolean withPatchGrid = true;
		
	List<GDeformableObject> targets = new ArrayList<GDeformableObject>();
	
	public GPatchObject(){
		super("Interaction");
		setVisibility( DATA_VISIBLE | SYMBOLS_VISIBLE );	
	}
	
	public void enableGrid( boolean withGrid ){
		withPatchGrid = withGrid;
	}
	
	@Override
	public void setModel(Object object) {
		// TODO Auto-generated method stub	
	}
	
	public void setDeformation(Deformation deformation ){
		
		for( GObject gobject : getChildren() ){
			if ( gobject instanceof IDeformableGeometry ){
				((IDeformableGeometry)gobject).setDeformation(deformation);
			}
		}
	}
	
	public void addInterval( PatchInterval interval ){
		
		
		FeatureInterval featureInterval = interval.getInterval();
		GInterval gInterval = new GInterval( featureInterval );
		gInterval.enableDeformation(false);
		add( gInterval );
		
		GStyle style = new GStyle();
		style.setForegroundColor( GColor.fromAWTColor(interval.getColor() )  );
		style.setLineWidth(2);
		gInterval.setStyle( style );
		
		gInterval.draw();
	}
	
	
	public void addOutline( Patch patch, boolean surrounding ){
		
		GPolyline gBorder = new GPolyline(patch.getBorder());
		
		GStyle style = new GStyle();
		if ( !surrounding ){
			//style.setBackgroundColor(GColor.CYAN);
		}
		else {
			style.setBackgroundColor(GColor.ORANGE);
			gBorder.enableDeformation(false);
		}
		gBorder.setStyle (style);
		
		add( gBorder );
	
		gBorder.draw();
				
		if ( withPatchGrid &&  !surrounding & ( patch instanceof IMeshProvider ) ){
				GMesh gMesh = new GMesh( ((IMeshProvider)patch).getMesh() );
				add( gMesh );
				
				gMesh.draw();
		}
		
	}
	
	
	public void clearTargets(){
		for ( GDeformableObject line : targets ){
			remove(line);
		}
		targets.clear();
	}
	
	
	public void addTarget( LinePoint lp, GColor color ) {
		
		ICurviPoint cp = lp.getCurviPoint();
		IPolyline targetLine = lp.getLine();
		
		GDeformableObject gline  = null;
		
		if  ( targetLine instanceof FeatureInterval ) {
			
			GInterval gInterval = new GInterval( (FeatureInterval)targetLine );
			if ( cp.getType() == CoordType.EXTENDED ){
				gInterval.addExtendedPoint( (ExtensionPoint)cp );
			}
			
			gline = gInterval;
		}
		else {
			gline = new GPolyline( targetLine );
		}
		

		
		GStyle style = new GStyle();
		style.setForegroundColor( color  );
		style.setLineWidth(2);
		gline.setStyle( style );
		
		add( gline );
		
		gline.draw();
		

		targets.add( gline );		
		
	}

	public void addTarget(IPolyline targetLine, GColor color ) {
		
		GDeformableObject gline  = null;
		if ( targetLine instanceof FeatureInterval ){
			gline = new GInterval( (FeatureInterval)targetLine );
			
		}
		else {
			gline = new GPolyline( targetLine );
		}
		
		
		GStyle style = new GStyle();
		style.setForegroundColor( color  );
		style.setLineWidth(2);
		gline.setStyle( style );
		
		add( gline );
		
		gline.draw();
		

		targets.add( gline );		
		
	}
	
	
	@Override
	public void modelChanged(IControllerEvent<?> event) {		
		switch ((EnumEventAction) event.getEventAction()) {
		case MOVE:
			redraw();
			break;
		default:
			break;	 
		}
	}

	


}
