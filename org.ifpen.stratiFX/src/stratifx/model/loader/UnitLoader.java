package stratifx.model.loader;

import org.json.simple.JSONObject;
import stratifx.model.persistable.IPersisted;
import stratifx.model.persistable.PersistableUnit;

public class UnitLoader extends AbstractLoader {
    @Override
    public IPersisted create() {
        return new PersistableUnit();
    }

    @Override
    public IPersisted load(JSONObject jsonUnit) {
        PersistableUnit persistableUnit = (PersistableUnit)data;
        persistableUnit.setRgbColor( (String)jsonUnit.get("rgbColor"));
        persistableUnit.setTopEvent( AbstractLoader.loadSubObject(jsonUnit, "topEvent"));
        return data;
    }
}
