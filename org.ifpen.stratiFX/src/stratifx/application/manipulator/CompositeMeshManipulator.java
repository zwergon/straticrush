/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.manipulator;

import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.deformation.IDeformationItem;
import fr.ifp.jdeform.deformation.items.HorizonFaultMSGlidingItem;
import fr.ifp.jdeform.deformation.items.HorizonMS2LineItem;
import fr.ifp.jdeform.scene.FaultMS;
import fr.ifp.jdeform.scene.MasterSlave;
import fr.ifp.jdeform.scene.Scene;
import fr.ifp.jdeform.scene.algo.FaultExtractor;
import fr.ifp.jdeform.scene.algo.HorizonExtractor;
import fr.ifp.jdeform.scene.algo.MasterSlaveExtractor;
import fr.ifp.jdeform.scene.algo.TargetsExtractor;
import fr.ifp.kronosflow.mesh.CompositeMesh2D;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.geology.BoundaryFeature;
import fr.ifp.kronosflow.model.geology.FaultFeature;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.utils.LOGGER;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import stratifx.application.views.GMasterSlave;
import stratifx.application.views.GMesh;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.interaction.GMouseEvent;

/**
 *
 * @author lecomtje
 */
public class CompositeMeshManipulator extends CompositeManipulator {

    final static int G_MESH = 0;
    final static int G_HORIZON_MS = 1;
    final static int G_FAULT_MS = 2;

    HashMap<Integer, GObject> gObjects = new HashMap<>();

    List<FaultMS> faultsMS = new ArrayList<>();

    private <T> T getObject(int type) {
        GObject gObject = gObjects.get(type);
        if (null != gObject) {
            return (T) gObject.getUserData();
        }
        return null;
    }

    private <T> T getGObject(int type) {
        return (T) gObjects.get(type);
    }

    private PolyLine getTargetLine() {
        Scene scene = deformationCaller.getScene();
        Section section = scene.getSection();

        assert section != null : "Section is null";

        return section.getPatchLibrary().getPaleobathymetry().getPolyline();
    }

    public CompositeMeshManipulator(
            GScene gscene,
            DeformationControllerCaller caller
    ) {
        super(gscene, caller);
    }

    @Override
    public boolean isActive() {
        return !gObjects.isEmpty();
    }

    @Override
    public void activate() {

        Scene scene = deformationCaller.getScene();
        /* create a composite mesh with all patches*/
        CompositeMesh2D mesh = new CompositeMesh2D();
        for (Patch patch : scene.getElements()) {
            if (patch instanceof IMeshProvider) {
                IMeshProvider mPatch = (IMeshProvider) patch;
                mesh.addMesh(mPatch.getMesh());
            }
        }
        GMesh gMesh = new GMesh(mesh);
        gObjects.put(G_MESH, gMesh);
        gscene.add(gMesh);

        gMesh.redraw();
    }

    @Override
    public void deactivate() {
        for (GObject object : gObjects.values()) {
            gscene.remove(object);
        }
        gObjects.clear();
    }

    @Override
    public void updateGraphics() {
        for (GObject object : gObjects.values()) {
            object.redraw();
        }
    }

    @Override
    protected void computeTargets() {

        items = new ArrayList<IDeformationItem>();

        HorizonMS2LineItem hlItem = new HorizonMS2LineItem();
        hlItem.setHorizonMS(getObject(G_HORIZON_MS));
        hlItem.setTargetLine(getTargetLine());

        items.add(hlItem);

        for (FaultMS fms : faultsMS) {
            HorizonFaultMSGlidingItem hfgItem = new HorizonFaultMSGlidingItem();
            hfgItem.setFault(fms);
            hfgItem.setHorizon(getObject(G_HORIZON_MS));

            items.add(hfgItem);
        }

    }

    @Override
    public void onMousePress(GMouseEvent event) {

        MasterSlave horizonMS = selectHorizon(event.x, event.y);
        if (null != horizonMS) {

            GMasterSlave gHorizonMS = new GMasterSlave(horizonMS, false);
            gObjects.put(G_HORIZON_MS, gHorizonMS);
            gscene.add(gHorizonMS);

            gHorizonMS.redraw();

            faultsMS.clear();
            Section section = deformationCaller.getService().getSection();
            List<FaultFeature> faults = section.getFeatures().getGeologicFeaturesByClass(FaultFeature.class);
            for (FaultFeature feature : faults) {
                if ( feature.getName().equals("F37") || feature.getName().equals("F38") ) {

                    MasterSlave ms = selectFault(feature);
                    if (ms != null) {
                        faultsMS.add((FaultMS) ms);
                    }
                }
            }
        }

    }

    @Override
    public void onMouseRelease(GMouseEvent event) {
        computeTargets();
    }

    @Override
    public void onMouseMove(GMouseEvent event) {
    }

    protected MasterSlave selectHorizon(int x, int y) {

        Scene scene = deformationCaller.getScene();

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

        return builder.getMasterSlave();

    }

    protected MasterSlave selectFault(FaultFeature selectedFeature) {

        Scene scene = deformationCaller.getScene();

        MasterSlaveExtractor builder = new FaultExtractor(scene);
        builder.compute(selectedFeature);

        return builder.getMasterSlave();
    }

}
