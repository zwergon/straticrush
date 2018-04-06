package stratifx.model.json;

import stratifx.model.persistable.PersistableGeologicFeature;
import stratifx.model.persistable.PersistableUnit;

public class JSONUnit extends JSONFeature {

    JSONUnit( PersistableUnit persisted) {
        super(persisted);

        put("topEvent", new JSONFeature( (PersistableGeologicFeature)persisted.getTopEvent()));
    }
}
