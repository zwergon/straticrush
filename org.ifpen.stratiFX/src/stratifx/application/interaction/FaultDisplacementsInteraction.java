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

import fr.ifp.jdeform.controllers.scene.Scene;
import fr.ifp.jdeform.controllers.scene.algo.TargetsExtractor;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.jdeform.controllers.scene.algo.FaultMasterSlaveExtractor;
import fr.ifp.kronosflow.model.geology.FaultFeature;
import fr.ifp.kronosflow.utils.LOGGER;
import stratifx.application.plot.GFXScene;
import stratifx.application.views.GDisplacement;
import stratifx.application.views.GPolyline;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.interaction.GMouseEvent;

public class FaultDisplacementsInteraction extends SectionInteraction {

    GPolyline gFault;

    Scene scene;

    public FaultDisplacementsInteraction(GFXScene gfxScene) {
        super(gfxScene);
    }

    @Override
    public boolean mouseEvent(GScene gscene, GMouseEvent event) {
        if (gscene != this.gscene) {
            return false;
        }

        switch (event.type) {
            case GMouseEvent.BUTTON_DOWN:
                Patch patch = getSelectedPatch(event.x, event.y);
                if (patch != null) {
                    scene = createScene(patch);
                    selectFault(event.x, event.y);
                    gscene.redraw();
                    gscene.refresh();
                }

                break;
            case GMouseEvent.BUTTON_UP:
                if (gFault != null) {
                    gscene.remove(gFault);
                    gscene.refresh();
                }

                break;
        }

        return true;
    }

    private void selectFault(int x, int y) {

        double[] src = gscene.getTransformer().deviceToWorld(x, y);

        TargetsExtractor extractor = new TargetsExtractor(scene);
        PatchInterval faultInterval = extractor.findFaultFeature(src);

        if (null == faultInterval) {
            LOGGER.warning("No fault in the Scene !", getClass());
            return;
        }

        FaultFeature faultFeature = (FaultFeature) faultInterval.getInterval().getFeature();
        LOGGER.debug("fault selected " + faultFeature.getName(), getClass());

        FaultMasterSlaveExtractor builder = new FaultMasterSlaveExtractor(scene);
        builder.compute(faultFeature);

        gFault = new GPolyline(builder.getMergedFault());
        gscene.add(gFault);

        GStyle style = new GStyle();
        style.setForegroundColor(GColor.red);
        style.setLineWidth(2);
        gFault.setStyle(style);

    }

}
