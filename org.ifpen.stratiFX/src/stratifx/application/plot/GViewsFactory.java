package stratifx.application.plot;

import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.model.*;
import fr.ifp.kronosflow.model.explicit.ExplicitPatch;
import fr.ifp.kronosflow.model.geology.Paleobathymetry;
import fr.ifp.kronosflow.model.implicit.MeshPatch;
import fr.ifp.kronosflow.model.topology.Border;
import fr.ifp.kronosflow.model.topology.Contact;
import fr.ifp.kronosflow.model.LineSet;
import stratifx.application.views.*;

import java.util.HashMap;
import java.util.Map;

public class GViewsFactory {

    static private Map<String, String> mapViews;

    static {
        mapViews = new HashMap<String, String>();
        registerView(Patch.class, GPatchView.class);
        registerView(MeshPatch.class, GPatchView.class);
        registerView(ExplicitPatch.class, GPatchView.class);
        registerView(CompositePatch.class, GPatchView.class);
        registerView(PatchInterval.class, GPatchIntervalView.class);
        registerView(FeatureGeolInterval.class, GPatchIntervalView.class);
        registerView(Contact.class, GPartitionLineView.class);
        registerView(Border.class, GPartitionLineView.class);
        registerView(Paleobathymetry.class, GPaleoView.class);
        registerView(Section.class, GSectionView.class);
        registerView(GeoschedulerSection.class, GSectionView.class);
        registerView(LineSet.class, GLineSetView.class);
    }

    static public void registerView(Class<?> object_class, Class<?> view_class) {
        mapViews.put(object_class.getCanonicalName(), view_class.getCanonicalName());
    }


    public static GView createView(Object object) {

        if (object == null) {
            return null;
        }
        GView view = null;
        try {
            /*
             * TODO go through class inheritance to find the first ascending
             * class valid to create a GView
             */
            String key = object.getClass().getCanonicalName();

            //LOGGER.debug("create View " + key, getClass());

            if (!mapViews.containsKey(key)) {
                return null;
            }

            Class<?> c1 = Class.forName(mapViews.get(key));
            if (c1 == null) {
                return null;
            }
            view = (GView) c1.newInstance();
            if (null != view) {
                view.setModel(object);
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        return view;
    }







}
