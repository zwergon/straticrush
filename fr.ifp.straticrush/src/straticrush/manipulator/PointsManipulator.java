package straticrush.manipulator;

import java.util.ArrayList;
import java.util.Collection;

import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GTransformer;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.controllers.scene.Scene;
import fr.ifp.jdeform.deformation.IDeformationItem;
import fr.ifp.jdeform.deformation.items.NodeMoveItem;
import fr.ifp.kronosflow.geometry.Vector2D;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.polyline.Node;

public class PointsManipulator extends CompositeManipulator {
	
	Collection<NodeMarker> markers;
	
	
	boolean isManipulating;
	
	class NodeMarker {
		GTranslateObject marker;
		Node node;
		
		NodeMarker(GTranslateObject marker, Node node){
			this.marker = marker;
			this.node = node;
		}
	}
	

	public PointsManipulator(GScene gscene, DeformationControllerCaller caller) {
		super(gscene, caller);
	}
	
	@Override
	public void activate() {
		super.activate();
		isManipulating = true;
		markers = new ArrayList<NodeMarker>();
	}
	
	@Override
	public void deactivate() {
		for( NodeMarker nm : markers ){
			nm.marker.removeSegments();
			nm.marker.remove();
		}
		markers = null;
		
		isManipulating = false;
		super.deactivate();
		
	};
	
	@Override
	public boolean isManipulating() {
		return isManipulating;
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
		Node node = selectNode(selected, w_pos);
		
		
		int[] d_pos = transformer.worldToDevice(node.x(), node.y() );
		
		GTranslateObject marker = new GTranslateObject();
		marker.createMarker( d_pos[0], d_pos[1] );
		gscene.add(marker);
		
		markers.add( new NodeMarker( marker, node ) );
		
	}

	@Override
	public void onMouseRelease(GMouseEvent event) {
		if ( event.type == GMouseEvent.BUTTON3_UP ){
			isManipulating = false;
		}

	}
	
	@Override
	public void onMouseMove(GMouseEvent event) {
		//do nothing
	}
	
	@Override
	public boolean canDeform() {
		
		items = new ArrayList<IDeformationItem>();
		GTransformer transformer = gscene.getTransformer();
		
		for( NodeMarker nm : markers ){
			int[] start = nm.marker.getStart();
			double[] wStart = transformer.deviceToWorld(start);

			int[] end = nm.marker.getEnd();
			double[] wEnd = transformer.deviceToWorld(end);


			NodeMoveItem item = new NodeMoveItem(nm.node);
			item.setDisplacement(Vector2D.substract(wEnd, wStart));

			items.add( item );
		}
		return super.canDeform();
	}

	@Override
	protected void computeTargets() {
		// TODO Auto-generated method stub

	}

}
