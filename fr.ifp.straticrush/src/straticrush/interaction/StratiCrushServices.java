package straticrush.interaction;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

import no.geosoft.cc.graphics.GWindow;
import fr.ifp.jdeform.continuousdeformation.Deformation;
import fr.ifp.jdeform.continuousdeformation.IDeformation;
import fr.ifp.jdeform.deformation.ChainMailDeformation;
import fr.ifp.jdeform.deformation.DeformationController;
import fr.ifp.jdeform.deformation.MassSpringNodeDeformation;
import fr.ifp.jdeform.deformation.ResetDeformation;
import fr.ifp.jdeform.deformation.TargetsSolverDeformation;
import fr.ifp.jdeform.deformation.TranslateDeformation;
import fr.ifp.jdeform.geometric.VerticalShear;
import fr.ifp.kronosflow.controller.ICommandController;
import fr.ifp.kronosflow.controller.IControllerEvent;
import fr.ifp.kronosflow.controller.IEventListener;

public class StratiCrushServices extends ViewNotifier implements IEventListener {
	
	
	private GWindow window;
	
	private static StratiCrushServices instance;
	private Map<String, String> deformationMap = new HashMap<String, String>(); //dictionnary for IController creation
	
	
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
	
	
	void registerDeformation( String type, Class<?> view_class ){
		deformationMap.put( type, view_class.getCanonicalName() );
	}
	
	
	protected StratiCrushServices() {	
		registerDeformation("Translate", TranslateDeformation.class);
		registerDeformation("ChainMail", ChainMailDeformation.class);
		registerDeformation("MassSpring", MassSpringNodeDeformation.class );
		registerDeformation("Reset", ResetDeformation.class );
		registerDeformation("VerticalShear", VerticalShear.class );
		registerDeformation("DynamicFEASolver", TargetsSolverDeformation.class );
		registerDeformation("StaticFEASolver", TargetsSolverDeformation.class );
	}
	
	
	public DeformationController createDeformationController(){
		
		DeformationController controller = new DeformationController();
		controller.addListener(this);
		
	    return controller;
	}
	
	
	
	@SuppressWarnings("rawtypes")
	public Deformation createDeformation( String type ){

		Deformation deformation = null;
	    try {
	    	/*
	    	 * TODO go through class inheritance to find the first ascending 
	    	 * class valid to create a View
	    	 */
	    	Class<?> c1 = Class.forName( deformationMap.get( type ) );
	    	deformation = (Deformation)c1.newInstance();
	    	
	    	
	    }
	    catch( Exception ex){
	    	System.out.println("exception createDeformation" + ex.toString());
	    }
	    
	    return deformation;
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
