package stratifx.application.views;

import fr.ifp.kronosflow.property.IPropertyProvider;
import fr.ifp.kronosflow.property.IPropertyValue;
import fr.ifp.kronosflow.property.Property;
import fr.ifp.kronosflow.property.PropertyLocation;
import fr.ifp.kronosflow.property.PropertyStatistic;
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

        return colormap.getColor(value.real());
    }

    private void updateColormap(Property property) {

        PropertyStatistic stat = property.getAccessor().getStatistic();

        if (stat == null) {
            LOGGER.error("unable to retrieve Properties info for colormap", getClass());
            return;
        }

        IPropertyValue min = stat.getMinValue();
        IPropertyValue max = stat.getMaxValue();

        colormap.setMinMax(min.real(), max.real());
    }

}
