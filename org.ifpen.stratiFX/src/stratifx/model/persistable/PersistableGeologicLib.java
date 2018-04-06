package stratifx.model.persistable;

import fr.ifp.kronosflow.model.geology.GeologicLibrary;
import fr.ifp.kronosflow.uids.IHandle;

import java.util.ArrayList;
import java.util.List;

public class PersistableGeologicLib extends AbstractPersisted {

    List<IPersisted> geologicFeatures = new ArrayList<>();

    IPersisted stratiColumn;

    public PersistableGeologicLib() {
    }

    public PersistableGeologicLib(GeologicLibrary geologicLibrary) {
        super(geologicLibrary, geologicLibrary.getName());
    }

    public List<IPersisted> getGeologicFeatures() {
        return geologicFeatures;
    }

    public void setGeologicFeatures(List<IPersisted> geologicFeatures) {
        this.geologicFeatures = geologicFeatures;
    }

    public IPersisted getStratiColumn() {
        return stratiColumn;
    }

    public void setStratiColumn(IPersisted stratiColumn) {
        this.stratiColumn = stratiColumn;
    }
}
