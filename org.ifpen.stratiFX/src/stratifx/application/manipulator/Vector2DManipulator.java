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

public class Vector2DManipulator extends CompositeManipulator {
	
	GTranslateObject translateMarker;
	
	private Node  selected_node_;
	
	private boolean canTranslate = true;

	public Vector2DManipulator(
			GScene gscene,
			DeformationControllerCaller caller ) {
		super(gscene, caller );
	}
	
	/**
	 * during mouse drag, marker stay at the same position, usefull
	 * to select one fixed node.
	 */
	protected void enableTranslate( boolean canTranslate ){
		this.canTranslate = canTranslate;
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
		
		
		int[] d_pos = transformer.worldToDevice(selected_node_.x(), selected_node_.y() );
		translateMarker.createMarker( d_pos[0], d_pos[1] );
		gscene.add(translateMarker);
		
	}

	@Override
	public void onMouseMove(GMouseEvent event) {

		if ( !isActive() ){
			return;
		}
	
		if ( canTranslate ){
			translateMarker.moveTo( event.x, event.y );
		}

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
