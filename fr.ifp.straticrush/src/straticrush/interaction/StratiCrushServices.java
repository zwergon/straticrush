package straticrush.interaction;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

import no.geosoft.cc.graphics.GWindow;
import fr.ifp.jdeform.continuousdeformation.Deformation;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.deformation.ChainMailDeformation;
import fr.ifp.jdeform.deformation.MassSpringNodeDeformation;
import fr.ifp.jdeform.deformation.ResetDeformation;
import fr.ifp.jdeform.deformation.TargetsSolverDeformation;
import fr.ifp.jdeform.deformation.TranslateDeformation;
import fr.ifp.jdeform.flexural.FlexuralSlip;
import fr.ifp.jdeform.geometric.VerticalShear;
import fr.ifp.kronosflow.controllers.ControllerEventList;
import fr.ifp.kronosflow.controllers.IControllerService;
import fr.ifp.kronosflow.model.EnumEventAction;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.newevents.IControllerEvent;

public class StratiCrushServices extends ViewNotifier implements IControllerService {
	
	
	private GWindow window;
	
	private Section section;
	
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
		registerDeformation("FlexuralSlip", FlexuralSlip.class );
	}
	
	public DeformationControllerCaller createDeformationCaller(){
	    return new DeformationControllerCaller( this ) ;
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
	public Section getSection() {
		return section;
	}
	
	public void setSection( Section section ){
		this.section = section;
	}

	@Override
	public void handleEvents( ControllerEventList eventList ) {
		
		//test if one Move Event to trigger view redraw.
		IControllerEvent<?> moveEvent = null;
		for( IControllerEvent<?> event : eventList ){
			if ( event.getEventAction() == EnumEventAction.MOVE ){
				moveEvent = event;
				break;
			}
		}
		
		if ( moveEvent != null ){
			ReadWriteLock lock = window.getLock();
			lock.writeLock().lock();
			try {
				notifyViews( moveEvent );

			}  finally {
				lock.writeLock().unlock();
			}
		}

	}




}
