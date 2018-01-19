/*
 * Copyright (C) 2014-2018 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.views;

import fr.ifp.dem.model.Interactable;
import fr.ifp.dem.model.InteractableGraph;
import fr.ifp.dem.model.Material;
import fr.ifp.dem.model.Particle;
import fr.ifp.kronosflow.utils.LOGGER;
import fr.ifp.kronosflow.warp.IWarp;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;

/**
 *
 * @author lecomtje
 */
public class GMaterial 
        extends
        GDeformableObject {

    public GMaterial(Material material) {
        super(material);

        InteractableGraph graph = material.getGraph();
        for ( Interactable interactable : graph.getInteractables() ) {
            if ( interactable instanceof Particle ){
                addParticle( (Particle)interactable);
            }
        }
    }


    @Override
    protected void warpedDraw(IWarp warp) {
        LOGGER.debug("warped draw", getClass());
        for (GSegment segment : getSegments()) {
            Object userData = segment.getUserData();
            if (userData instanceof Particle) {
                updateParticleGeometry(segment, warp);
            }
        }
    }

    @Override
    protected void directDraw() {
        
        LOGGER.debug("direct draw", getClass());
        for (GSegment segment : getSegments()) {
            Object userData = segment.getUserData();
            if (userData instanceof Particle) {
                updateParticleGeometry(segment);        
            }
        }
    }

    private GSegment addParticle( Particle particle ) {

        GSegment gcell = new GSegment();
        gcell.setUserData(particle);

        GStyle style = new GStyle();
        style.setForegroundColor(GColor.BLUE);
        style.setBackgroundColor(null);
        style.setFillPattern(GStyle.FILL_NONE);
        style.setLineWidth(1);
        gcell.setStyle(style);

        addSegment(gcell);

        return gcell;
    }

    private void updateParticleGeometry(GSegment gcell) {

        
        Particle particle = (Particle) gcell.getUserData();

        int npts = 20;
        double[] xpts = new double[npts];
        double[] ypts = new double[npts];
        
        double angle = 2*Math.PI / (npts-1);
        
        double[] pos = particle.getPosition();
        double radius = particle.getRadius();

        for (int i = 0; i < npts; i++) {
            
            
            xpts[i] = pos[0] + radius * Math.cos(angle*i);
            ypts[i] = pos[1] + radius * Math.sin(angle*i);
        }

        gcell.setWorldGeometry(xpts, ypts);
    }

    private void updateParticleGeometry(GSegment gcell, IWarp warp) {

      
        Particle particle = (Particle) gcell.getUserData();

        int npts = 20;
        double[] xpts = new double[npts];
        double[] ypts = new double[npts];
        
        double angle = 2*Math.PI / (npts-1);

        double[] pos = particle.getPosition();
        double radius = particle.getRadius();
        double[] dst = new double[2];
        
        for (int i = 0; i < npts; i++) {
            
            double[] xy = new double[]{
                pos[0] + radius * Math.cos(angle*i),
                pos[1] + radius * Math.sin(angle*i)
            };
            
            warp.getDeformed(xy, dst);
            xpts[i] = dst[0];
            ypts[i] = dst[1];

        }

        gcell.setWorldGeometry(xpts, ypts);

    }

}
