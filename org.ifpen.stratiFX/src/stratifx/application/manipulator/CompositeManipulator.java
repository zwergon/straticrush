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
package stratifx.application.manipulator;

import java.util.List;

import fr.ifp.kronosflow.deform.controllers.TranslationController;
import fr.ifp.kronosflow.deform.controllers.callers.DeformationControllerCaller;
import fr.ifp.kronosflow.deform.deformation.Deformation;
import fr.ifp.kronosflow.deform.deformation.IDeformationItem;
import fr.ifp.kronosflow.deform.deformation.IRigidItem;
import fr.ifp.kronosflow.deform.scene.Scene;
import fr.ifp.kronosflow.deform.scene.algo.TargetsExtractor;
import fr.ifp.kronosflow.kernel.geometry.Vector2D;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.kernel.polyline.IPolylineProvider;
import fr.ifp.kronosflow.kernel.polyline.Node;
import fr.ifp.kronosflow.uids.IHandle;
import stratifx.application.views.GPatchObject;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.interaction.GMouseEvent;

public abstract class CompositeManipulator implements IStratiManipulator {

    protected GScene gscene;

    protected GPatchObject selectedPatchGraphic;

    double[] start;

    double[] prev;

    protected DeformationControllerCaller deformationCaller;

    List<IDeformationItem> items;

    List<IRigidItem> rigidItems;

    TranslationController translateController;

    protected TargetsExtractor targetsExtractor;

    List<IPolylineProvider> potentialHorizon;

    public CompositeManipulator(
            GScene gscene,
            DeformationControllerCaller caller) {
        this.gscene = gscene;
        this.deformationCaller = caller;

        translateController = new TranslationController();
        translateController.setScene(caller.getScene());

        targetsExtractor = new TargetsExtractor(caller.getScene());

    }

    @Override
    public void onMousePress(GMouseEvent event) {
        start = gscene.getTransformer().deviceToWorld(event.x, event.y);
        prev = Vector2D.copy(start);
    }

    @Override
    public void onMouseMove(GMouseEvent event) {

        double[] xy = gscene.getTransformer().deviceToWorld(event.x, event.y);
        translateTo(xy); // rigid body deformation
        computeTargets();

        if (null != selectedPatchGraphic) {
            selectedPatchGraphic.redraw();
        }
    }

    abstract protected void computeTargets();

    protected void translateTo(double[] xy) {
        double[] t = new double[]{xy[0] - prev[0], xy[1] - prev[1]};

        translateController.setTranslation(t);
        translateController.prepare();
        translateController.move();
        prev = Vector2D.copy(xy);
    }

    /**
     * By default, many manipulators are atomic. Only one clic. A manipulator
     * may be active ( visible ) but no more action is required for user. (
     * isManipulating == false ).
     */
    @Override
    public boolean isManipulating() {
        return false;
    }

    @Override
    public boolean isActive() {
        return (selectedPatchGraphic != null);
    }

    @Override
    public GObject getGraphic() {
        return selectedPatchGraphic;
    }

    public boolean canDeform() {
        if (items == null) {
            return false;
        }

        if (items.isEmpty()) {
            return false;
        }

        return true;
    }

    public List<IDeformationItem> getItems() {
        return items;
    }

    public List<IRigidItem> getRigidItems() {
        return rigidItems;
    }

    @Override
    public void activate() {

        Scene scene = deformationCaller.getScene();

        createPotentialTargets();

        if (null != scene.getSelected()) {
            selectedPatchGraphic = new GPatchObject();
            gscene.add(selectedPatchGraphic);
            for (Patch p : scene.getUnselected()) {
                selectedPatchGraphic.addOutline(p, true);
            }
            selectedPatchGraphic.addOutline(scene.getSelected(), false);
        }

        if (null != selectedPatchGraphic) {

            Deformation deformation = (Deformation) deformationCaller.getDeformation();
            selectedPatchGraphic.setDeformation(deformation);
            selectedPatchGraphic.redraw();
        }

    }

    @Override
    public void deactivate() {
        if (null != selectedPatchGraphic) {
            selectedPatchGraphic.setDeformation(null);
            gscene.remove(selectedPatchGraphic);
            selectedPatchGraphic.removeSegments();;
            selectedPatchGraphic.remove();
            selectedPatchGraphic = null;
        }
    }

    public void updateGraphics() {

        if (null != selectedPatchGraphic) {
            selectedPatchGraphic.redraw();
        }
    }

    protected Node selectNode(Patch patch, double[] pos) {

        double distance = Double.MAX_VALUE;
        Node nearest_node = null;
        for (IHandle ih : patch.getNodes()) {

            Node ctl_node = (Node) ih;

            double cur_distance = ctl_node.distance(pos);
            if (cur_distance < distance) {
                distance = cur_distance;
                nearest_node = ctl_node;
            }
        }
        return nearest_node;
    }

    protected void createPotentialTargets() {
        potentialHorizon = targetsExtractor.getHorizonTargets();
    }

}
