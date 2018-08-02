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
package stratifx.application.properties;

import fr.ifp.kronosflow.model.FeatureGeolInterval;
import fr.ifp.kronosflow.model.geology.BoundaryFeature;
import fr.ifp.kronosflow.model.geology.StratigraphicEvent;
import fr.ifp.kronosflow.model.property.EnumProperty;
import fr.ifp.kronosflow.controllers.property.PropertyComputer;
import fr.ifp.kronosflow.kernel.geometry.Point2D;
import fr.ifp.kronosflow.geoscheduler.property.GeoschedulerPropertyComputer;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.property.PropertyStyle;
import fr.ifp.kronosflow.kernel.property.IPropertyAccessor;
import fr.ifp.kronosflow.kernel.property.Property;
import fr.ifp.kronosflow.kernel.property.PropertyLocation;
import fr.ifp.kronosflow.model.time.ATimeProvider;
import fr.ifp.kronosflow.utils.LOGGER;

import java.util.*;

public class TimePropertyComputer extends GeoschedulerPropertyComputer {

    Map<StratigraphicEvent, Double> timeMap;

    protected TimePropertyComputer(GeoschedulerSection section) {
        super(section);
    }

    static public class Builder implements PropertyComputer.Builder {

        @Override
        public PropertyComputer create(Section section) {

            if (section instanceof GeoschedulerSection) {
                TimePropertyComputer computer = new TimePropertyComputer((GeoschedulerSection) section);
                computer.timeMap = ATimeProvider.createTimes(section);
                return computer;
            }

            LOGGER.error("need a GeoschedulerSection to create this PropertyComputer", getClass());

            return null;
        }
    }



    @Override
    public Property compute(Collection<Patch> patches) {

        Property surfaceProp = findOrCreateProperty(EnumProperty.TIME.info);

        IPropertyAccessor accessor = surfaceProp.getAccessor();

        for (Patch patch : patches) {
            computeUsingPatch(patch, accessor);
        }

        PropertyStyle propStyle = new PropertyStyle(section_.getStyle());
        propStyle.setCurrent(surfaceProp);

        return surfaceProp;

    }

    private void computeUsingPatch(Patch patch, IPropertyAccessor accessor) {

        int count = 0;

        Collection<FeatureGeolInterval> fIntervals = patch.findObjects(FeatureGeolInterval.class);

        for (FeatureGeolInterval fInterval : fIntervals) {
            BoundaryFeature feature = fInterval.getInterval().getFeature();
            if (feature instanceof StratigraphicEvent) {
                count++;
                double time = timeMap.get(feature);
                for (Point2D pt : fInterval.getInterval().getPoints2D()) {
                    PropertyLocation location = new PropertyLocation(patch, pt.getPosition());
                    accessor.setValue(location, time);
                }
            }
        }

        if( count < 3 ) {
            LOGGER.info("compute time using " + count + " horizons", getClass());
        }
        else {
            LOGGER.error("more than 3 horizons in this patch :" + count + " horizons", getClass());
        }

    }





}
