/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.interaction.deform;

import fr.ifp.kronosflow.deform.controllers.callers.DeformationControllerCaller;
import stratifx.application.manipulator.CompositeManipulator;
import stratifx.application.manipulator.CompositeMeshManipulator;
import stratifx.canvas.graphics.GScene;

/**
 *
 * @author lecomtje
 */
public class GlobalMoveInteraction extends DeformationInteraction {

    public GlobalMoveInteraction(GScene scene) {
        super(scene);
    }

    @Override
    public CompositeManipulator createManipulator(GScene gscene, DeformationControllerCaller caller) {
        return new CompositeMeshManipulator(gscene, caller);
    }
    
}
