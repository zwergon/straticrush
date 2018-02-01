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

import fr.ifp.jdeform.scene.Scene;
import fr.ifp.jdeform.scene.SceneBuilder;
import fr.ifp.kronosflow.geoscheduler.Geoscheduler;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerLink;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.filters.SvgExportPolylines;
import fr.ifp.kronosflow.model.implicit.MeshPatch;
import fr.ifp.kronosflow.model.style.Style;
import stratifx.application.main.StratiFXService;
import stratifx.application.plot.GFXScene;
import stratifx.application.views.GPatchView;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.interaction.GInteraction;
import stratifx.canvas.interaction.GKeyEvent;
import stratifx.canvas.interaction.GMouseEvent;

public class SectionInteraction implements GInteraction {

    protected GScene gscene;

    protected GeoschedulerLink link = null;

    protected Style style;

    public SectionInteraction(GScene scene) {
        gscene = scene;
    }

    public void setStyle(Style style) {
        this.style = style;
    }
    
    public Section getSection(){
        return StratiFXService.instance.getSection();
    }

    public Geoscheduler getScheduler() {

        Section section = getSection();
        if (section instanceof GeoschedulerSection) {
            return ((GeoschedulerSection) section).getGeoscheduler();
        }

        return null;
    }

    protected Patch getSelectedPatch(int x, int y) {
        GSegment selected = gscene.findSegment(x, y);
        if (selected != null) {
            GObject gobject = selected.getOwner();
            while (gobject != null) {
                if (gobject instanceof GPatchView) {
                    return ((GPatchView) gobject).getObject();
                }
                gobject = gobject.getParent();
            }

        }

        return null;
    }
    

    protected Scene createScene(Patch patch) {

        if ((patch.getPatchLibrary().getPatches().size() == 1)
                && (patch instanceof MeshPatch)) {
            Scene scene = new Scene();
            scene.setSelected(patch);
            return scene;
        }

        return SceneBuilder.createDefaultScene(patch, style);
    }

    @Override
    public boolean start(GScene scene) {
        return false;
    }

    @Override
    public boolean stop(GScene scene) {
        return false;
    }

    @Override
    public boolean mouseEvent(GScene scene, GMouseEvent event) {
        return false;
    }

    @Override
    public boolean keyEvent(GScene scene, GKeyEvent event) {


        if (event.type == GKeyEvent.KEY_PRESSED) {
            switch (event.getKeyCode()) {

                case GKeyEvent.VK_Z:
                    GFXScene gfxScene = (GFXScene)scene;
                    if ((event.getKeyModifiers() == GKeyEvent.CTRL_MASK)) {
                        getScheduler().removeCurrent();
                        gfxScene.refresh();
                    }
                    break;

                case GKeyEvent.VK_P:
                    if ((event.getKeyModifiers() == GKeyEvent.CTRL_MASK)) {
                        Section section = StratiFXService.instance.getSection();
                        SvgExportPolylines exporter = new SvgExportPolylines("/tmp/section.svg");
                        for (Patch patch : section.getPatchLibrary().getPatches()) {
                            exporter.add(patch.getBorder(), null, 50, null);
                        }
                        exporter.export();
                    }
                    break;
                default:
                    break;
            }

        }

        return true;
    }

}
