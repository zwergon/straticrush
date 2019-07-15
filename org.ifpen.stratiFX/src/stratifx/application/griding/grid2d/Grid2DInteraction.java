/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.griding.grid2d;

import fr.ifp.kronosflow.model.IPatchMeshBuilder;
import fr.ifp.kronosflow.model.builder.Grid2DPatchBuilder;
import stratifx.application.interaction.tools.AMesh2DInteraction;
import stratifx.canvas.graphics.GScene;

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
