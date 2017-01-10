package stratifx.application.manipulator;

import java.util.ArrayList;

import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.controllers.scene.Scene;
import fr.ifp.jdeform.deformation.IDeformationItem;
import fr.ifp.jdeform.deformation.items.DisplacementItem;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.IPolyline;
import fr.ifp.kronosflow.polyline.LinePoint;
import fr.ifp.kronosflow.warp.LineNoDisplacement;
import stratifx.application.plot.GFXScene;
import stratifx.application.views.GView;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GTransformer;
import stratifx.canvas.interaction.GMouseEvent;

public class HorizonManipulator extends CompositeManipulator {
	
	GView horizonView;

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
		
		
		GFXScene gfxScene = (GFXScene)gscene;
		GTransformer transformer = gscene.getTransformer();
		double[] w_pos = transformer.deviceToWorld(event.x, event.y);
		PatchInterval selectedHorizon = findHorizonFeature( w_pos );
		if ( null != selectedHorizon ){
			if ( null != horizonView ){
				gfxScene.destroyView(horizonView);
			}
			
			horizonView = gfxScene.createView(selectedHorizon);
		}
		
		gscene.refresh();
			
	}

	@Override
	public void onMouseMove(GMouseEvent event) {

		if ( !isActive() ){
			return;
		}
	
	}

	@Override
	public void onMouseRelease(GMouseEvent event) {
		
		GFXScene gfxScene = (GFXScene)gscene;

		if ( null != horizonView ){
			gfxScene.destroyView(horizonView);
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