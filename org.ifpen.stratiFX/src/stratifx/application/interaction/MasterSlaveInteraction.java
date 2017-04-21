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

import fr.ifp.jdeform.scene.FaultMS;
import fr.ifp.jdeform.scene.MasterSlave;
import fr.ifp.jdeform.scene.Scene;
import fr.ifp.jdeform.scene.algo.FaultExtractor;
import fr.ifp.jdeform.scene.algo.HorizonExtractor;
import fr.ifp.jdeform.scene.algo.HorizonFaultIntersection;
import fr.ifp.jdeform.scene.algo.MasterSlaveExtractor;
import fr.ifp.jdeform.scene.algo.TargetsExtractor;
import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.geology.BoundaryFeature;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.LinePoint;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.polyline.PolyLineGeometry;
import fr.ifp.kronosflow.utils.LOGGER;
import stratifx.application.plot.GFXScene;
import stratifx.application.views.GMasterSlave;
import stratifx.application.views.GPoints;
import stratifx.application.views.GPolyline;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.interaction.GMouseEvent;

public class MasterSlaveInteraction extends SectionInteraction {

    GPolyline gFault;

    GMasterSlave gFaultMS;

    GMasterSlave gHorizonMS;

    GPoints gPoints;

    Scene scene;

    public MasterSlaveInteraction(GFXScene gfxScene) {
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
                    MasterSlave faultMS = selectFault(event.x, event.y);
                    MasterSlave horizonMS = selectHorizon(event.x, event.y);

                    if ((null != faultMS) && (null != horizonMS)) {
                        createIntersection(faultMS, horizonMS);
                    }

                    gscene.refresh();
                }

                break;
            case GMouseEvent.BUTTON_UP:
                if (gFault != null) {
                    gscene.remove(gFault);
                }

                if (gFaultMS != null) {
                    gscene.remove(gFaultMS);
                }

                if (gHorizonMS != null) {
                    gscene.remove(gHorizonMS);
                }

                if (gPoints != null) {
                    gscene.remove(gPoints);
                }
                gscene.refresh();

                break;
        }

        return true;
    }

    private MasterSlave selectFault(int x, int y) {

        double[] src = gscene.getTransformer().deviceToWorld(x, y);

        TargetsExtractor extractor = new TargetsExtractor(scene);
        PatchInterval faultInterval = extractor.findFaultFeature(src);

        if (null == faultInterval) {
            LOGGER.warning("No fault in the Scene !", getClass());
            return null;
        }

        BoundaryFeature selectedFeature = (BoundaryFeature) faultInterval.getInterval().getFeature();
        LOGGER.debug("feature selected " + selectedFeature.getName(), getClass());

        MasterSlaveExtractor builder = new FaultExtractor(scene);
        builder.compute(selectedFeature);

        gFault = new GPolyline(builder.getMergedLine());

        gscene.add(gFault);

        GStyle style = new GStyle();
        style.setForegroundColor(GColor.red);
        style.setLineWidth(2);
        gFault.setStyle(style);
        gFault.redraw();

        /*gFaultMS = new GMasterSlave( builder.getMasterSlave());
        gscene.add(gFaultMS);
        
        gFaultMS.redraw();*/
        return builder.getMasterSlave();
    }

    private MasterSlave selectHorizon(int x, int y) {

        double[] src = gscene.getTransformer().deviceToWorld(x, y);

        TargetsExtractor extractor = new TargetsExtractor(scene);
        PatchInterval horizonInterval = extractor.findHorizonFeature(src);

        if (null == horizonInterval) {
            LOGGER.warning("No Horizon in the Scene !", getClass());
            return null;
        }

        BoundaryFeature selectedFeature = (BoundaryFeature) horizonInterval.getInterval().getFeature();
        LOGGER.debug("feature selected " + selectedFeature.getName(), getClass());

        MasterSlaveExtractor builder = new HorizonExtractor(scene);
        builder.compute(selectedFeature);

        gHorizonMS = new GMasterSlave(builder.getMasterSlave(), false);

        gscene.add(gHorizonMS);

        gHorizonMS.redraw();

        return builder.getMasterSlave();

    }

    private void createIntersection(MasterSlave fault, MasterSlave horizon) {

        FaultMS faultMS = (FaultMS) fault;

        HorizonFaultIntersection intersection = new HorizonFaultIntersection(horizon, fault);
        intersection.compute();

        gPoints = new GPoints();

        PolyLine faultSupport = faultMS.getSupport();
        PolyLineGeometry faultGeometry = new PolyLineGeometry(faultSupport);

        //search master side intersection
        LinePoint iM = intersection.getMasterIntersection();
        if (iM == null) {
            LOGGER.warning("no intersection with master side", getClass());
            return;
        }
        ICurviPoint iMCurvi = faultMS.getSupportPoint(iM.getPosition());

        Point2D inter = faultSupport.getPosition(iMCurvi);
        gPoints.addPoint(inter.getPosition(), GColor.GREEN);

        //search slave side intersection
        LinePoint iS = intersection.getSlaveIntersection();
        if (iS == null) {
            LOGGER.warning("no intersection with slave side", getClass());
            return;
        }
        ICurviPoint iSCurvi = faultMS.getSupportPoint(iS.getPosition());
        inter = faultSupport.getPosition(iSCurvi);
        gPoints.addPoint(inter, GColor.RED);

        int i = 0;
        for (LinePoint lp : fault.getSlavePoints()) {

            if (i % 3 == 0) {

                double[] pos = new double[2];
                lp.getPosition(pos);

                //gPoints.addPoint(pos, GColor.CYAN);
                ICurviPoint curviCur = faultMS.getSupportPoint(pos);

                double length = faultGeometry.signedLength(iSCurvi, curviCur);

                ICurviPoint curviTarget = faultGeometry.getPointAtLength(iMCurvi, length);

                Point2D target = faultSupport.getPosition(curviTarget);

                gPoints.addPoint(target.getPosition(), GColor.YELLOW);

            }
            i++;

        }

        gscene.add(gPoints);;

        gPoints.redraw();

    }

}
