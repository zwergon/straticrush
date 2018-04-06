package stratifx.model.loader;

import fr.ifp.kronosflow.utils.LOGGER;
import org.json.simple.JSONObject;
import stratifx.model.persistable.IPersisted;
import stratifx.model.persistable.PersistablePolyline;

public class LineLoader extends AbstractLoader{


    @Override
    public IPersisted create() {
        return new PersistablePolyline();
    }

    @Override
    public IPersisted load(JSONObject jsonLine) {

        PersistablePolyline persistablePolyline = (PersistablePolyline)data;

        persistablePolyline.setClosed((Boolean)jsonLine.get("closed"));
        persistablePolyline.setCurviIDs( AbstractLoader.loadLongArray(jsonLine, "curviIds"));
        persistablePolyline.setCurviPositions(AbstractLoader.loadDoubleArray(jsonLine, "curviPositions"));
        persistablePolyline.setNodesIDs( AbstractLoader.loadLongArray(jsonLine, "nodeIds"));
        persistablePolyline.setCurviValues(AbstractLoader.loadDoubleArray(jsonLine, "curviValues"));

        LOGGER.debug("load " + data.getClass() + "(" + data.getName() +")", getClass());

        return data;
    }
}
