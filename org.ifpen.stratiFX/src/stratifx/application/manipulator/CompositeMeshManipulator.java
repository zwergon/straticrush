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
import fr.ifp.jdeform.scene.MasterSlave;
import fr.ifp.jdeform.scene.Scene;
import fr.ifp.jdeform.scene.algo.HorizonExtractor;
import fr.ifp.jdeform.scene.algo.MasterSlaveExtractor;
import fr.ifp.jdeform.scene.algo.TargetsExtractor;
import fr.ifp.kronosflow.mesh.CompositeMesh2D;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.geology.BoundaryFeature;
import fr.ifp.kronosflow.utils.LOGGER;
import java.util.HashMap;
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

    CompositeMesh2D mesh;

    final static int G_MESH = 0;
    final static int G_HORIZON_MS = 1;

    HashMap<Integer, GObject> gObjects = new HashMap<>();

    protected <T> T getGObject(int type) {
        return (T) gObjects.get(type);
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
        mesh = new CompositeMesh2D();
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
    }

    @Override
    public void onMousePress(GMouseEvent event) {

        MasterSlave horizonMS = selectHorizon(event.x, event.y);
        if (null != horizonMS) {

            GMasterSlave gHorizonMS = new GMasterSlave(horizonMS, false);
            gObjects.put(G_HORIZON_MS, gHorizonMS);
            gscene.add(gHorizonMS);

            gHorizonMS.redraw();
        }
    }

    @Override
    public void onMouseRelease(GMouseEvent event) {
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

}
