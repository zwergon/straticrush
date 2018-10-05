/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.interaction.tools;

import fr.ifp.kronosflow.geoscheduler.GeoschedulerLink;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerLinkType;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.mesh.builder.TrglMeshBuilder;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.geology.*;
import fr.ifp.kronosflow.model.time.NeutralFiberTimeProvider;
import fr.ifp.kronosflow.kernel.polyline.PolyLine;
import fr.ifp.kronosflow.utils.LOGGER;
import stratifx.application.caller.TimePatchCaller;
import stratifx.application.interaction.AbstractValueExtractor;
import stratifx.application.interaction.NodesValueExtractor;
import stratifx.application.interaction.SectionInteraction;
import fr.ifp.kronosflow.model.time.ITimeProvider;
import stratifx.application.main.StratiFXService;
import stratifx.application.views.GMesh;
import stratifx.application.views.GPolyline;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.interaction.GMouseEvent;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lecomtje
 */
public class TimeLineInteraction extends SectionInteraction {


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

    protected TimePatchCaller createCaller() {
        TimePatchCaller caller = new TimePatchCaller(StratiFXService.instance);

        link = new GeoschedulerLink(GeoschedulerLinkType.SPLIT_PATCH, caller);

        return caller;
    }


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

                if (link != null) {
                    getScheduler().addCurrent(link);
                    link = null;
                }
                gscene.refresh();

                break;
        }

        return true;
    }

    private void handleMousePress(GMouseEvent event) {

        if (event.button != GMouseEvent.BUTTON_1) {
            return;
        }

        Patch patch = getSelectedPatch(event.x, event.y);

        if ( null == patch ){
            return;
        }

        Map<Patch, Mesh2D> meshes = retrieveMeshes(patch);

        ITimeProvider provider = new NeutralFiberTimeProvider(meshes.keySet());

        provider.setPatch(patch);
        double[] xy = gscene.getTransformer().deviceToWorld(event.x, event.y);
        double timeValue = provider.getTime(xy);

        LOGGER.debug("time picked: " + timeValue, getClass());

        TimePatchCaller caller = createCaller();
        caller.setTime(timeValue);

        StratigraphicUnit unit = patch.getGeologicFeaturesByClass(StratigraphicUnit.class);

        int iObjects = 0;
        for (Map.Entry<Patch, Mesh2D> entry : meshes.entrySet()) {
            Mesh2D mesh = entry.getValue();
            Patch p = entry.getKey();

            GMesh gmesh = new GMesh(mesh);
            gscene.add(gmesh);
            gObjects.put(iObjects++, gmesh);
            gmesh.redraw();

            provider.setPatch(p);
            AbstractValueExtractor tle = new NodesValueExtractor(mesh, timeValue, provider);
            tle.buildLines();

            Collection<PolyLine> lines = tle.getPolyLine();
            if ( lines.size() == 1 ){
                caller.addPatchToSplit(p, lines.iterator().next());
            }

            int i = 0;
            for (PolyLine line : lines) {

                GPolyline gline = new GPolyline(line);
                GStyle style = new GStyle();
                style.setForegroundColor(colors[i % colors.length]);
                style.setLineWidth(2);
                gline.setStyle(style);

                gscene.add(gline);
                gObjects.put(iObjects++, gline);

                gline.redraw();
                i++;
            }


        }



    }






    private Map<Patch, Mesh2D>  retrieveMeshes(Patch patch) {

        Map<Patch, Mesh2D> meshes = new HashMap<>();

        BodyFeature feature = patch.getBodyFeature();
        for( Patch p : patch.getPatchLibrary().getPatches()) {
            if ( feature.equals(p.getBodyFeature())){
                TrglMeshBuilder meshBuilder = new TrglMeshBuilder();
                meshBuilder.setBeautify(false);
                Mesh2D mesh = meshBuilder.createMesh( p.getBorder().getPoints2D() );
                meshes.put(p, mesh);
            }
        }

        /*TrglMeshBuilder meshBuilder = new TrglMeshBuilder();
        meshBuilder.setBeautify(false);
        Mesh2D mesh = meshBuilder.createMesh( patch.getBorder().getPoints2D() );
        meshes.put(patch, mesh);*/

        return meshes;
    }

}
