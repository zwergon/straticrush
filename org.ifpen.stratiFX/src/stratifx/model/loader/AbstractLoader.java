package stratifx.model.loader;

import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.model.explicit.ExplicitPatch;
import fr.ifp.kronosflow.kernel.polyline.explicit.ExplicitPolyLine;
import fr.ifp.kronosflow.kernel.polyline.explicit.InfinitePolyline;
import fr.ifp.kronosflow.model.geology.*;
import fr.ifp.kronosflow.utils.LOGGER;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import stratifx.application.main.GParameters;
import stratifx.model.persistable.IPersisted;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractLoader {

    IPersisted data;

    static Map<String, Class<?> > loaderMap;

    static {
        loaderMap = new HashMap<>();
        loaderMap.put(ExplicitPatch.class.getCanonicalName(), PatchLoader.class );
        loaderMap.put(GeoschedulerSection.class.getCanonicalName(), SectionLoader.class );
        loaderMap.put(ExplicitPolyLine.class.getCanonicalName(), LineLoader.class );
        loaderMap.put(InfinitePolyline.class.getCanonicalName(), LineLoader.class );
        loaderMap.put(GeologicLibrary.class.getCanonicalName(), GeologicLibLoader.class );
        loaderMap.put(StratigraphicUnit.class.getCanonicalName(), UnitLoader.class );
        loaderMap.put(StratigraphicEvent.class.getCanonicalName(), FeatureLoader.class );
        loaderMap.put(UnassignedBodyFeature.class.getCanonicalName(), FeatureLoader.class);
        loaderMap.put(UnassignedBoundaryFeature.class.getCanonicalName(), FeatureLoader.class);
        loaderMap.put(FaultFeature.class.getCanonicalName(), FeatureLoader.class );
        loaderMap.put(StratigraphicColumn.class.getCanonicalName(), StratiColumnLoader.class );
        loaderMap.put(GParameters.class.getCanonicalName(), ParametersLoader.class);

    }

    static public AbstractLoader createLoader(JSONObject jsonObject ){

        try {

            String persistedClass = (String)jsonObject.get( "persistedClass");
            if ( null == persistedClass ){
                LOGGER.error("unable to find persistedClass key in json", AbstractLoader.class );
                return null;
            }

            Class loaderClazz = loaderMap.get(persistedClass);
            if ( null == loaderClazz ){
                LOGGER.error("No loader was found for " + persistedClass, AbstractLoader.class );
                return null;
            }
            AbstractLoader loader = (AbstractLoader)loaderClazz.newInstance();
            if ( loader != null ) {
                loader.data = loader.create();
                loader.data.setUid((Long)jsonObject.get("uid"));
                loader.data.setName((String)jsonObject.get("name"));
                loader.data.setPersistedClass(persistedClass);
            }

            return loader;

        } catch ( IllegalAccessException | InstantiationException e) {
            LOGGER.error("Unable to create loader -> Exception " + e.getClass(), AbstractLoader.class );
        }

        return null;

    }


    static double[] loadDoubleArray( JSONObject jsonObject, String key) {
        JSONArray jsonArray = (JSONArray)jsonObject.get(key);
        double[] array = new double[jsonArray.size()];
        for(int i=0; i<jsonArray.size(); i++ ){
            array[i] = (Double)jsonArray.get(i);
        }
        return array;
    }

    static long[] loadLongArray( JSONObject jsonObject, String key) {
        JSONArray jsonArray = (JSONArray)jsonObject.get(key);
        long[] array = new long[jsonArray.size()];
        for(int i=0; i<jsonArray.size(); i++ ){
            array[i] = (Long)jsonArray.get(i);
        }
        return array;
    }

    static public IPersisted loadSubObject(JSONObject jsonObject, String key ){
        JSONObject jsonChild = (JSONObject) jsonObject.get(key);
        AbstractLoader loader = AbstractLoader.createLoader(jsonChild);
        if ( null != loader ) {
            return loader.load(jsonChild);
        }
        return null;
    }

    static public IPersisted loadObject( JSONObject jsonObject ){
        AbstractLoader loader = AbstractLoader.createLoader(jsonObject);
        if ( null != loader ) {
            return loader.load(jsonObject);
        }
        return null;
    }

    static public List<IPersisted> loadObjectArray(JSONObject jsonObject, String key ){

        List<IPersisted> objects = new ArrayList<>();
        JSONArray jsonArray = (JSONArray)jsonObject.get(key);
        for( Object o : jsonArray ){
            if ( o instanceof JSONObject ){
                objects.add( AbstractLoader.loadObject((JSONObject)o) );
            }
        }

        return objects;
    }

    public abstract IPersisted create();

    public abstract IPersisted load(JSONObject object);

}
