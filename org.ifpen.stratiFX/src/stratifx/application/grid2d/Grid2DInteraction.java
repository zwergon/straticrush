/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.grid2d;

import fr.ifp.kronosflow.deform.scene.Scene;
import fr.ifp.kronosflow.kernel.geometry.Point2D;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.IPatchMeshBuilder;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.builder.Grid2DPatchBuilder;
import stratifx.application.interaction.SectionInteraction;
import stratifx.application.interaction.tools.AMesh2DInteraction;
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
public class Grid2DInteraction extends AMesh2DInteraction {


    public Grid2DInteraction(GScene scene) {
        super(scene);
    }

    @Override
    public IPatchMeshBuilder createBuilder() {
        return new Grid2DPatchBuilder();
    }
}
