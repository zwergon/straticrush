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
import fr.ifp.jdeform.scene.HorizonMS;
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
import java.util.HashMap;
import stratifx.application.views.GMasterSlave;
import stratifx.application.views.GPoints;
import stratifx.application.views.GPolyline;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.interaction.GMouseEvent;

public class MasterSlaveInteraction extends SectionInteraction {
    
    final static int G_FAULT = 0;
    final static int G_POINTS = 1;
    final static int G_FAULT_MS = 2;
    final static int G_HORIZON_MS = 3;
    
    HashMap<Integer, GObject>  gObjects = new HashMap<>();
    
    Scene scene;

    public MasterSlaveInteraction(GScene gfxScene) {
        super(gfxScene);
    }
    
    
    protected <T> T getGObject( int type ){
        return (T)gObjects.get(type);
    }
    
    
    HorizonMS getHorizonMS(){
        
        GMasterSlave gHorizonMS = getGObject(G_HORIZON_MS);
        if ( null != gHorizonMS ){
            return (HorizonMS)(gHorizonMS.getUserData());
        }
        
        return null;
    }

    @Override
    public boolean mouseEvent(GScene gscene, GMouseEvent event) {
        if (gscene != this.gscene) {
            return false;
        }

        switch (event.type) {
            case GMouseEvent.BUTTON_DOWN:
                handleMousePress(event, gscene);
                break;
            case GMouseEvent.BUTTON_UP:
                for( GObject gobject : gObjects.values() ){
                    gscene.remove(gobject);
                }
                gscene.refresh();

                break;
        }

        return true;
    }

    protected void handleMousePress(GMouseEvent event, GScene gscene1) {
        Patch patch = getSelectedPatch(event.x, event.y);
        if (patch != null) {
            scene = createScene(patch);
            MasterSlave faultMS = selectFault(event.x, event.y);
            MasterSlave horizonMS = selectHorizon(event.x, event.y);
            if ((null != faultMS) && (null != horizonMS)) {
                createIntersection(faultMS, horizonMS);
            }
            gscene1.refresh();
        }
    }

    protected MasterSlave selectFault(int x, int y) {

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

        GPolyline gFault = new GPolyline(builder.getMergedLine());
        
        gObjects.put(G_FAULT, gFault);

        gscene.add(gFault);

        GStyle style = new GStyle();
        style.setForegroundColor(GColor.red);
        style.setLineWidth(2);
        gFault.setStyle(style);
        gFault.redraw();

        return builder.getMasterSlave();
    }

    protected MasterSlave selectHorizon(int x, int y) {

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

        GMasterSlave gHorizonMS = new GMasterSlave(builder.getMasterSlave(), false);
        
        gObjects.put(G_HORIZON_MS, gHorizonMS);
        gscene.add(gHorizonMS);

        gHorizonMS.redraw();

        return builder.getMasterSlave();

    }

    protected void createIntersection(MasterSlave fault, MasterSlave horizon) {

        FaultMS faultMS = (FaultMS) fault;

        HorizonFaultIntersection intersection = new HorizonFaultIntersection(horizon, fault);
        intersection.compute();

        GPoints gPoints = new GPoints();
        gObjects.put(G_POINTS, gPoints);

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

            if (i % 1 == 0) {

                double[] pos = new double[2];
                lp.getPosition(pos);

                //gPoints.addPoint(pos, GColor.CYAN);
                ICurviPoint curviCur = faultMS.getSupportPoint(pos);

                double length = faultGeometry.signedLength(iSCurvi, curviCur);

                ICurviPoint curviTarget = faultGeometry.getPointAtLength(iMCurvi, length);

                Point2D target = faultSupport.getPosition(curviTarget);
                //gPoints.addPoint(target.getPosition(), GColor.YELLOW);

                LinePoint lpOnMaster = faultMS.getMasterPoint(target.getPosition());
                if (lpOnMaster != null) {
                    gPoints.addPoint(lpOnMaster.getPosition(), GColor.YELLOW);
                }
            }
            i++;

        }

        gscene.add(gPoints);;

        gPoints.redraw();

    }

}
