/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 *
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.interaction.tools;

import fr.ifp.kronosflow.deform.scene.Scene;
import fr.ifp.kronosflow.kernel.geometry.Point2D;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.IPatchMeshBuilder;
import fr.ifp.kronosflow.model.Patch;
import stratifx.application.interaction.SectionInteraction;
import stratifx.application.views.GMesh;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.interaction.GMouseEvent;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author lecomtje
 */
public abstract class AMesh2DInteraction extends SectionInteraction {

    final static int G_MESH = 0;

    static GColor[] colors = new GColor[]{
            GColor.CYAN,
            GColor.GREEN,
            GColor.MAGENTA,
            GColor.ORANGE,
            GColor.PINK,
            GColor.RED,
            GColor.WHITE,
            GColor.YELLOW,
            GColor.BLUE
    };

    HashMap<Integer, GObject> gObjects = new HashMap<>();

    Scene scene;

    protected AMesh2DInteraction(GScene scene) {
        super(scene);
    }

    @Override
    public boolean mouseEvent(GScene scene, GMouseEvent event) {
        if (scene != this.gscene) {
            return false;
        }

        switch (event.type) {
            case GMouseEvent.BUTTON_DOWN:
                handleMousePress(event);
                gscene.refresh();
                break;
            case GMouseEvent.BUTTON_UP:
                for (GObject gobject : gObjects.values()) {
                    gscene.remove(gobject);
                }
                gscene.refresh();

                break;
        }

        return true;
    }

    private void handleMousePress(GMouseEvent event) {
        if (event.button != GMouseEvent.BUTTON_1) {
            return;
        }

        Patch patch = getSelectedPatch(event.x, event.y);

        if (patch != null) {
            double[] xy = gscene.getTransformer().deviceToWorld(event.x, event.y);

            scene = createScene(patch);

            Patch selected = scene.getSelected();
            List<Point2D> pts = selected.getBorder().getPoints2D();

            IPatchMeshBuilder builder = createBuilder();
            builder.initialize(selected, pts);
            Mesh2D mesh = builder.createMesh(selected.getBorder().getPoints2D());

            GMesh gmesh = new GMesh(mesh);
            gscene.add(gmesh);
            gObjects.put(G_MESH, gmesh);
            gmesh.redraw();
        }
    }

    public abstract IPatchMeshBuilder createBuilder();

}
