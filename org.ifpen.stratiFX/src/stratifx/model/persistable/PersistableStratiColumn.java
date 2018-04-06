package stratifx.model.persistable;

import fr.ifp.kronosflow.model.geology.StratigraphicColumn;

import java.util.ArrayList;
import java.util.List;

public class PersistableStratiColumn  extends AbstractPersisted {

    List<IPersisted> units = new ArrayList<>();

    public PersistableStratiColumn(){}

    public PersistableStratiColumn(StratigraphicColumn column){
        super(column, column.getName());
    }

    public List<IPersisted> getUnits() {
        return units;
    }

    public void setUnits(List<IPersisted> units) {
        this.units = units;
    }

}
