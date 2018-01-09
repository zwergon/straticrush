/*
 * Copyright (C) 2014-2018 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.manipulator;

import fr.ifp.dem.Material;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import stratifx.application.views.GMaterial;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.interaction.GMouseEvent;

/**
 *
 * @author lecomtje
 */
public class DEMManipulator extends CompositeManipulator {
    
    GMaterial gMaterial;
    
    Material material;

    public DEMManipulator(GScene gscene, DeformationControllerCaller caller, Material material ) {
        super(gscene, caller);
        this.material = material;
    }
    
    
     @Override
    public void activate() {
        super.activate();
        gMaterial = new GMaterial(material);
        gscene.add(gMaterial);
        gMaterial.redraw();

    }

    @Override
    public void deactivate() {
        gscene.remove(gMaterial);
        gMaterial = null;
        super.deactivate();
    }

    @Override
    protected void computeTargets() {
    }

    @Override
    public void onMouseRelease(GMouseEvent event) {
    }
    
}
