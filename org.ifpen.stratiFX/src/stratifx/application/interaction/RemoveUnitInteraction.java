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

import fr.ifp.kronosflow.geoscheduler.GeoschedulerLink;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerLinkType;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.geology.StratigraphicUnit;
import stratifx.application.StratiFXService;
import stratifx.application.caller.RemoveUnitCaller;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.interaction.GMouseEvent;

public class RemoveUnitInteraction extends SectionInteraction {

    public RemoveUnitInteraction(GScene scene) {
        super(scene);
    }

    public RemoveUnitCaller getCaller() {
        return (RemoveUnitCaller) link.getCaller();
    }

    protected RemoveUnitCaller createCaller() {
        RemoveUnitCaller caller = new RemoveUnitCaller(StratiFXService.instance);

        link = new GeoschedulerLink(GeoschedulerLinkType.REMOVE_UNIT, caller);

        return caller;
    }

    @Override
    public boolean mouseEvent(GScene scene, GMouseEvent event) {
        if (scene != scene_) {
            return false;
        }

        switch (event.type) {
            case GMouseEvent.BUTTON_DOWN:
                GSegment selected = scene_.findSegment(event.x, event.y);
                if (selected != null) {
                    Patch patch = getSelectedPatch(event.x, event.y);
                    if (patch != null) {
                        StratigraphicUnit unit = patch.getGeologicFeaturesByClass(StratigraphicUnit.class);

                        if (unit != null) {
                            RemoveUnitCaller caller = createCaller();
                            caller.setUnitToRemove(unit);
                        }
                        scene_.refresh();
                    }
                }
                break;

            case GMouseEvent.BUTTON_DRAG:
                break;

            case GMouseEvent.BUTTON_UP:

                if (link != null) {
                    getScheduler().addCurrent(link);
                    link = null;
                    scene_.refresh();
                }
                break;

            case GMouseEvent.ABORT:
                break;
        }

        return true;
    }

}
