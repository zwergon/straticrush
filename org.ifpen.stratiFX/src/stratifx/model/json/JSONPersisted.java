package stratifx.model.json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import stratifx.model.persistable.AbstractPersisted;

public class JSONPersisted extends JSONObject {

    JSONPersisted(AbstractPersisted persisted ){
        put( "persistedClass", persisted.getPersistedClass() );
        put( "uid", persisted.getUid() );
        put( "name", persisted.getName() );
    }

    protected void exportLongArray( String name, long[] array ){
        JSONArray jsonArray = new JSONArray();
        for( long values : array ) {
            jsonArray.add(values);
        }
        put(name, jsonArray);
    }

    protected void exportDoubleArray( String name, double[] array ){
        JSONArray jsonArray = new JSONArray();
        for( double values : array ) {
            jsonArray.add(values);
        }
        put(name, jsonArray);
    }
}
