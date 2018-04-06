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
package stratifx.application.interaction.deform;


import fr.ifp.dem.model.Material;
import fr.ifp.dem.util.Compact2DEM;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.scene.Scene;
import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.mesh.compact2D.Compact2D;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.builder.Compact2DPatchBuilder;
import java.util.List;

import stratifx.application.manipulator.CompositeManipulator;
import stratifx.application.manipulator.DEMManipulator;
import stratifx.canvas.graphics.GScene;


public class AntiGravityInteraction extends DeformationInteraction {

	
	public AntiGravityInteraction( GScene scene ){
		super(scene);
	}
	
        @Override
        public CompositeManipulator createManipulator(GScene gscene, DeformationControllerCaller caller ) {
            
            Scene scene = caller.getScene();
            
            Compact2DPatchBuilder builder = new Compact2DPatchBuilder();
            
            Patch selected = scene.getSelected();
            List<Point2D> pts = selected.getBorder().getPoints2D();
            
            builder.initialize(selected, pts);
            
            Compact2D compact = (Compact2D)builder.createMesh( scene.getSelected().getBorder().getPoints2D() );
            
            Material material = Compact2DEM.create(compact);
            
            material.addWallToDomain();
            material.update();
            
            
            return new DEMManipulator(gscene, caller, material);
        }
	
	

	
}


