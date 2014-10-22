package straticrush.interaction;

import fr.ifp.kronosflow.controller.Event;

public interface IViewListener {
	
	void destroy();
	void objectChanged( Object object, Event event );
}
