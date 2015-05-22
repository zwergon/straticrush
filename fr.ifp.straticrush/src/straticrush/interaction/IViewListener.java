package straticrush.interaction;

import fr.ifp.kronosflow.controller.IControllerEvent;

public interface IViewListener {
	
	void destroy();
	void objectChanged( IControllerEvent<?> event );
}
