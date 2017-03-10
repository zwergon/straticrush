package stratifx.application.properties;

import fr.ifp.kronosflow.model.property.Properties;
import fr.ifp.kronosflow.controllers.property.PropertyComputer;
import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.geoscheduler.property.GeoschedulerPropertyComputer;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.style.PropertyStyle;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.property.IPropertyAccessor;
import fr.ifp.kronosflow.property.Property;
import fr.ifp.kronosflow.property.PropertyLocation;
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

        Property surfaceProp = findOrCreateProperty(Properties.XY.info);

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
