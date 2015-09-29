package straticrush.interaction;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

import no.geosoft.cc.graphics.GWindow;
import fr.ifp.jdeform.continuousdeformation.Deformation;
import fr.ifp.jdeform.continuousdeformation.DeformationFactory;
import fr.ifp.jdeform.continuousdeformation.DeformationFactory.Kind;
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
import fr.ifp.kronosflow.model.style.Style;
import fr.ifp.kronosflow.model.style.StyleManager;
import fr.ifp.kronosflow.newevents.IControllerEvent;

public class StratiCrushServices extends ViewNotifier implements IControllerService {
	
	
	private GWindow window;
	
	private Section section;
	
	private static StratiCrushServices instance;
	
	
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
	
	protected StratiCrushServices() {	
		
		DeformationFactory factory = DeformationFactory.getInstance();
		factory.register( Kind.DEFORMATION, "Reset", ResetDeformation.class );
	}
	
	public DeformationControllerCaller createDeformationCaller(){
	    return new DeformationControllerCaller( this ) ;
	}
	
	public Deformation createDeformation( String type ){
		
		StyleManager styleManager = StyleManager.getInstance();
		Style style = styleManager.createStyle();
		if ( type.equals("Reset") ||
			 type.equals("ChainMail") ||
			 type.equals("MassSpring") ||
			 type.equals("Translate") ||
			 type.equals("VerticalShear") ||
			 type.equals("FlexuralSlip") ){
			style.setAttribute( Kind.DEFORMATION.toString(), type );
		}
		else {
			style.setAttribute( Kind.DEFORMATION.toString(), "TargetsSolverDeformation" );
			if ( type.equals("StaticFEASolver") ){
				style.setAttribute( Kind.SOLVER.toString(), "ImplicitStatic" );
			}
			else {
				style.setAttribute( Kind.SOLVER.toString(), "ImplicitDynamic" );
			}
		}
	
	
		Deformation deformation = (Deformation)DeformationFactory.getInstance().createDeformation(style);
		
		
		styleManager.deleteStyle(style);
		
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
