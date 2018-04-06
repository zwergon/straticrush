package stratifx.model.json;

import stratifx.model.persistable.PersistableGeologicFeature;

public class JSONFeature extends JSONPersisted {

    JSONFeature(PersistableGeologicFeature persisted) {
        super(persisted);


        put("geologicType", persisted.getGeologicType());
        put("awtColor", persisted.getAwtColor());
        put("extendable", persisted.isExtendable());
    }
}
