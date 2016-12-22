package stratifx.application;

import java.util.HashMap;
import java.util.Map;

public class StratiFXService implements IUIController {
	
	Map<IUIController.Type, IUIController> controllers;
	
	static public StratiFXService instance;
	
	static {
		instance = new StratiFXService();
	}
	
	protected StratiFXService() {
		controllers = new HashMap<IUIController.Type, IUIController>();
	}
	
	public void registerController( IUIController.Type type, IUIController controller ){
		controllers.put( type, controller );
	}
	
	public void broadCastAction( UIAction action ){
		
		//handled by service first.
		if ( handleAction(action) ){
			return;
		}
		
		for( IUIController controller : controllers.values() ){
			if ( controller.handleAction(action) ){
				//action is eaten.
				return;
			}
		}
	}
	
	public void broadCastAction( int actionType ){
		broadCastAction( new UIAction(actionType) ) ;
	}
	
	public void fireAction( IUIController.Type type , UIAction action ){
		controllers.get(type).handleAction( action );
	}
	
	public void fireAction( IUIController.Type type , int action ){
		controllers.get(type).handleAction( new UIAction(action) );
	}

	@Override
	public boolean handleAction(UIAction action) {
		// TODO Auto-generated method stub
		return false;
	}
}
