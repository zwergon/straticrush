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
package stratifx.application.interaction;

import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.algo.ComputeContact;
import stratifx.application.views.GPatchView;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.interaction.GInteraction;
import stratifx.canvas.interaction.GKeyEvent;
import stratifx.canvas.interaction.GMouseEvent;

public class ComputeContactInteraction implements GInteraction {

	Patch p1 = null;
	Patch p2 = null;
	GScene scene = null;
	
	public ComputeContactInteraction( GScene scene ){
		this.scene = scene;
	}
	
	@Override
	public boolean mouseEvent ( GScene scene, GMouseEvent event ){
		
		if ( this.scene != scene ){
			return false;
		}
		
		if ( event.modifier == GMouseEvent.BUTTON_1 ){

			switch (event.type) {
			case GMouseEvent.BUTTON_DOWN:
				GSegment selected = scene.findSegment (event.x, event.y);
				if ( selected !=  null ){
					GObject gobject = selected.getOwner();
					if ( gobject instanceof GPatchView ){
						GPatchView view = (GPatchView)gobject;
						if ( p1 == null ){
							p1 = view.getObject();
						}
						else if ( p2 == null ){
							p2 = view.getObject();
						}
					}
				}
				break;
			case GMouseEvent.BUTTON_UP:
				if ( ( p1 != null )&& ( p2 != null ) ){
					ComputeContact compute = new ComputeContact(p1);
					compute.execute(p2);
					p1 = null;
					p2 = null;
				}
			}

		}
		
		return true;

	}
	
	@Override
	public  boolean keyEvent   ( GScene scene, GKeyEvent event ){
		return false;
	}

	@Override
	public boolean start(GScene scene) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stop(GScene scene) {
		// TODO Auto-generated method stub
		return false;
	}

}
