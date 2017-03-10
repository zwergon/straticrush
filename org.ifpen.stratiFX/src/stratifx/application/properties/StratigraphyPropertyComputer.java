/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratifx.application.properties;

import fr.ifp.kronosflow.model.property.Properties;
import fr.ifp.jdeform.controllers.scene.Scene;
import fr.ifp.jdeform.controllers.scene.SceneBuilder;
import fr.ifp.kronosflow.controllers.property.PropertyComputer;
import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.geometry.RectD;
import fr.ifp.kronosflow.geometry.Vector2D;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.geoscheduler.property.GeoschedulerPropertyComputer;
import fr.ifp.kronosflow.model.CompositePatch;
import fr.ifp.kronosflow.model.FeatureGeolInterval;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.geology.StratigraphicEvent;
import fr.ifp.kronosflow.model.sampling.CompactPointSampling;
import fr.ifp.kronosflow.model.sampling.PointSampling;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.IPolyline;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.polyline.PolyLineGeometry;
import fr.ifp.kronosflow.polyline.PolyLineTangent;
import fr.ifp.kronosflow.property.IPropertyAccessor;
import fr.ifp.kronosflow.property.Property;
import fr.ifp.kronosflow.property.PropertyInfo;
import fr.ifp.kronosflow.property.PropertyLocation;
import fr.ifp.kronosflow.utils.LOGGER;
import fr.ifp.kronosflow.warp.Displacement;
import fr.ifp.kronosflow.warp.RBFWarp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import stratifx.application.IUIController;
import stratifx.application.StratiFXService;
import stratifx.application.plot.GFXScene;
import stratifx.application.plot.PlotController;
import stratifx.application.views.GVectorField;

/**
 *
 * @author lecomtje
 */
public class StratigraphyPropertyComputer extends GeoschedulerPropertyComputer {

    GVectorField gField;

    protected StratigraphyPropertyComputer(GeoschedulerSection section) {
        super(section);
    }

    static public class Builder implements PropertyComputer.Builder {

        @Override
        public PropertyComputer create(Section section) {

            if (section instanceof GeoschedulerSection) {
                return new StratigraphyPropertyComputer((GeoschedulerSection) section);
            }

            LOGGER.error("need a GeoschedulerSection to create this PropertyComputer", getClass());

            return null;
        }
    }

   
    protected Property compute( Collection<Patch> unused ) {
        
        
        GFXScene gfxScene = createGField();
        
        
        Property stratiProp = findOrCreateProperty( Properties.STRATIGRAPHY.info );

        
        Section section = getSection();
        PatchLibrary library = section.getPatchLibrary();

        Patch patch = library.getPatches().get(0);
        Scene scene = SceneBuilder.createDefaultScene(patch);


        for (Patch selected : scene.getElements()) {
            if (selected instanceof CompositePatch) {
                computeFromPatch((CompositePatch) selected, stratiProp.getAccessor());
            }
        }

        gField.redraw();
        gfxScene.refresh();

        return stratiProp;
    }

    private GFXScene createGField() {
        gField = new GVectorField();
        PlotController plot = (PlotController) StratiFXService.instance.getController(IUIController.Type.PLOT);
        GFXScene gfxScene = plot.getGFXScene();
        gfxScene.add(gField);
        return gfxScene;
    }

    private void computeFromPatch(CompositePatch composite, IPropertyAccessor accessor) {

        PolyLineGeometry geometry = new PolyLineGeometry(composite.getBorder());
        double l = geometry.length() / 30;

        Collection<FeatureGeolInterval> intervals = getHorizons(composite);

        Collection<Displacement> displacements = new ArrayList<>();

        for (FeatureGeolInterval fInterval : intervals) {
            IPolyline line = fInterval.getInterval();

            PolyLineTangent tangent = new PolyLineTangent(line);
            tangent.compute();

            for (ICurviPoint cp : line.getPoints()) {
                double[] pos = new double[2];
                line.getPosition(cp, pos);

                if (alreadySeen(displacements, pos)) {
                    continue;
                }

                double[] v = tangent.getTangent(cp);
                Vector2D.normalize(v);

                if (v[0] < 0) {
                    v[0] *= -l;
                    v[1] *= -l;
                } else {
                    v[0] *= l;
                    v[1] *= l;
                }

                displacements.add(new Displacement(pos, Vector2D.add(pos, v)));

            }
        }

        //computes approximation of strati angle over the whole composite patch.
        RBFWarp warp = new RBFWarp();
        warp.setDisplacements(displacements);

        //sample this approximation for all inner Patches.
        for (Patch innerPatch : composite.getPatchs()) {

            PolyLine border = innerPatch.getBorder();
            PolyLineGeometry innerGeometry = new PolyLineGeometry(border);
            RectD bbox = innerGeometry.computeBoundingBox();
            PointSampling sampling = new CompactPointSampling(bbox);
            sampling.sample(1000);

            double[] dst = new double[2];
            for (Point2D pt : sampling.getPoints()) {
                double[] pos = pt.getPosition();

                if (innerGeometry.isPointInside(pos)) {
                    warp.getDeformed(pos, dst);
                    double[] v = Vector2D.substract(dst, pos);

                    PropertyLocation location = new PropertyLocation(innerPatch, pos);

                    double[] polar = Vector2D.toPolar(v);

                    accessor.setValue(location, polar[1]);

                    gField.addVector(pos, v);
                }
            }
        }
    }

    private boolean alreadySeen(Collection<Displacement> displacements, double[] pos) {
        if (displacements.stream().anyMatch((d) -> (Vector2D.length2(d.getStart(), pos) < Vector2D.EPSILON))) {
            return true;
        }
        return false;
    }

   
    /**
     *
     * @return all {@link FeatureGeolInterval} that are linked to a
     * {@link StratigraphicEvent}
     */
    Collection<FeatureGeolInterval> getHorizons(CompositePatch patch) {

        Collection<FeatureGeolInterval> intervals = new HashSet<>();

        for (Patch innerPatch : patch.getPatchs()) {

            Collection<FeatureGeolInterval> fIntervals = innerPatch.findObjects(FeatureGeolInterval.class);
            for (FeatureGeolInterval fInterval : fIntervals) {
                if (fInterval.getInterval().getFeature() instanceof StratigraphicEvent) {
                    intervals.add(fInterval);
                }
            }

        }

        return intervals;
    }

}
