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

import fr.ifp.kronosflow.geoscheduler.Geoscheduler;
import fr.ifp.kronosflow.geoscheduler.algo.DisplacementsBetween;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.kernel.warp.barycentric.BarycentricWarp;
import fr.ifp.kronosflow.kernel.warp.Displacement;
import fr.ifp.kronosflow.kernel.warp.barycentric.HormannBarycentricWarp;
import java.util.ArrayList;
import java.util.List;
import stratifx.application.views.GDisplacement;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.interaction.GMouseEvent;

public class PatchDisplacementsInteraction extends SectionInteraction {

    GDisplacement gDisplacement;
    
    DisplacementsBetween dBetween;

    public PatchDisplacementsInteraction(GScene gfxScene) {
        super(gfxScene);
        
        Geoscheduler scheduler = getScheduler();
        dBetween = new DisplacementsBetween( scheduler.getCurrentPath(), scheduler.getRoot() );
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
                    createDisplacement( patch, event.x, event.y );
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

    private void createDisplacement(Patch patch, int x, int y) {
        
        dBetween.delta(patch);
            
        BarycentricWarp warp = new HormannBarycentricWarp();
        warp.setDisplacements(dBetween);
        
        double[] src = gscene.getTransformer().deviceToWorld( x, y );
        
        double[] dst = new double[2];
        warp.getUndeformed(src, dst);
        
        List<Displacement> currentDisplacement = new ArrayList<Displacement>();
        currentDisplacement.add( new Displacement(src, dst)  );
        gDisplacement = new GDisplacement(dBetween, currentDisplacement ) ;

        GStyle gStyle = new GStyle();

        gStyle.setForegroundColor(GColor.RED);
        gStyle.setLineWidth(2);

        gDisplacement.setStyle(gStyle);

        gscene.add(gDisplacement);

        gDisplacement.redraw();
    }

    
}
