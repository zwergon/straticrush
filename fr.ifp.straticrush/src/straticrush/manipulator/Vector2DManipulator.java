package straticrush.manipulator;

import java.util.List;

import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GTransformer;
import fr.ifp.jdeform.deformation.constraint.NodeMoveItem;
import fr.ifp.kronosflow.mesh.Node;
import fr.ifp.kronosflow.model.Patch;

public class Vector2DManipulator extends CompositeManipulator {
	
	GTranslateObject translateMarker;
	
	private Node  selected_node_;

	public Vector2DManipulator(
			GScene scene, 
			Patch selectedComposite,
			List<Patch> surroundedComposites) {
		super(scene, selectedComposite, surroundedComposites);
		
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
	public void onMousePress(GMouseEvent event) {

		GTransformer transformer = scene.getTransformer();
		double[] w_pos = transformer.deviceToWorld(event.x, event.y);
		selected_node_ = selectNode(selectedComposite, w_pos);
		
		
		translateMarker.createMarker(event.x, event.y);
		scene.add(translateMarker);
		
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
		scene.remove(translateMarker);
		
		items.clear();
		
		GTransformer transformer = scene.getTransformer();
		int[] displacement = translateMarker.getDisplacement();
		
		int[] d_pos = transformer.worldToDevice(selected_node_.getPosition());
		d_pos[0] += displacement[0];
		d_pos[1] += displacement[1];

		
		double[] w_pos = transformer.deviceToWorld(d_pos);
		
		NodeMoveItem item = new NodeMoveItem(selected_node_);
		item.setTarget(w_pos);
		
		items.add( item );
		
	}

}
