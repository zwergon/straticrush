/* 
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.stratigraphy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import fr.ifp.kronosflow.geometry.RectD;
import fr.ifp.kronosflow.model.FeatureGeolInterval;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.geology.StratigraphicEvent;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.Interval;
import fr.ifp.kronosflow.polyline.LinePoint;
import fr.ifp.kronosflow.polyline.LinePointPair;
import fr.ifp.kronosflow.polyline.PolyLineGeometry;
import fr.ifp.kronosflow.polyline.PolylineDirection;
import fr.ifp.kronosflow.utils.LOGGER;

/**
 * @author lecomtje
 */
public class PatchHorizonManager {

    Patch patch;

    LinkedList<FeatureGeolInterval> intervals;

    public PatchHorizonManager(Patch patch) {
        this.patch = patch;
        retrievesHorizons();
    }

    public Collection<FeatureGeolInterval> getHorizons() {
        return intervals;
    }

    public FeatureGeolInterval getTop() {
        return intervals.getFirst();
    }

    public FeatureGeolInterval getBottom() {
        return intervals.getLast();
    }

    public List<LinePointPair> getTopBottomPairs() {

        List<LinePointPair> pairs = new ArrayList<>();

        FeatureGeolInterval top = getTop();

        FeatureGeolInterval bottom = getBottom();

        // TODO only one horizon, to do....
        if (top == bottom) {
            LOGGER.warning("only one horizon in Patch !", getClass());
        }

        int nStep = 20;

        Interval topInterval = top.getInterval();
        Interval bottomInterval = bottom.getInterval();

        // start points.
        LinePoint topCenter = new LinePoint(topInterval, topInterval.computeMiddle());
        LinePoint bottomCenter = new LinePoint(bottomInterval, bottomInterval.computeMiddle());

        PolylineDirection polyDir = new PolylineDirection(topInterval);
        PolylineDirection.Orientation dir = polyDir.isSameOrientation(bottomInterval);

        List<ICurviPoint> topSamples = samplePolyline(topInterval, topCenter,
                PolylineDirection.Orientation.SAME, nStep);
        List<ICurviPoint> bottomSamples = samplePolyline(bottomInterval, bottomCenter, dir, nStep);

        return pairs;
    }

    private List<ICurviPoint> samplePolyline(Interval interval, LinePoint from,
            PolylineDirection.Orientation direction, int nStep) {

        List<ICurviPoint> sampled = new ArrayList<>();

        PolyLineGeometry origGeom = new PolyLineGeometry(
                new Interval(interval.getParentLine(), interval.getS1(), from.getCurviPoint()));
        double l1 = origGeom.length();

        origGeom = new PolyLineGeometry(
                new Interval(interval.getParentLine(), from.getCurviPoint(), interval.getS2()));
        double l2 = origGeom.length();

        double lStart;
        double lEnd;
        if (direction == PolylineDirection.Orientation.SAME) {
            lStart = l1;
            lEnd = l2;
        }
        else {
            lStart = l2;
            lEnd = l1;
        }

        double incr = nStep / (lEnd - lStart);

        origGeom = new PolyLineGeometry(interval);
        for (double l = l1; l <= l2; l += incr) {
            ICurviPoint cp = origGeom.getPointAtLength(from.getCurviPoint(), l);
            sampled.add(cp);
        }

        return sampled;
    }

    /**
     * @return all {@link FeatureGeolInterval} that are linked to a {@link StratigraphicEvent}
     */
    private void retrievesHorizons() {

        intervals = new LinkedList<>();

        for (Patch innerPatch : patch.getPatchs()) {

            Collection<FeatureGeolInterval> fIntervals = innerPatch
                    .findObjects(FeatureGeolInterval.class);
            for (FeatureGeolInterval fInterval : fIntervals) {
                if (fInterval.getInterval().getFeature() instanceof StratigraphicEvent) {
                    intervals.add(fInterval);
                }
            }
        }

        // sort them against their mean Y value ( from the higher to the lowest )
        Collections.sort(intervals, new Comparator<FeatureGeolInterval>() {
            @Override
            public int compare(FeatureGeolInterval o1, FeatureGeolInterval o2) {
                PolyLineGeometry geom1 = new PolyLineGeometry(o1.getInterval());
                RectD bbox1 = geom1.computeBoundingBox();

                PolyLineGeometry geom2 = new PolyLineGeometry(o2.getInterval());
                RectD bbox2 = geom2.computeBoundingBox();

                return Double.compare(bbox1.centerY(), bbox2.centerY());

            }

        });
    }

}
