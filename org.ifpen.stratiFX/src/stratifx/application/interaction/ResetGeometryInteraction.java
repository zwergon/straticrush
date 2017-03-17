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

import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.controllers.scene.SceneBuilder;
import fr.ifp.kronosflow.geoscheduler.Geoscheduler;
import fr.ifp.kronosflow.geoscheduler.IGeoschedulerCaller;
import fr.ifp.kronosflow.model.Patch;
import stratifx.application.manipulator.CompositeManipulator;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.interaction.GKeyEvent;
import stratifx.canvas.interaction.GMouseEvent;

public class ResetGeometryInteraction extends DeformationInteraction {
	

	public ResetGeometryInteraction( GScene scene ) {
		super( scene);
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

	@Override
	public boolean mouseEvent(GScene scene, GMouseEvent event) {
		if ( scene != this.scene_ ){
			return false;
		}

		switch (event.type) {
		case GMouseEvent.BUTTON_DOWN :
			Patch patch = getSelectedPatch(event.x, event.y);
			if ( patch !=  null ) {
                            DeformationControllerCaller caller = createCaller();
                        
                            Geoscheduler sheduler = getScheduler();                              
                            caller.revert( sheduler.getRoot().getWrapper() );

                            caller.publish();
                            scene.refresh();
                    }

			break;
		}
		
		return true;
	}

	@Override
	public boolean keyEvent(GScene scene, GKeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public CompositeManipulator createManipulator(GScene gscene, DeformationControllerCaller caller) {
		// TODO Auto-generated method stub
		return null;
	}

}
