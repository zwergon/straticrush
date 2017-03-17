/* 
 * Copyright 2017 lecomtje.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
