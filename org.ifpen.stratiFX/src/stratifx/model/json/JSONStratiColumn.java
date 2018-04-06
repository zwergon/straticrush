package stratifx.model.json;

import org.json.simple.JSONArray;
import stratifx.model.persistable.IPersisted;
import stratifx.model.persistable.PersistableGeologicFeature;
import stratifx.model.persistable.PersistableStratiColumn;
import stratifx.model.persistable.PersistableUnit;

public class JSONStratiColumn extends JSONPersisted{
    JSONStratiColumn(PersistableStratiColumn persisted) {
        super(persisted);

        {
            JSONArray jsonArray = new JSONArray();
            for (IPersisted unit : persisted.getUnits()) {
                jsonArray.add(new JSONUnit((PersistableUnit) unit));
            }
            put("units", jsonArray);
        }


    }
}
