package straticrush.manipulator;

import java.util.ArrayList;

import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GTransformer;
import fr.ifp.jdeform.continuousdeformation.IDeformationItem;
import fr.ifp.jdeform.controllers.Scene;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.deformation.items.NodeMoveItem;
import fr.ifp.kronosflow.geometry.Vector2D;
import fr.ifp.kronosflow.model.Node;
import fr.ifp.kronosflow.model.Patch;

public class Vector2DManipulator extends CompositeManipulator {
	
	GTranslateObject translateMarker;
	
	private Node  selected_node_;

	public Vector2DManipulator(
			GScene gscene,
			DeformationControllerCaller caller ) {
		super(gscene, caller );
		withTranslateMarker = false;
	}
	
	@Override
	public void activate() {
		super.activate();
		selected_node_ = null;
		
		translateMarker = new GTranslateObject();
		
	}
	
	@Override
	public void deactivate() {
		translateMarker.removeSegments();;
		translateMarker.remove();
		selected_node_ = null;
		super.deactivate();
	};
	
	@Override
	public boolean canDeform() {
		
		items = new ArrayList<IDeformationItem>();
		GTransformer transformer = gscene.getTransformer();
		int[] start = translateMarker.getStart();
		double[] wStart = transformer.deviceToWorld(start);
		
		int[] end = translateMarker.getEnd();
		double[] wEnd = transformer.deviceToWorld(end);
		
		
		NodeMoveItem item = new NodeMoveItem(selected_node_);
		item.setDisplacement(Vector2D.substract(wEnd, wStart));
		
		items.add( item );
		return super.canDeform();
	}

	@Override
	public void onMousePress(GMouseEvent event) {
		
		Scene scene = deformationCaller.getScene();
		Patch selected = scene.getSelected();
		if ( null == selected ){
			return;
		}

		GTransformer transformer = gscene.getTransformer();
		double[] w_pos = transformer.deviceToWorld(event.x, event.y);
		selected_node_ = selectNode(selected, w_pos);
		
		
		translateMarker.createMarker(event.x, event.y);
		gscene.add(translateMarker);
		
	}

	@Override
	public void onMouseMove(GMouseEvent event) {

		if ( !isActive() ){
			return;
		}
	
		translateMarker.moveTo( event.x, event.y );

	}

	@Override
	public void onMouseRelease(GMouseEvent event) {
		
		if ( !isActive() ){
			return;
		}
		
		
		translateMarker.removeSegments();
		gscene.remove(translateMarker);		
		
	}

	@Override
	protected void computeTargets() {
		// Nothing to do
		
	}

}
