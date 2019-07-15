/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.griding.compact2d;

import fr.ifp.kronosflow.model.IPatchMeshBuilder;
import fr.ifp.kronosflow.model.builder.Compact2DPatchBuilder;

import stratifx.application.interaction.tools.AMesh2DInteraction;
import stratifx.canvas.graphics.GScene;

/**
 *
 * @author lecomtje
 */
public class Compact2DInteraction extends AMesh2DInteraction {

    public Compact2DInteraction(GScene scene) {
        super(scene);
    }

    @Override
    public IPatchMeshBuilder createBuilder() {
        return new Compact2DPatchBuilder();
    }
}
