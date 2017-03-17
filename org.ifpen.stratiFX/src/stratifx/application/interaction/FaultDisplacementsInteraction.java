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

import fr.ifp.jdeform.controllers.scene.algo.TargetsExtractor;
import fr.ifp.kronosflow.geoscheduler.Geoscheduler;
import fr.ifp.kronosflow.geoscheduler.algo.DisplacementsBetween;
import fr.ifp.kronosflow.model.FeatureGeolInterval;
import fr.ifp.kronosflow.model.FeatureInterval;
import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.algo.FaultMasterSlaveBuilder;
import fr.ifp.kronosflow.model.geology.BoundaryFeature;
import fr.ifp.kronosflow.model.geology.FaultFeature;
import fr.ifp.kronosflow.polyline.IPolylineProvider;
import fr.ifp.kronosflow.warp.barycentric.BarycentricWarp;
import fr.ifp.kronosflow.warp.Displacement;
import fr.ifp.kronosflow.warp.barycentric.HormannBarycentricWarp;
import java.util.ArrayList;
import java.util.List;
import stratifx.application.plot.GFXScene;
import stratifx.application.views.GDisplacement;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.interaction.GMouseEvent;

public class FaultDisplacementsInteraction extends SectionInteraction {

    GDisplacement gDisplacement;

    DisplacementsBetween dBetween;

    public FaultDisplacementsInteraction(GFXScene gfxScene) {
        super(gfxScene);

        Geoscheduler scheduler = getScheduler();
        dBetween = new DisplacementsBetween(scheduler.getCurrentPath(), scheduler.getRoot());
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
                    selectFault(patch, event.x, event.y);
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

    private void selectFault(Patch patch, int x, int y) {

        double[] src = scene_.getTransformer().deviceToWorld(x, y);
        
        TargetsExtractor extractor = new TargetsExtractor(patch, null);
        PatchInterval faultInterval = extractor.findFaultFeature(src);
        
        FaultMasterSlaveBuilder builder = new FaultMasterSlaveBuilder( getScheduler().getSection() );
        builder.compute( (FaultFeature)faultInterval.getInterval().getFeature() );

    }

}
