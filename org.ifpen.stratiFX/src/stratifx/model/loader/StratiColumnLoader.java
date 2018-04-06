package stratifx.model.loader;

import fr.ifp.kronosflow.model.geology.StratigraphicColumn;
import fr.ifp.kronosflow.utils.LOGGER;
import org.json.simple.JSONObject;
import stratifx.model.persistable.IPersisted;
import stratifx.model.persistable.PersistableStratiColumn;

public class StratiColumnLoader extends AbstractLoader {


    @Override
    public IPersisted create() {
        return new PersistableStratiColumn();
    }

    @Override
    public IPersisted load(JSONObject jsonStratiColumn) {

        PersistableStratiColumn persistableStratiColumn = (PersistableStratiColumn)data;

        persistableStratiColumn.setUnits( AbstractLoader.loadObjectArray(jsonStratiColumn, "units"));

        LOGGER.debug("load " + data.getClass() + "(" + data.getName() +")", getClass());
        return data;
    }
}
