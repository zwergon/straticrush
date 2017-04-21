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
import java.util.Collection;

import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.deformation.IDeformationItem;
import fr.ifp.jdeform.deformation.items.NodeMoveItem;
import fr.ifp.jdeform.scene.Scene;
import fr.ifp.kronosflow.geometry.Vector2D;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.polyline.Node;
import stratifx.application.views.GTranslateObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GTransformer;
import stratifx.canvas.interaction.GMouseEvent;

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
		if ( event.type == GMouseEvent.BUTTON_UP ){
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
