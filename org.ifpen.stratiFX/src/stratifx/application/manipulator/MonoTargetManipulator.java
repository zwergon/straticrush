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
package stratifx.application.manipulator;

import java.util.ArrayList;

import fr.ifp.kronosflow.deform.controllers.callers.DeformationControllerCaller;
import fr.ifp.kronosflow.deform.deformation.IDeformationItem;
import fr.ifp.kronosflow.deform.deformation.IRigidItem;
import fr.ifp.kronosflow.deform.deformation.items.LinePairingItem;
import fr.ifp.kronosflow.deform.deformation.items.PatchIntersectionItem;
import fr.ifp.kronosflow.deform.deformation.items.TranslateItem;
import fr.ifp.kronosflow.kernel.geometry.Vector2D;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.geology.Paleobathymetry;
import fr.ifp.kronosflow.kernel.polyline.LineIntersection;
import fr.ifp.kronosflow.kernel.polyline.LinePointPair;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.interaction.GMouseEvent;

public class MonoTargetManipulator extends CompositeManipulator  {
	
	protected PatchInterval  selectedHorizon = null;


	public MonoTargetManipulator( GScene gscene, DeformationControllerCaller caller ){
		 super( gscene, caller );
	}
	
	
	@Override
	public boolean canDeform() {
		items = new ArrayList<IDeformationItem>();
		rigidItems = new ArrayList<IRigidItem>();

		// first computes PatchIntersectionItem
		Paleobathymetry bathy = selectedHorizon.getPatchLibrary().getPaleobathymetry();
		LineIntersection lineInter = new LineIntersection(bathy.getPolyline());

		LinePointPair I = lineInter.getFirstIntersection(selectedHorizon.getInterval());
		if (null != I) {
			LinePairingItem item = new PatchIntersectionItem(selectedHorizon, I);
			items.add(item);
		}
				
		Patch selectedPatch = deformationCaller.getScene().getSelected();
		rigidItems.add(new TranslateItem(selectedPatch, Vector2D.substract(prev, start)));

		// then restore initial geometry
		translateTo(start);
	
		return super.canDeform();
	}
	
	@Override
	public void onMousePress( GMouseEvent event ){
		
		if ( !isActive() ){
			return;
		}
		
		super.onMousePress(event);
		
		selectedHorizon = targetsExtractor.findHorizonFeature( start );
		
		
		if ( null != selectedHorizon ){
			selectedHorizon.getInterval().createExtension();	
			selectedPatchGraphic.addInterval(selectedHorizon);
		}

		
	}
	
	@Override
	public void onMouseRelease(GMouseEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void computeTargets() {

		selectedPatchGraphic.clearTargets();

        Paleobathymetry bathy = selectedHorizon.getPatchLibrary().getPaleobathymetry();
        LineIntersection lineInter = new LineIntersection(bathy.getPolyline());

        LinePointPair I = lineInter.getFirstIntersection(selectedHorizon.getInterval());
        if (null != I) {
            selectedPatchGraphic.addTarget(I.getPoint().getLine(), GColor.BLUE.darker() );
        }
        
		
	}
	

}
