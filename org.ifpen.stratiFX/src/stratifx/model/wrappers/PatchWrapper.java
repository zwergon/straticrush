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
package stratifx.model.wrappers;

import fr.ifp.kronosflow.geometry.Geometry;
import fr.ifp.kronosflow.model.ContactInterval;
import fr.ifp.kronosflow.model.FeatureGeolInterval;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.geology.*;
import fr.ifp.kronosflow.model.wrapper.IWrapper;
import fr.ifp.kronosflow.model.wrapper.WrapperFactory;
import fr.ifp.kronosflow.polyline.CurviPoint;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.ICurviPoint.CoordType;
import fr.ifp.kronosflow.polyline.PolyLine;
import stratifx.model.persistable.PersistablePatch;
import stratifx.model.persistable.PersistablePolyline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author lecomtje
 */
public class PatchWrapper implements IWrapper<Patch> {

    PersistablePatch persistedPatch;

    public PatchWrapper() {
    }

   

    @Override
    public boolean load(Patch wrapped) {

        if (null == persistedPatch) {
            return false;
        }

        // First, load the polyline
        PolyLine border = wrapped.getBorder();
        if (border == null) {
            border = (PolyLine) WrapperFactory.build( persistedPatch.getBorder().getPersistedClass() );
            wrapped.setBorder(border);
        }
       
        WrapperFactory.load( border, persistedPatch.getBorder() );

        // Second, the patchInterval for the features
        loadPatchInterval(wrapped);

        // Some Metadata
        wrapped.setName(persistedPatch.getName());
        wrapped.setUID( persistedPatch.getUid());

        // StratiUnit
        // On récupere la geological lib
        GeologicLibrary library = ((Section) wrapped.getParent().getParent()).getFeatures();

        // StratiUnit
        BodyFeature unit = wrapped.getBodyFeature();
        if (unit == null || persistedPatch.getUnitId() != unit.getUID().getId()) {
            if (unit != null) {
                wrapped.removeFeature(unit);
            }

            List<StratigraphicUnit> features = library
                    .getGeologicFeaturesByClass(StratigraphicUnit.class);
            for (StratigraphicUnit feature : features) {
                if (persistedPatch.getUnitId() == feature.getUID().getId()) {
                    wrapped.setFeature(feature);
                    break;
                }
            }
        }

        LithoFaciesFeature litho = wrapped.getGeologicFeaturesByClass(LithoFaciesFeature.class);

        if (litho == null || (persistedPatch.getFaciesId() != litho.getLithoIndex())) {

            if (litho != null) {
                wrapped.removeFeature(litho);
            }

            for (LithoFaciesFeature feature : library
                    .getGeologicFeaturesByClass(LithoFaciesFeature.class)) {
                if (persistedPatch.getFaciesId() == feature.getLithoIndex()) {
                    wrapped.setFeature(feature);
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean save(Patch wrapped) {

        if ( null == persistedPatch ) {
            persistedPatch = new PersistablePatch(wrapped); // First, save the polyline
        }

        PersistablePolyline polyline = (PersistablePolyline)persistedPatch.getBorder();
        if (polyline == null) {
            polyline = new PersistablePolyline(wrapped.getBorder());
            persistedPatch.setBorder(polyline);
        }
        
        WrapperFactory.save( wrapped.getBorder(), persistedPatch.getBorder() );
        
        // Second, the patchInterval for the features
        savePatchInterval(wrapped);

        // Some Metadata
        persistedPatch.setName(wrapped.getName());
        persistedPatch.setUid(wrapped.getUID().getId());

        // StratiUnit
        BodyFeature unit = wrapped.getBodyFeature();
        if (unit != null) {
            persistedPatch.setUnitId(unit.getUID().getId());
        } else {
            persistedPatch.setUnitId(-1);
        }

        LithoFaciesFeature litho = wrapped.getGeologicFeaturesByClass(LithoFaciesFeature.class);

        if (litho != null) {
            persistedPatch.setFaciesId(litho.getLithoIndex());
        } else {
            persistedPatch.setFaciesId(-1);
        }

        return true;
    }

    protected void loadPatchInterval(Patch wrapped) {
        List<PatchInterval> intervals = wrapped.getIntervals();

        double[] s1s2 = persistedPatch.getIntervalsS1S2();
        long[] ids = persistedPatch.getFeatureIntervalIds();
        Set<Long> done = new HashSet<>();

        // On r�cupere la geological lib
        GeologicLibrary library = ((Section) wrapped.getParent().getParent()).getFeatures();

        HashMap<Long, BoundaryFeature> features = new HashMap<>();

        library.getGeologicFeaturesByClass(BoundaryFeature.class).forEach((feature) -> {
            features.put(feature.getUID().getId(), feature);
        });

        features.put(library.getUnassignedBoundaryFeature().getUID().getId(), library.getUnassignedBoundaryFeature());

        // On prend tout sauf les contacts
        for (PatchInterval interval : intervals) {

            if (!(interval instanceof ContactInterval)) {

                int index = persistedPatch.indexOfFeaturesId(interval.getUID());
                if (index != -1) {
                    done.add(interval.getUID().getId());

                    // Try to find a point of the border corresponding
                    ICurviPoint s1 = null;
                    ICurviPoint s2 = null;

                    for (ICurviPoint point : wrapped.getBorder().getPoints()) {
                        if (point.getCurvilinear() == s1s2[index * 2]) {
                            s1 = point;
                        }

                        if (point.getCurvilinear() == s1s2[index * 2 + 1]) {
                            s2 = point;
                        }
                    }

                    if (s1 == null) {
                        s1 = new CurviPoint(CoordType.INTERPOLATED, s1s2[index * 2]);
                    }

                    if (s2 == null) {
                        s2 = new CurviPoint(CoordType.INTERPOLATED, s1s2[index * 2 + 1]);
                    }
                    interval.getInterval().setS1(s1);
                    interval.getInterval().setS2(s2);
                    interval.getInterval().setParentLine(wrapped.getPolyline());

                    BoundaryFeature feature = features.get(persistedPatch.getBoundaryfeaturesId()[index]);

                    interval.getInterval().setFeature(feature);
                } // On retire l'interval sur-num�raire
                else {
                    wrapped.remove(interval);
                }
            }
        }

        for (int index = 0; index < ids.length; index++) {
            if (!done.contains(ids[index])) {
                BoundaryFeature feature = features.get(persistedPatch.getBoundaryfeaturesId()[index]);
                // Try to find a point of the border corresponding
                ICurviPoint s1 = null;
                ICurviPoint s2 = null;

                for (ICurviPoint point : wrapped.getBorder().getPoints()) {
                    if (Geometry.isEqual(point.getCurvilinear(), s1s2[index * 2])) {
                        s1 = point;
                    }

                    if (Geometry.isEqual(point.getCurvilinear(), s1s2[index * 2 + 1])) {
                        s2 = point;
                    }
                }

                if (s1 == null) {
                    s1 = new CurviPoint(CoordType.INTERPOLATED, s1s2[index * 2]);
                }

                if (s2 == null) {
                    s2 = new CurviPoint(CoordType.INTERPOLATED, s1s2[index * 2 + 1]);
                }

                FeatureGeolInterval interval = wrapped.addFeatureGeolInterval(s1, s2, feature);
                interval.setUID(ids[index]);
            }
        }

    }

    protected void savePatchInterval(Patch wrapped) {
        List<PatchInterval> intervals = wrapped.getIntervals();

        // On prend tout sauf les contacts
        //premiere boucle pour compter
        int size = 0;
        for (PatchInterval interval : intervals) {
            if (!(interval instanceof ContactInterval)) {
                size++;
            }
        }

        double[] s1s2 = new double[2 * size];
        long[] ids = new long[size];
        long[] featureIds = new long[size];

        int index = 0;
        // deuxième boucle pour stocker.
        for (PatchInterval interval : intervals) {
            if (!(interval instanceof ContactInterval)) {
                ids[index] = interval.getUID().getId();
                s1s2[2 * index] = interval.getInterval().getS1().getCurvilinear();
                s1s2[2 * index + 1] = interval.getInterval().getS2().getCurvilinear();
                featureIds[index] = interval.getInterval().getFeature().getUID().getId();
                index++;
            }
        }

        persistedPatch.setBoundaryfeaturesId(featureIds);
        persistedPatch.setIntervalsS1S2(s1s2);
        persistedPatch.setFeatureIntervalIds(ids);

    }

    @Override
    public void setPersisted(Object persisted) {
        persistedPatch = (PersistablePatch)persisted;
    }

    @Override
    public Object getPersisted() {
        return persistedPatch;
    }
}
