package stratifx.model.json;

import org.json.simple.JSONArray;
import stratifx.model.persistable.PersistablePolyline;

import java.util.Arrays;

public class JSONLine extends JSONPersisted {

    JSONLine(PersistablePolyline persisted) {
        super(persisted);

        put("closed", persisted.isClosed() );
        exportLongArray("curviIds", persisted.getCurviIDs());
        exportLongArray("nodeIds", persisted.getNodesIDs());
        exportDoubleArray("curviPositions", persisted.getCurviPositions());
        exportDoubleArray("curviValues", persisted.getCurviValues());


    }


}
