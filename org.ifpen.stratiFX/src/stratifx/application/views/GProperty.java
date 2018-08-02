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
package stratifx.application.views;

import fr.ifp.kronosflow.kernel.property.IPropertyProvider;
import fr.ifp.kronosflow.kernel.property.IPropertyValue;
import fr.ifp.kronosflow.kernel.property.Property;
import fr.ifp.kronosflow.kernel.property.PropertyLocation;
import fr.ifp.kronosflow.kernel.property.PropertyStatistic;
import fr.ifp.kronosflow.utils.LOGGER;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GColorMap;

public class GProperty {

    Property property;
    GColorMap colormap;
    IPropertyProvider provider;

    public GProperty(Property property, IPropertyProvider provider) {
        this.property = property;
        this.provider = provider;
        colormap = new GColorMap();

        updateColormap(property);
    }

    public GColorMap getColorMap() {
        return colormap;
    }

    public Property getProperty() {
        return property;
    }

    public GColor getColor(double[] xy) {
        IPropertyValue value = property.getAccessor().getValue( new PropertyLocation(provider, xy) );

        return colormap.getColor(value.last());
    }

    private void updateColormap(Property property) {

        PropertyStatistic stat = property.getAccessor().getStatistic();

        if (stat == null) {
            LOGGER.error("unable to retrieve Properties info for colormap", getClass());
            return;
        }

        IPropertyValue min = stat.getMinValue();
        IPropertyValue max = stat.getMaxValue();

        colormap.setMinMax(min.last(), max.last());
    }

}
