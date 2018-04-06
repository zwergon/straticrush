package stratifx.model.json;

import org.json.simple.JSONArray;
import stratifx.model.persistable.IPersisted;
import stratifx.model.persistable.PersistableGeologicFeature;
import stratifx.model.persistable.PersistableGeologicLib;
import stratifx.model.persistable.PersistableStratiColumn;

public class JSONGeologicLib extends JSONPersisted {

    public JSONGeologicLib(PersistableGeologicLib persisted) {
        super(persisted);

        JSONArray jsonArray = new JSONArray();
        for( IPersisted feature : persisted.getGeologicFeatures() ){
            PersistableGeologicFeature persistedFeature = (PersistableGeologicFeature)feature;
            jsonArray.add( new JSONFeature(persistedFeature));
        }
        put("geologicalFeatures", jsonArray);

        put("stratiColumn", new JSONStratiColumn((PersistableStratiColumn)persisted.getStratiColumn()));

    }
}
