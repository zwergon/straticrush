package stratifx.application.properties;

import fr.ifp.kronosflow.controllers.property.PropertyComputer;
import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.geoscheduler.property.GeoschedulerPropertyComputer;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.style.PropertyStyle;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.Node;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.property.IPropertyAccessor;
import fr.ifp.kronosflow.property.Property;
import fr.ifp.kronosflow.property.PropertyInfo;
import fr.ifp.kronosflow.property.PropertyInfo.Kind;
import fr.ifp.kronosflow.property.PropertyInfo.Support;
import fr.ifp.kronosflow.property.PropertyLocation;
import fr.ifp.kronosflow.uids.UID;
import fr.ifp.kronosflow.utils.LOGGER;

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
    public Property compute() {

        Property surfaceProp = findOrCreateProperty(
                new PropertyInfo("XY", Support.NodeProperty, Kind.Vector)
        );

        IPropertyAccessor accessor = surfaceProp.getAccessor();

        PatchLibrary patchLib = section_.getPatchLibrary();
        for (Patch patch : patchLib.getPatches()) {
            if (patch instanceof IMeshProvider) {
                computeUsingMesh(patch, accessor);
            } else {
                computeUsingPatch(patch, accessor);
            }
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

    private void computeUsingMesh(Patch patch, IPropertyAccessor accessor) {

        Mesh2D mesh = ((IMeshProvider) patch).getMesh();

        for (UID uid : mesh.getNodeIds()) {
            Node node = (Node) mesh.getNode(uid);
            PropertyLocation location = node.getLocation(patch);
            accessor.setValue(location, node.getPosition());
        }

    }

    @Override
    public Property compute(Patch patchToCompute) {

        Property surfaceProp = findOrCreateProperty(
                new PropertyInfo("XY", Support.NodeProperty, Kind.Vector)
            );

        IPropertyAccessor accessor = surfaceProp.getAccessor();

        if (patchToCompute instanceof IMeshProvider) {
            computeUsingMesh(patchToCompute, accessor);
        } else {
            computeUsingPatch(patchToCompute, accessor);
        }

        PropertyStyle propStyle = new PropertyStyle(section_.getStyle());
        propStyle.setCurrent(surfaceProp);

        return surfaceProp;
    }

}
