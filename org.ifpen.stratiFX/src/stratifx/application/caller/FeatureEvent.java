package stratifx.application.caller;

import fr.ifp.kronosflow.controllers.events.AbstractControllerEvent;
import fr.ifp.kronosflow.controllers.events.EnumEventAction;
import fr.ifp.kronosflow.controllers.events.EnumEventType;
import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.geology.GeologicFeature;

public class FeatureEvent extends AbstractControllerEvent<KinObject> {

    private final GeologicFeature feature;

    public FeatureEvent(final EnumEventAction action, final EnumEventType type, final KinObject obj,
                        final GeologicFeature feature) {
        super(action, type, obj);
        this.feature = feature;
    }

    public GeologicFeature getFeature() {
        return feature;
    }
}
