/*
 * Copyright (C) 2014-2018 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.manipulator;

import fr.ifp.kronosflow.dem.deformation.MaterialItem;
import fr.ifp.kronosflow.dem.model.Material;
import fr.ifp.kronosflow.deform.controllers.callers.DeformationControllerCaller;
import fr.ifp.kronosflow.deform.deformation.IDeformationItem;
import fr.ifp.kronosflow.deform.scene.Scene;
import java.util.ArrayList;
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
        
        /*Deformation deformation = (Deformation) deformationCaller.getDeformation();
        gMaterial.setDeformation(deformation);*/
        
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

    @Override
    public boolean canDeform() {
        items = new ArrayList<IDeformationItem>();
        
        MaterialItem item = new MaterialItem(material);
        
        Scene scene = deformationCaller.getScene();
        item.addDeformed(scene.getSelected());
        items.add(item);
        
        return super.canDeform();
    }

    @Override
    public void updateGraphics() {
        gMaterial.redraw();
        super.updateGraphics(); 
        
    }
    
    
    
    
    
    
}
