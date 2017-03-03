/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratifx.application.interaction;

import fr.ifp.kronosflow.model.Patch;
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
        if (scene != this.scene_) {
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
       
        createScene(patch);
    }
    
}
