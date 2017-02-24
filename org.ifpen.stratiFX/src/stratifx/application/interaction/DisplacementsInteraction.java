/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratifx.application.interaction;

import fr.ifp.kronosflow.geoscheduler.Geoscheduler;
import fr.ifp.kronosflow.geoscheduler.algo.DisplacementsBetween;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.warp.IUIDDisplacements;
import stratifx.application.plot.GFXScene;
import stratifx.application.views.GDisplacement;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.interaction.GMouseEvent;

public class DisplacementsInteraction extends SectionInteraction {

    GDisplacement gDisplacement;

    public DisplacementsInteraction(GFXScene gfxScene) {
        super(gfxScene);
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
                    createDisplacement(patch);
                    scene.refresh();
                }

                break;
            case GMouseEvent.BUTTON_UP:
                if (gDisplacement != null) {
                    scene.remove(gDisplacement);
                    scene.refresh();
                }

                break;
        }

        return true;
    }

    private void createDisplacement(Patch patch) {

        IUIDDisplacements dBetween = getDisplacements();
        gDisplacement = new GDisplacement(patch.getBorder(), dBetween);

        GStyle gStyle = new GStyle();

        gStyle.setForegroundColor(GColor.RED);
        gStyle.setLineWidth(2);

        gDisplacement.setStyle(gStyle);

        scene_.add(gDisplacement);

        gDisplacement.redraw();
    }

    private DisplacementsBetween getDisplacements() {
        Geoscheduler scheduler = getScheduler();
        DisplacementsBetween dBetween = new DisplacementsBetween( scheduler.getCurrentPath() );
        dBetween.deltaWith(scheduler.getRoot());
        return dBetween;
    }
    
}
