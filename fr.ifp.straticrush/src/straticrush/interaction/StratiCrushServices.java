package straticrush.interaction;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

import no.geosoft.cc.graphics.GWindow;
import fr.ifp.jdeform.deformation.ChainMailDeformation;
import fr.ifp.jdeform.deformation.DeformationController;
import fr.ifp.jdeform.deformation.MassSpringNodeDeformation;
import fr.ifp.jdeform.deformation.ResetDeformation;
import fr.ifp.jdeform.deformation.TranslateDeformation;
import fr.ifp.kronosflow.controller.ICommandController;
import fr.ifp.kronosflow.controller.IControllerEvent;
import fr.ifp.kronosflow.controller.IEventListener;

public class StratiCrushServices extends ViewNotifier implements IEventListener {
	
	
	private GWindow window;
	
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
	
	public void setWindow( GWindow window ){
		this.window = window;
	}
	
	
	void registerController( String type, Class<?> view_class ){
		controllersMap.put( type, view_class.getCanonicalName() );
	}
	
	
	protected StratiCrushServices() {	
		registerController("Translate", DeformationController.class);
		registerController("ChainMail", DeformationController.class);
		registerController("MassSpring", DeformationController.class );
		registerController("Reset", DeformationController.class );
		registerController("StaticFEASolver", DeformationController.class );
		registerController("DynamicFEASolver", DeformationController.class );
	}
	
	
	@SuppressWarnings("rawtypes")
	public ICommandController createController( String type ){

		ICommandController controller = null;
	    try {
	    	/*
	    	 * TODO go through class inheritance to find the first ascending 
	    	 * class valid to create a View
	    	 */
	    	Class<?> c1 = Class.forName( controllersMap.get( type ) );
	    	controller = (ICommandController)c1.newInstance();
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
	public void objectChanged(IControllerEvent<?> event) {	
		ReadWriteLock lock = window.getLock();
		lock.writeLock().lock();
        try {
        	notifyViews( event );
        }  finally {
            lock.writeLock().unlock();
        }
	}




}
