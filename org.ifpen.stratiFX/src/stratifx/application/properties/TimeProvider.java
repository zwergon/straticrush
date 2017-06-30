/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.properties;

import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.geometry.Vector2D;
import fr.ifp.kronosflow.model.FeatureGeolInterval;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.explicit.InfinitePolyline;
import fr.ifp.kronosflow.model.geology.BoundaryFeature;
import fr.ifp.kronosflow.model.geology.StratigraphicColumn;
import fr.ifp.kronosflow.model.geology.StratigraphicEvent;
import fr.ifp.kronosflow.polyline.PolyLineGeometry;
import fr.ifp.kronosflow.polyline.PolylineDirection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lecomtje
 */
public class TimeProvider {

    Map<StratigraphicEvent, Double> timeMap;

    Patch patch;

    ArrayList<DistanceLine> distanceLines;

    public TimeProvider(Patch patch) {
        this.patch = patch;

        createTimes();
        createDistanceLines();
    }

    public Section getSection() {
        return patch.getPatchLibrary().getSection();
    }

    public double getTime(double[] pos) {

        Point2D src = new Point2D(pos);

        for (DistanceLine distanceLine : distanceLines) {
            distanceLine.compute(src);

            //we are on a time line, return time of the line.
            if (distanceLine.dist < 1e-6) {
                return distanceLine.time;
            }
        }

        Collections.sort(distanceLines, new DistanceLineComparator());

        DistanceLine dl1 = distanceLines.get(0);

        DistanceLine dl2 = null;

        for (DistanceLine distanceLine : distanceLines) {
            if ((dl1.feature != distanceLine.feature) && (dl1.up != distanceLine.up)) {
                dl2 = distanceLine;
                break;
            }
        }

        if (dl2 == null) {
            for (DistanceLine distanceLine : distanceLines) {
                if (dl1.feature != distanceLine.feature) {
                    dl2 = distanceLine;
                    break;
                }
            }
        }

        double[] v = Vector2D.createVector(dl1.getProj(), src.getPosition());

        double[] meanDir = Vector2D.createVector(dl1.getProj(), dl2.getProj());

        double s1s2 = Vector2D.scalar(meanDir, meanDir);

        double s = Vector2D.scalar(v, meanDir);

        return (dl2.time - dl1.time) * s / s1s2 + dl1.time;
    }

    class DistanceLine {

        boolean up = true;
        double dist;
        BoundaryFeature feature;
        double time;
        PolyLineGeometry geom;
        Point2D proj;

        public DistanceLine() {
            this.proj = new Point2D();
            this.dist = 0.0;
        }

        double[] getProj() {
            return proj.getPosition();
        }

        public void compute(Point2D src) {
            geom.projectEuclidian(src, proj);
            dist = src.distance(proj.getPosition());

            double[] v = Vector2D.createVector(src.getPosition(), proj.getPosition());
            if (Vector2D.scalar(v, new double[]{0, 1}) > 0) {
                up = true;
            } else {
                up = false;
            }

        }

    }

    private class DistanceLineComparator implements Comparator<DistanceLine> {

        @Override
        public int compare(DistanceLine id1, DistanceLine id2) {

            return Double.compare(id1.dist, id2.dist);
        }
    }

    void createTimes() {

        timeMap = new HashMap<>();

        StratigraphicColumn column = getSection().getStratigraphicColumn();

        double time = 0.0;
        StratigraphicEvent event = column.getFirstEvent();
        do {
            timeMap.put(event, time);
            time += 1.0;

            if (event == column.getLastEvent()) {
                break;
            }

            event = column.getNextEvent(event);
        } while (true);

    }

    /**
     *
     * @return all {@link FeatureGeolInterval} that are linked to a
     * {@link StratigraphicEvent}
     */
    private void createDistanceLines() {

        distanceLines = new ArrayList<>();

        for (Patch patch : this.patch.getPatchs()) {

            Collection<FeatureGeolInterval> fIntervals = patch.findObjects(FeatureGeolInterval.class);
            for (FeatureGeolInterval fInterval : fIntervals) {
                BoundaryFeature feature = fInterval.getInterval().getFeature();
                if (feature instanceof StratigraphicEvent) {
                    InfinitePolyline line = new InfinitePolyline();
                    line.initialize(fInterval.getInterval().getPoints2D());
                    DistanceLine indexedDist = new DistanceLine();
                    indexedDist.geom = new PolyLineGeometry(line);
                    indexedDist.feature = feature;
                    indexedDist.time = timeMap.get(feature);
                    distanceLines.add(indexedDist);
                }
            }

        }

    }

}
