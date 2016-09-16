package straticrush.manipulator;

import java.util.ArrayList;

import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GTransformer;
import straticrush.manipulator.PointsManipulator.NodeMarker;
import straticrush.view.Plot;
import straticrush.view.View;
import fr.ifp.jdeform.continuousdeformation.IDeformationItem;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.controllers.scene.Scene;
import fr.ifp.jdeform.deformation.items.DisplacementItem;
import fr.ifp.jdeform.deformation.items.NodeMoveItem;
import fr.ifp.kronosflow.geometry.Vector2D;
import fr.ifp.kronosflow.model.ICurviPoint;
import fr.ifp.kronosflow.model.IPolyline;
import fr.ifp.kronosflow.model.LinePoint;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.warp.Displacement;
import fr.ifp.kronosflow.warp.LineDisplacement;
import fr.ifp.kronosflow.warp.LineNoDisplacement;

public class HorizonManipulator extends CompositeManipulator {
	
	View horizonView;

	public HorizonManipulator(GScene gscene, DeformationControllerCaller caller) {
		super(gscene, caller);
	}
	
	@Override
	public void onMousePress(GMouseEvent event) {
		
		Scene scene = deformationCaller.getScene();
		Patch selected = scene.getSelected();
		if ( null == selected ){
			return;
		}
		
		
		Plot plot = getPlot();
		GTransformer transformer = gscene.getTransformer();
		double[] w_pos = transformer.deviceToWorld(event.x, event.y);
		PatchInterval selectedHorizon = findHorizonFeature( w_pos );
		if ( null != selectedHorizon ){
			if ( null != horizonView ){
				plot.destroyView(horizonView);
			}
			
			horizonView = plot.createView(selectedHorizon);
		}
		
		gscene.redraw();
			
	}

	@Override
	public void onMouseMove(GMouseEvent event) {

		if ( !isActive() ){
			return;
		}
	
	}

	@Override
	public void onMouseRelease(GMouseEvent event) {
		
		Plot plot = getPlot();

		if ( null != horizonView ){
			plot.destroyView(horizonView);
		}

	}
	
	
	@Override
	public boolean canDeform() {
		items = new ArrayList<IDeformationItem>();
		
		if ( horizonView != null ){
			
			PatchInterval interval = (PatchInterval)horizonView.getUserData();
			
			IPolyline polyline = interval.getPolyline();
			for( ICurviPoint cp : polyline.getPoints()){
				LinePoint lp = new LinePoint( polyline,cp );
				DisplacementItem item = new DisplacementItem( new LineNoDisplacement(lp) );
				items.add( item );
			}
		}
		return super.canDeform();

	}

	@Override
	protected void computeTargets() {
		// TODO Auto-generated method stub

	}
	

}
