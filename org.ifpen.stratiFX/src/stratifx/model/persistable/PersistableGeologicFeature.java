package stratifx.model.persistable;

import fr.ifp.kronosflow.model.geology.GeologicFeature;

public class PersistableGeologicFeature extends AbstractPersisted {

    String rgbColor;

    public PersistableGeologicFeature() {
    }

    public PersistableGeologicFeature(GeologicFeature feature) {
        super(feature, feature.getName());
    }

    public String getRgbColor() {
        return rgbColor;
    }

    public void setRgbColor(String color) {
        this.rgbColor = color;
    }

}
