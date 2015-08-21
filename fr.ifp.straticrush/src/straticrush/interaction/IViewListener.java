package straticrush.interaction;

import fr.ifp.kronosflow.newevents.IControllerEvent;


public interface IViewListener {
	
	void destroy();
	void objectChanged( IControllerEvent<?> event );
}
