package stratifx.model.json;

import org.json.simple.JSONArray;
import stratifx.model.persistable.*;

public class JSONSection extends JSONPersisted {

    public JSONSection(PersistableSection persistedSection){
        super(persistedSection);

        PersistablePolyline reference = (PersistablePolyline)persistedSection.getDomainReference();
        put("referencedomain", new JSONLine(reference));

        PersistablePolyline paleobathymetry = (PersistablePolyline)persistedSection.getPaleobathymetry();
        put("paleobathymetry", new JSONLine(paleobathymetry));

        JSONArray jsonArray = new JSONArray();
        for(IPersisted patch : persistedSection.getPatches()){
            PersistablePatch persistablePatch = (PersistablePatch)patch;
            jsonArray.add(new JSONPatch(persistablePatch));
        }
        put("patches", jsonArray);

        PersistableGeologicLib persistableGeologicLib = (PersistableGeologicLib) persistedSection.getGeologicalLibrary();
        put("geologicalLib", new JSONGeologicLib(persistableGeologicLib));
    }

}
