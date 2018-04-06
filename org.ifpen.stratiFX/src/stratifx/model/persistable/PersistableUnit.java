package stratifx.model.persistable;

import fr.ifp.kronosflow.model.geology.StratigraphicUnit;

public class PersistableUnit extends PersistableGeologicFeature {

    IPersisted topEvent;

    public PersistableUnit() {
    }

    public PersistableUnit(StratigraphicUnit unit) {
        super(unit);
    }

    public IPersisted getTopEvent() {
        return topEvent;
    }

    public void setTopEvent(IPersisted topEvent) {
        this.topEvent = topEvent;
    }


}
