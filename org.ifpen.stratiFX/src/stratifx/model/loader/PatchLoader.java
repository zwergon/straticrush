package stratifx.model.loader;

import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.utils.LOGGER;
import org.json.simple.JSONObject;
import stratifx.model.json.JSONLine;
import stratifx.model.persistable.IPersisted;
import stratifx.model.persistable.PersistablePatch;
import stratifx.model.persistable.PersistablePolyline;

public class PatchLoader extends AbstractLoader {


    @Override
    public IPersisted create() {
        return new PersistablePatch();
    }

    @Override
    public IPersisted load(JSONObject jsonPatch ) {

        PersistablePatch persistablePatch =(PersistablePatch)data;

        persistablePatch.setUnitId((Long)jsonPatch.get("unitId"));
        persistablePatch.setFaciesId((Long)jsonPatch.get("facies"));
        persistablePatch.setBorder(AbstractLoader.loadSubObject(jsonPatch, "border"));
        persistablePatch.setFeatureIntervalIds( AbstractLoader.loadLongArray(jsonPatch, "featureIntervalIds"));
        persistablePatch.setBoundaryfeaturesId( AbstractLoader.loadLongArray(jsonPatch,"boundaryfeaturesId"));
        persistablePatch.setIntervalsS1S2( AbstractLoader.loadDoubleArray(jsonPatch, "intervalsS1S2"));

        LOGGER.debug("load " + data.getClass() + "(" + data.getName() +")", getClass());

        return data;
    }
}
