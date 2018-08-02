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

import fr.ifp.kronosflow.model.property.EnumProperty;
import fr.ifp.kronosflow.controllers.property.PropertyComputer;
import fr.ifp.kronosflow.kernel.geometry.Point2D;
import fr.ifp.kronosflow.geoscheduler.property.GeoschedulerPropertyComputer;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.property.PropertyStyle;
import fr.ifp.kronosflow.kernel.polyline.ICurviPoint;
import fr.ifp.kronosflow.kernel.polyline.PolyLine;
import fr.ifp.kronosflow.kernel.property.IPropertyAccessor;
import fr.ifp.kronosflow.kernel.property.Property;
import fr.ifp.kronosflow.kernel.property.PropertyLocation;
import fr.ifp.kronosflow.utils.LOGGER;
import java.util.Collection;

public class XYPropertyComputer extends GeoschedulerPropertyComputer {

    protected XYPropertyComputer(GeoschedulerSection section) {
        super(section);
    }

    static public class Builder implements PropertyComputer.Builder {

        @Override
        public PropertyComputer create(Section section) {

            if (section instanceof GeoschedulerSection) {
                return new XYPropertyComputer((GeoschedulerSection) section);
            }

            LOGGER.error("need a GeoschedulerSection to create this PropertyComputer", getClass());

            return null;
        }
    }

    @Override
    public Property compute(Collection<Patch> patches) {

        Property surfaceProp = findOrCreateProperty(EnumProperty.XY.info);

        IPropertyAccessor accessor = surfaceProp.getAccessor();

        for (Patch patch : patches) {
            computeUsingPatch(patch, accessor);
        }

        PropertyStyle propStyle = new PropertyStyle(section_.getStyle());
        propStyle.setCurrent(surfaceProp);

        return surfaceProp;

    }

    private void computeUsingPatch(Patch patch, IPropertyAccessor accessor) {

        PolyLine border = patch.getBorder();

        for (ICurviPoint cp : border.getPoints()) {
            Point2D pt = border.getPosition(cp);

            PropertyLocation location = new PropertyLocation(patch, pt.getPosition());
            accessor.setValue(location, pt.getPosition());
        }

    }

   

}
