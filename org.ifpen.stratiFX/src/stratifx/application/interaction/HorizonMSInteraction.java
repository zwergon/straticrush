/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.interaction;

import fr.ifp.kronosflow.deform.deformation.constraint.ExactTargetsComputer;
import fr.ifp.kronosflow.deform.deformation.items.PatchIntersectionItem;
import fr.ifp.kronosflow.deform.scene.HorizonMS;
import fr.ifp.kronosflow.kernel.geometry.Point2D;
import fr.ifp.kronosflow.model.FeatureInterval;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.geology.Paleobathymetry;
import fr.ifp.kronosflow.kernel.polyline.ICurviPoint;
import fr.ifp.kronosflow.kernel.polyline.LinePoint;
import fr.ifp.kronosflow.kernel.polyline.LinePointPair;
import fr.ifp.kronosflow.kernel.polyline.PolyLine;
import fr.ifp.kronosflow.kernel.polyline.PolyLineGeometry;
import fr.ifp.kronosflow.utils.LOGGER;
import fr.ifp.kronosflow.kernel.warp.Displacement;
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

        List<PatchInterval> slaves = horizon.getSlaves();

        if (slaves.isEmpty()) {
            LOGGER.warning("no slaves in this horizon", getClass());
            return;
        }
        
        PolyLineGeometry geometry = new PolyLineGeometry( getTargetLine() );
        
        double[] targetPos = getFirstTarget(slaves);

        allDisplacement.clear();
        for (PatchInterval interval : slaves) {
            
            LinePointPair pointPair = getNextPair( interval, targetPos, geometry );

            PatchIntersectionItem pi = new PatchIntersectionItem(interval, pointPair);
            ExactTargetsComputer targets = new ExactTargetsComputer();
            targets.initialize(pi);
            targets.compute(scene);
            
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
        
        return (PolyLine)bathy.getPolyline();
    }

    
    //first extremity of slave
    private double[] getFirstTarget( List<PatchInterval> slaves ){
        
        
        PatchInterval firstInterval = slaves.get(0);
        FeatureInterval fInterval = firstInterval.getInterval();
        
        double[] start = new double[2];
        fInterval.getPosition(fInterval.first(), start);

        double[] end = new double[2];
        fInterval.getPosition(fInterval.last(), end);

        if ( start[0] > end[0 ]) {
            return end;
        }
        else {
            return start;
        }


    }
    
    private LinePointPair getNextPair( PatchInterval interval, double[] target, PolyLineGeometry geometry ){
       
        FeatureInterval fInterval = interval.getInterval();
        LinePoint lpStart = new LinePoint(fInterval, fInterval.first());

        Point2D start = lpStart.getPosition();

        LinePoint lpEnd = new LinePoint(fInterval, fInterval.last());
        Point2D end = lpEnd.getPosition();

        LinePoint lp = lpStart;
        if ( start.x() > end.x() ){
            lp = lpEnd;
        }

        //where on the bathy
        ICurviPoint cpProj = geometry.projectVertically( new Point2D(target), null);
        LinePoint lp2 = new LinePoint(geometry.getLine(), cpProj);

        return new LinePointPair(lp2, lp);
        
    }
}
