/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.interaction.tools;

import fr.ifp.jdeform.scene.Scene;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.utils.LOGGER;
import java.util.HashMap;

import stratifx.application.interaction.AbstractValueExtractor;
import stratifx.application.interaction.NodesValueExtractor;
import stratifx.application.interaction.SectionInteraction;
import stratifx.application.properties.TimeProvider;
import stratifx.application.views.GMesh;
import stratifx.application.views.GPolyline;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.interaction.GMouseEvent;

/**
 *
 * @author lecomtje
 */
public class TimeLineInteraction extends SectionInteraction {

    final static int G_MESH = 0;
    final static int G_TIMELINE = 1;
    
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

    HashMap<Integer, GObject> gObjects = new HashMap<>();

    Scene scene;

    public TimeLineInteraction(GScene scene) {
        super(scene);
    }

    @Override
    public boolean mouseEvent(GScene scene, GMouseEvent event) {
        if (scene != this.gscene) {
            return false;
        }

        switch (event.type) {
            case GMouseEvent.BUTTON_DOWN:
                handleMousePress(event);
                gscene.refresh();
                break;
            case GMouseEvent.BUTTON_UP:
                for (GObject gobject : gObjects.values()) {
                    gscene.remove(gobject);
                }
                gscene.refresh();

                break;
        }

        return true;
    }

    private void handleMousePress(GMouseEvent event) {
        
        if (event.button != GMouseEvent.BUTTON_1 ){
            return;
        }
        
        Patch patch = getSelectedPatch(event.x, event.y);

        if (patch != null) {
            double[] xy = gscene.getTransformer().deviceToWorld(event.x, event.y);

            scene = createScene(patch);

      
            Patch p = scene.getSelected();
            assert p instanceof IMeshProvider : "patch need to be a IMeshProvider";

            Mesh2D pMesh = ((IMeshProvider) p).getMesh();

            GMesh gmesh = new GMesh(pMesh);
            gscene.add(gmesh);
            gObjects.put(G_MESH, gmesh);
            gmesh.redraw();

    
            TimeProvider provider = new TimeProvider(p);
            
            double timeValue = provider.getTime(xy);
            
            LOGGER.debug("time picked: " + timeValue, getClass());

            AbstractValueExtractor tle = new NodesValueExtractor(pMesh, timeValue, provider);
            tle.buildLines();

            int i = 0;
            for (PolyLine line : tle.getPolyLine()) {

                GPolyline gline = new GPolyline(line);
                GStyle style = new GStyle();
                style.setForegroundColor( colors[i % colors.length] );
                style.setLineWidth(2);
                gline.setStyle(style);

                gscene.add(gline);
                gObjects.put(G_TIMELINE + i, gline);

                gline.redraw();
                i++;
            }
        }

    }

}
