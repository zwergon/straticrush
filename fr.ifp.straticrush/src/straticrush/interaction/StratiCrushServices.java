package straticrush.interaction;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

import org.eclipse.swt.widgets.Display;

import no.geosoft.cc.graphics.GWindow;
import no.geosoft.cc.utils.GParameters;
import fr.ifp.jdeform.continuousdeformation.Deformation;
import fr.ifp.jdeform.continuousdeformation.DeformationFactory;
import fr.ifp.jdeform.continuousdeformation.DeformationFactory.Kind;
import fr.ifp.jdeform.continuousdeformation.DeformationStyle;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.deformation.ResetDeformation;
import fr.ifp.kronosflow.controllers.ControllerEventList;
import fr.ifp.kronosflow.controllers.IControllerService;
import fr.ifp.kronosflow.controllers.handlers.RefreshSummary;
import fr.ifp.kronosflow.model.EnumEventAction;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.factory.ModelFactory.GridType;
import fr.ifp.kronosflow.model.factory.ModelFactory.NatureType;
import fr.ifp.kronosflow.model.factory.SceneStyle;
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
		
		
		Style style = GParameters.getStyle();
		
		SceneStyle sceneStyle = new SceneStyle(style);
		
		if ( type.equals("Reset") ||
			 type.equals("Translate") ||
			 type.equals("VerticalShear") ||
			 type.equals("FlexuralSlip") ||
			 type.equals("MovingLS")){
			style.setAttribute( Kind.DEFORMATION.toString(), type );
			sceneStyle.setGridType(GridType.LINE);
			sceneStyle.setNatureType(NatureType.EXPLICIT);
		}
		else {
			sceneStyle.setGridType(GridType.TRGL );
			sceneStyle.setNatureType(NatureType.EXPLICIT);
			
			if ( !type.equals("ChainMail") && !type.equals("MassSpring") ){
				style.setAttribute( Kind.DEFORMATION.toString(), "TargetsSolverDeformation" );
				if ( type.equals("StaticFEASolver") ){
					style.setAttribute( Kind.SOLVER.toString(), "ImplicitStatic" );
				}
				else {
					style.setAttribute( Kind.SOLVER.toString(), "ImplicitDynamic" );
				}
			}
			else {
				style.setAttribute( Kind.DEFORMATION.toString(), type );
			}
		}
	
		Deformation deformation = (Deformation)DeformationFactory.getInstance().createDeformation(style);
		
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
		
		Map< EnumEventAction, IControllerEvent<?> > summary = new HashMap<EnumEventAction, IControllerEvent<?>>();
		
		for( IControllerEvent<?> event : eventList ){
			summary.put(  event.getEventAction(), event );
		}
		
		for( IControllerEvent<?> event : summary.values() ){
			notifyViews(event);
		}
		
	}

	@Override
	public void handleEvents( 
			ControllerEventList eventList, 
			boolean forceRefresh ) {
		handleEvents(eventList);
	}
	

}
