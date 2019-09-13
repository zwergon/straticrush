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
package stratifx.application.interaction.tools;

import fr.ifp.kronosflow.model.Patch;
import stratifx.application.interaction.SectionInteraction;
import stratifx.application.views.GVectorField;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.interaction.GMouseEvent;

/**
 *
 * @author lecomtje
 */
public class TangentInteraction extends SectionInteraction {
    
    GVectorField gField;
    
    public TangentInteraction(GScene scene) {
        super(scene);
    }
    
       @Override
    public boolean mouseEvent(GScene scene, GMouseEvent event) {
        if (scene != this.gscene) {
            return false;
        }

        switch (event.type) {
            case GMouseEvent.BUTTON_DOWN:
                Patch patch = getSelectedPatch(event.x, event.y);
                if (patch != null) {
                    createVectorField( patch );
                    scene.refresh();
                }

                break;
            case GMouseEvent.BUTTON_UP:
                if (gField != null) {
                    scene.remove(gField);
                    scene.refresh();
                }

                break;
        }

        return true;
    }

    private void createVectorField(Patch patch) {
       
        createCompositeScene(patch);
    }
    
}
