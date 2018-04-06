package stratifx.model.persistable;

import fr.ifp.kronosflow.model.geology.GeologicFeature;
import fr.ifp.kronosflow.model.geology.GeologicLibrary;
import fr.ifp.kronosflow.uids.IHandle;

public class PersistableGeologicFeature extends AbstractPersisted {


    String geologicType;

    int awtColor;

    boolean extendable;

    public PersistableGeologicFeature() {
    }

    public PersistableGeologicFeature(GeologicFeature feature) {
        super(feature, feature.getName());
    }

    public String getGeologicType() {
        return geologicType;
    }

    public void setGeologicType(String geologicType) {
        this.geologicType = geologicType;
    }

    public int getAwtColor() {
        return awtColor;
    }

    public void setAwtColor(long awtColor) {
        this.awtColor = (int)awtColor;
    }

    public boolean isExtendable() {
        return extendable;
    }

    public void setExtendable(boolean extendable) {
        this.extendable = extendable;
    }
}
