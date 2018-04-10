package stratifx.model.json;

import stratifx.model.persistable.PersistableGeologicFeature;

public class JSONFeature extends JSONPersisted {

    JSONFeature(PersistableGeologicFeature persisted) {
        super(persisted);
        put("rgbColor", persisted.getRgbColor());
    }
}
