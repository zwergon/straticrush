package stratifx.application.caller;

import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import stratifx.application.main.UIAction;

public class EventUIAction extends UIAction< IControllerEvent<?> > {

    public EventUIAction(IControllerEvent<?> event) {
        super(EVENT, event);
    }


}
