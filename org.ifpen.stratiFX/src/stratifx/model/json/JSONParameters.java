package stratifx.model.json;


import fr.ifp.kronosflow.model.style.KMetaList;
import fr.ifp.kronosflow.model.style.Style;
import stratifx.model.persistable.PersistedParameters;

import java.util.Map;

public class JSONParameters extends JSONPersisted {
    public JSONParameters(PersistedParameters persisted) {
        super(persisted);

        Style style = persisted.getStyle();

        Map<String, KMetaList> list = style.getList();
        for (Entry<String, KMetaList> entry : list.entrySet()) {
            KMetaList strings = entry.getValue();
            put(entry.getKey(), new JSONKMetaList(strings));
        }
    }
}
