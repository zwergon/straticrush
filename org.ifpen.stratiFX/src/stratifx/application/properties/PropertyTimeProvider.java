package stratifx.application.properties;

import fr.ifp.kronosflow.kernel.property.IPropertyAccessor;
import fr.ifp.kronosflow.kernel.property.IPropertyProvider;
import fr.ifp.kronosflow.kernel.property.Property;
import fr.ifp.kronosflow.kernel.property.PropertyLocation;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.property.EnumProperty;
import fr.ifp.kronosflow.model.time.ITimeProvider;
import fr.ifp.kronosflow.utils.LOGGER;

public class PropertyTimeProvider implements ITimeProvider {

    IPropertyAccessor timePropertyAccessor;
    IPropertyProvider provider;

    public PropertyTimeProvider(Section section) {

        Property timeProperty = section.getPropertyDB().findProperty(EnumProperty.TIME.info);
        if (null != timeProperty) {
            timePropertyAccessor = timeProperty.getAccessor();
        }
        else {
            LOGGER.error("time property is not computed", getClass());
        }

    }

    @Override
    public void setPatch(Patch patch) {
        this.provider = patch;
    }

    @Override
    public double getTime(double[] pos) {

        if ( null == provider ){
            LOGGER.error("no property Provider set for this time accessor", getClass());
            return 0.0;
        }
        PropertyLocation location = new PropertyLocation(provider, pos);

        return timePropertyAccessor.getValue(location).real();
    }
}
