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
import stratifx.application.manipulator.CompositeManipulator;
import stratifx.application.manipulator.Vector2DManipulator;
import stratifx.canvas.graphics.GScene;


public class NodeMoveInteraction extends DeformationInteraction {

	
	public NodeMoveInteraction( GScene scene ){
		super(scene);
	}
	
	@Override
	public CompositeManipulator createManipulator(GScene gscene, DeformationControllerCaller caller ) {
		return new Vector2DManipulator(gscene, caller);
	}
	
	

	
}


