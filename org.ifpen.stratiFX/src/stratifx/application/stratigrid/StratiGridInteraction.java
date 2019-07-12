/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.stratigrid;

import fr.ifp.kronosflow.deform.stratigraphy.StratiGridPatchBuilder;
import fr.ifp.kronosflow.model.IPatchMeshBuilder;
import stratifx.application.interaction.tools.AMesh2DInteraction;
import stratifx.canvas.graphics.GScene;

/**
 *
 * @author lecomtje
 */
public class StratiGridInteraction extends AMesh2DInteraction {

    public StratiGridInteraction(GScene scene) {
        super(scene);
    }

    @Override
    public IPatchMeshBuilder createBuilder() {
        return new StratiGridPatchBuilder();
    }
}
