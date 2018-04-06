package stratifx.model.loader;

import fr.ifp.kronosflow.model.geology.GeologicLibrary;
import fr.ifp.kronosflow.utils.LOGGER;
import org.json.simple.JSONObject;
import stratifx.model.persistable.IPersisted;
import stratifx.model.persistable.PersistableGeologicLib;

public class GeologicLibLoader extends AbstractLoader {

    @Override
    public IPersisted create() {
        return new PersistableGeologicLib();
    }

    @Override
    public IPersisted load(JSONObject jsonLib) {

        PersistableGeologicLib persistableGeologicLib = (PersistableGeologicLib)data;

        persistableGeologicLib.setStratiColumn( AbstractLoader.loadSubObject(jsonLib, "stratiColumn") );
        persistableGeologicLib.setGeologicFeatures( AbstractLoader.loadObjectArray(jsonLib, "geologicalFeatures") );

        LOGGER.debug("load " + data.getClass() + "(" + data.getName() +")", getClass());
        return data;

    }
}
