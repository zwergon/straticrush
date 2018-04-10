package stratifx.model.loader;

import fr.ifp.kronosflow.utils.LOGGER;
import org.json.simple.JSONObject;
import stratifx.model.persistable.IPersisted;
import stratifx.model.persistable.PersistableGeologicFeature;

public class FeatureLoader extends AbstractLoader{

    @Override
    public IPersisted create() {
        return new PersistableGeologicFeature();
    }

    @Override
    public IPersisted load(JSONObject jsonFeature ) {

        PersistableGeologicFeature persistableFeature = (PersistableGeologicFeature)data;
        persistableFeature.setRgbColor( (String)jsonFeature.get("rgbColor"));

        LOGGER.debug("load " + data.getClass() + "(" + data.getName() +")", getClass());

        return data;
    }
}
