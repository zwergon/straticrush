package stratifx.application.manipulator;

import java.util.ArrayList;

import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.deformation.IDeformationItem;
import fr.ifp.jdeform.deformation.IRigidItem;
import fr.ifp.jdeform.deformation.items.LinePairingItem;
import fr.ifp.jdeform.deformation.items.PatchIntersectionItem;
import fr.ifp.jdeform.deformation.items.TranslateItem;
import fr.ifp.kronosflow.geometry.Vector2D;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.geology.Paleobathymetry;
import fr.ifp.kronosflow.polyline.LineIntersection;
import fr.ifp.kronosflow.polyline.LinePointPair;
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
		
		selectedHorizon = findHorizonFeature( start );
		
		
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
            selectedPatchGraphic.addTarget(I.getPoint().getLine());
        }
        
		
	}
	

}
