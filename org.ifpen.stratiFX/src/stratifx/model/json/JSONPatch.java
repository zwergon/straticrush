package stratifx.model.json;

import stratifx.model.persistable.AbstractPersisted;
import stratifx.model.persistable.IPersisted;
import stratifx.model.persistable.PersistablePatch;
import stratifx.model.persistable.PersistablePolyline;

public class JSONPatch extends JSONPersisted {
    JSONPatch(PersistablePatch persisted) {
        super(persisted);

        put("unitId", persisted.getUnitId());
        put("facies", persisted.getFaciesId());
        put("name", persisted.getName());
        put("border", new JSONLine((PersistablePolyline)persisted.getBorder()));
        exportLongArray("featureIntervalIds", persisted.getFeatureIntervalIds());
        exportDoubleArray("intervalsS1S2", persisted.getIntervalsS1S2());
        exportLongArray("boundaryfeaturesId", persisted.getBoundaryfeaturesId());

    }
}
