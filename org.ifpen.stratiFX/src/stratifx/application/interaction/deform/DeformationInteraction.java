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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import fr.ifp.kronosflow.deform.controllers.DeformationController;
import fr.ifp.kronosflow.deform.controllers.callers.DeformationControllerCaller;
import fr.ifp.kronosflow.deform.deformation.Deformation;
import fr.ifp.kronosflow.deform.deformation.DeformationFactory;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerJob;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerLink;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerLinkType;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.kernel.polyline.IPolyline;
import fr.ifp.kronosflow.utils.LOGGER;
import stratifx.application.interaction.SectionInteraction;
import stratifx.application.main.GParameters;
import stratifx.application.main.StratiFXService;
import stratifx.application.manipulator.CompositeManipulator;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.interaction.GKeyEvent;
import stratifx.canvas.interaction.GMouseEvent;

public abstract class DeformationInteraction extends SectionInteraction {

    GeoschedulerJob moveJob;

    Timer animationTimer;

    protected boolean isJobFinished = false;

    /**
     * horizons that may be a target for deformation ordered using straticolumn
     */
    protected List<IPolyline> potentialHorizonTargets = new ArrayList<IPolyline>();

    CompositeManipulator manipulator;

    public abstract CompositeManipulator createManipulator(
            GScene gscene,
            DeformationControllerCaller caller);

    public DeformationInteraction(GScene scene) {
        super(scene);
        manipulator = null;
    }

    public void update() {
        synchronized (getController()) {
            manipulator.updateGraphics();
            gscene.refresh();
        }
    }

    public void end() {

        if (null != animationTimer) {
            animationTimer.cancel();
        }

        synchronized (getController()) {
            manipulator.deactivate();
            gscene.redraw();
            gscene.refresh();
        }

        animationTimer = null;
        manipulator = null;
        moveJob = null;

    }

    public DeformationController getController() {
        if (null != link) {
            return getCaller().getController();
        }

        return null;
    }

    public DeformationControllerCaller getCaller() {
        if (null != link) {
            return (DeformationControllerCaller) link.getCaller();
        }
        return null;
    }

    protected DeformationControllerCaller createCaller() {
        DeformationControllerCaller caller = new DeformationControllerCaller(StratiFXService.instance);

        Deformation deformation = DeformationFactory.getInstance().createDeformation(style);
        if (null == deformation) {
            LOGGER.debug("no deformation found", getClass());
        }

        caller.setDeformation(deformation);

        link = new GeoschedulerLink(GeoschedulerLinkType.DEFORMATION, caller);

        return caller;
    }

    @Override
    public boolean mouseEvent(GScene scene, GMouseEvent event) {
        if (scene != gscene) {
            return false;
        }

        if (moveJob != null) {
            return false;
        }

        DeformationController controller = getController();

        //can not interact during run of a simulation
        if ((controller != null) && controller.isRunning()) {
            return false;
        }

        switch (event.type) {
            case GMouseEvent.BUTTON_DOWN:

                if (manipulator == null) {
                    Patch patch = getSelectedPatch(event.x, event.y);
                    if (patch != null) {

                        DeformationControllerCaller caller = createCaller();

                        caller.clear();
                        caller.setScene(createScene(patch));
                        manipulator = createManipulator(scene, caller);
                        manipulator.activate();
                    }
                }

                if ((manipulator != null) && manipulator.isActive()) {
                    manipulator.onMousePress(event);
                }

                gscene.refresh();

                break;

            case GMouseEvent.BUTTON_DRAG:

                if ((null != manipulator) && manipulator.isActive()) {
                    manipulator.onMouseMove(event);
                    scene.refresh();
                }

                break;

            case GMouseEvent.BUTTON_UP:

                if ((null != manipulator) && manipulator.isActive()) {
                    manipulator.onMouseRelease(event);

                    CompositeManipulator compositeManipulator = (CompositeManipulator) manipulator;

                    if (!manipulator.isManipulating()) {

                        if (compositeManipulator.canDeform()) {

                            DeformationControllerCaller deformationCaller = getCaller();
                            deformationCaller.hasPostDeform(false);
                            deformationCaller.addItems(compositeManipulator.getItems());
                            deformationCaller.addRigidItems(compositeManipulator.getRigidItems());

                            deformationCaller.getDeformation().getStyle().cloneData(GParameters.getStyle());

                            moveJob = new GeoschedulerJob(getScheduler());
                            moveJob.compute(link);

                            animationTimer = DeformationAnimation.start(this);

                        } else {
                            end();
                        }
                    } else {
                        gscene.refresh();
                    }
                }
                break;

        }

        return true;

    }

    @Override
    public boolean keyEvent(GScene scene, GKeyEvent event) {

        if (event.type == GKeyEvent.KEY_PRESSED) {
            switch (event.getKeyCode()) {
                case GKeyEvent.VK_ESCAPE:
                    if (moveJob != null) {
                        moveJob.cancel();
                        moveJob = null;
                    }
                    break;
                default:
                    break;
            }

        }

        //if no job currently running, spread key event to parent.
        if (moveJob == null) {
            return super.keyEvent(scene, event);
        }

        return true;
    }

}
