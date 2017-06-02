/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.interaction;

import fr.ifp.jdeform.deformation.constraint.ExactTargetsComputer;
import fr.ifp.jdeform.scene.HorizonMS;
import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.model.FeatureInterval;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.geology.Paleobathymetry;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.LinePoint;
import fr.ifp.kronosflow.polyline.LinePointPair;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.polyline.PolyLineGeometry;
import fr.ifp.kronosflow.utils.LOGGER;
import fr.ifp.kronosflow.warp.Displacement;
import java.util.ArrayList;
import java.util.List;
import stratifx.application.views.GDisplacement;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.interaction.GMouseEvent;

/**
 *
 * @author lecomtje
 */
public class HorizonMSInteraction extends MasterSlaveInteraction {
    
    final static int G_DISPLACEMENTS = 4;
    
    List<Displacement> allDisplacement =  new ArrayList<>();

    public HorizonMSInteraction(GScene gfxScene) {
        super(gfxScene);
    }

    protected void handleMousePress(GMouseEvent event, GScene gscene) {
        Patch patch = getSelectedPatch(event.x, event.y);
        if (patch != null) {
            scene = createScene(patch);
            selectHorizon(event.x, event.y);

            createTargetLine();
            gscene.refresh();
        }
    }

    private void createTargetLine() {
        HorizonMS horizon = getHorizonMS();
        if (null == horizon) {
            LOGGER.warning("unable to find horizon", getClass());
            return;
        }

        horizon.sort(HorizonMS.Order.XOrder);

        List<PatchInterval> slaves = horizon.getSlave();

        if (slaves.isEmpty()) {
            LOGGER.warning("no slaves in this horizon", getClass());
            return;
        }
        
        PolyLineGeometry geometry = new PolyLineGeometry( getTargetLine() );
        
        double[] targetPos = getFirstTarget(slaves);

        allDisplacement.clear();
        for (PatchInterval interval : slaves) {
            
            LinePointPair pointPair = getNextPair( interval, targetPos, geometry );

            ExactTargetsComputer targets = new ExactTargetsComputer(interval, pointPair);
            targets.compute();
            
            List<Displacement> displacements = targets.getDisplacements();
            
            allDisplacement.addAll(displacements);
              
            targetPos = displacements.get( displacements.size() -1 ).getTarget();

        }
        
        
        GDisplacement gDisplacement = new GDisplacement(allDisplacement);
        gscene.add(gDisplacement);
        
        gDisplacement.redraw();
        
        gObjects.put(G_DISPLACEMENTS, gDisplacement);

    }
    
    private PolyLine getTargetLine( ){
        Section section = getSection();
        Paleobathymetry bathy = section.getPatchLibrary().getPaleobathymetry();
        
        return bathy.getPolyline();
    }

    
    //first extremity of slave
    private double[] getFirstTarget( List<PatchInterval> slaves ){
        
        
        PatchInterval firstInterval = slaves.get(0);
        FeatureInterval fInterval = firstInterval.getInterval();
        
        double[] pos = new double[2];
        fInterval.getPosition(fInterval.first(), pos);
        
        return pos;
    }
    
    private LinePointPair getNextPair( PatchInterval interval, double[] target, PolyLineGeometry geometry ){
       
        FeatureInterval fInterval = interval.getInterval();
        LinePoint lp = new LinePoint(fInterval, fInterval.first());

        //where on the bathy
        ICurviPoint cpProj = geometry.projectVertically( new Point2D(target), null);
        LinePoint lp2 = new LinePoint(geometry.getLine(), cpProj);

        return new LinePointPair(lp2, lp);
        
    }
}
