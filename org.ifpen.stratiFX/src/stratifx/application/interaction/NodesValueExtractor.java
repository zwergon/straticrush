/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.interaction;

import fr.ifp.kronosflow.geometry.Vector2D;
import fr.ifp.kronosflow.mesh.Cell;
import fr.ifp.kronosflow.mesh.Edge;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.uids.UID;
import stratifx.application.properties.TimeProvider;

/**
 *
 * @author lecomtje
 */
public class NodesValueExtractor extends AbstractValueExtractor {
    
    TimeProvider timeProvider;

    public NodesValueExtractor(Mesh2D mesh, double isoValue, TimeProvider provider ) {
        super(mesh, isoValue);
        this.timeProvider = provider;
    }

    @Override
    protected boolean findCellSegment(Cell cell, EdgePoint[] segLine) {
         int i = 0;
        for (Edge e : cell.getEdges()) {

            UID uid1 = e.first();

            double[] pt1 = geomProvider.getPosition(uid1);
            double t1 = timeProvider.getTime(pt1);

            UID uid2 = e.second();
            double[] pt2 = geomProvider.getPosition(uid2);
            double t2 = timeProvider.getTime(pt2);

            if ((t1 - isoValue) * (t2 - isoValue) <= 0) {

                double alpha = (isoValue - t1) / (t2 - t1);
                double[] pt = new double[2];
                Vector2D.interpolate(pt1, pt2, alpha, pt);

                segLine[i] = new EdgePoint(cell, pt);
                i++;
            }

            if (i == 2) {
                return true;
            }
        }

        return false;
    }
    
}
