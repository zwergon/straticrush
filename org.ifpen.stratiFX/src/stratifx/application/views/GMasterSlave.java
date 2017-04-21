/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.views;

import fr.ifp.jdeform.scene.MasterSlave;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import java.util.List;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GStyle;

/**
 *
 * @author lecomtje
 */
public class GMasterSlave extends GObject {

    static GColor[] colors = new GColor[]{
        GColor.CYAN,
        GColor.GREEN,
        GColor.MAGENTA,
        GColor.ORANGE,
        GColor.PINK,
        GColor.RED,
        GColor.WHITE,
        GColor.YELLOW,
        GColor.BLUE
    };

    public GMasterSlave(MasterSlave masterSlave, boolean drawMaster) {

        setName(masterSlave.getFeature().getName());
        int i = 0;

        /*GPoints points = new GPoints();
        add(points);*/

        List<PatchInterval> patches = (masterSlave.getMaster().isEmpty()) ? masterSlave.getSlave() : masterSlave.getMaster();

        for (PatchInterval pInterval : patches) {

            GPolyline line = new GPolyline(pInterval.getInterval());
            
            GColor color = colors[i % colors.length];

            GStyle style = new GStyle();
            style.setForegroundColor( color );
            style.setLineWidth(2);
            line.setStyle(style);

            /*ICurviPoint cp = pInterval.getInterval().getS1();
            double[] pt = new double[2];
            pInterval.getInterval().getPosition(cp, pt);
            points.addPoint(pt, color );*/

            add(line);

            i++;
        }

        //points.toFront();

    }

    public GMasterSlave(MasterSlave masterSlave) {
        this(masterSlave, true);
    }

}
