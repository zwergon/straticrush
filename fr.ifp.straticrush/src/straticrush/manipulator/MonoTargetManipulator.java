package straticrush.manipulator;

import java.util.ArrayList;

import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import fr.ifp.jdeform.continuousdeformation.IDeformationItem;
import fr.ifp.jdeform.continuousdeformation.IRigidItem;
import fr.ifp.jdeform.controllers.Scene;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.deformation.items.LinePairingItem;
import fr.ifp.jdeform.deformation.items.PatchIntersectionItem;
import fr.ifp.jdeform.deformation.items.TranslateItem;
import fr.ifp.kronosflow.geology.Paleobathymetry;
import fr.ifp.kronosflow.geometry.Vector2D;
import fr.ifp.kronosflow.model.LinePointPair;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.algo.LineIntersection;

public class MonoTargetManipulator extends CompositeManipulator  {
	
	protected PatchInterval  selectedHorizon = null;
	
	protected GSegment selectedTargetHorizon;
	

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

		selectedPatchGraphic.clearLines();

        Paleobathymetry bathy = selectedHorizon.getPatchLibrary().getPaleobathymetry();
        LineIntersection lineInter = new LineIntersection(bathy.getPolyline());

        LinePointPair I = lineInter.getFirstIntersection(selectedHorizon.getInterval());
        if (null != I) {
            selectedPatchGraphic.addLine(I.getPoint().getLine());
        }
        
        selectedPatchGraphic.draw();
		
	}
	

}
