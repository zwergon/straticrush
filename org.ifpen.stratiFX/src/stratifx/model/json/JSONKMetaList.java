package stratifx.model.json;

import fr.ifp.kronosflow.model.style.KMetaList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONKMetaList  extends JSONObject {
    JSONKMetaList(KMetaList metaList) {

        put("type", metaList.getType());

        JSONArray array = new JSONArray();
        for (String obj : metaList.getObjects()) {
            array.add(obj);
        }
        put("objects", array);
    }
}
