/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratifx.application.views;

import java.util.ArrayList;
import java.util.Collection;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GSegment;

/**
 *
 * @author lecomtje
 */
public class GVectorField extends GObject {

    Collection<Vector> vectors;
    
    double ratio = 100;
    
    public GVectorField(){
        vectors = new ArrayList<>();
    }
    
    public void addVector( double[] pos, double[] dir ){
        
        //LOGGER.debug("add vector" + pos[0] + "," + pos[1] + "," + dir[0] + "," + dir[1], getClass());
        Vector v = new Vector(pos, dir);
        vectors.add( v ); 
    }
    
    
    private class Vector {
        GSegment segment;
        double[] pos;
        double[] dir;
        
        Vector( double[] pos, double[] dir ){
            this.pos = pos;
            this.dir = dir;
            segment = new GSegment();
            addSegment( segment );
        }
    }

    @Override
    protected void draw() {

        for( Vector v : vectors ){
            GSegment segment = v.segment;
            segment.setWorldGeometry(v.pos[0], v.pos[1], v.pos[0]+ratio*v.dir[0], v.pos[1]+ratio*v.dir[1]);
        }
    }
    
    
    
    
    
    
}
