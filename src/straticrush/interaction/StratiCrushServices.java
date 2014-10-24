package straticrush.interaction;

import java.util.HashMap;
import java.util.Map;

import fr.ifp.jdeform.deformation.ChainMeshNodeMove;
import fr.ifp.jdeform.deformation.MassSpringNodeMove;
import fr.ifp.jdeform.deformation.TranslateNodeMove;
import fr.ifp.kronosflow.controller.Event;
import fr.ifp.kronosflow.controller.IKronosController;
import fr.ifp.kronosflow.controller.IEventListener;

public class StratiCrushServices extends ViewNotifier implements IEventListener{
	
	private static StratiCrushServices instance;
	private Map<String, String> controllersMap = new HashMap<String, String>(); //dictionnary for IController creation
	
	
	static {
		instance = null;	
	}
	
	public static StratiCrushServices getInstance() {
		if ( null == instance ){
			instance = new StratiCrushServices();
		}		
		return instance;
	}
	
	
	void registerController( String type, Class<?> view_class ){
		controllersMap.put( type, view_class.getCanonicalName() );
	}
	
	
	
	
	protected StratiCrushServices() {	
		registerController("Translate", TranslateNodeMove.class);
		registerController("ChainMail", ChainMeshNodeMove.class);
		registerController("MassSpring", MassSpringNodeMove.class );
	}
	
	public IKronosController createController( String type ){

		IKronosController controller = null;
	    try {
	    	/*
	    	 * TODO go through class inheritance to find the first ascending 
	    	 * class valid to create a View
	    	 */
	    	Class<?> c1 = Class.forName( controllersMap.get( type ) );
	    	controller = (IKronosController)c1.newInstance();
	    	if ( null != controller ){
	    		controller.addListener(this);
	    	}
	    }
	    catch( Exception ex){
	    	System.out.println("createController" + ex.toString());
	    }
	    
	    return controller;
	}


	@Override
	public void objectChanged(Object object, Event event) {
		notifyViews(object, event);
	}




}
