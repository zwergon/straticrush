package straticrush.interaction;

import java.util.HashMap;
import java.util.Map;

import no.geosoft.cc.utils.GParameters;
import straticrush.view.StratiWindow;
import fr.ifp.jdeform.continuousdeformation.Deformation;
import fr.ifp.jdeform.continuousdeformation.DeformationFactory;
import fr.ifp.jdeform.continuousdeformation.DeformationFactory.Kind;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.deformation.ResetDeformation;
import fr.ifp.kronosflow.controllers.ControllerEventList;
import fr.ifp.kronosflow.controllers.IControllerService;
import fr.ifp.kronosflow.model.EnumEventAction;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.factory.ModelFactory.GridType;
import fr.ifp.kronosflow.model.factory.ModelFactory.NatureType;
import fr.ifp.kronosflow.model.factory.SceneStyle;
import fr.ifp.kronosflow.model.style.Style;
import fr.ifp.kronosflow.newevents.IControllerEvent;
import fr.ifp.kronosflow.utils.LOGGER;

public class StratiCrushServices  implements IControllerService {
	
	private StratiWindow window;
	
	private Section section;
	
	private static StratiCrushServices instance;
	
	ViewNotifier notifier;
	
	
	static {
		instance = null;	
	}
	
	public static StratiCrushServices getInstance() {
		if ( null == instance ){
			instance = new StratiCrushServices();
		}		
		return instance;
	}
	
	public void setWindow( StratiWindow window ){
		this.window = window;
	}
	
	public StratiWindow getWindow(){
		return window;
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
			 type.equals("VerticalShear") ||
			 type.equals("FlexuralSlip") ||
			 type.equals("MovingLS")){
			sceneStyle.setGridType(GridType.LINE);
			sceneStyle.setNatureType(NatureType.EXPLICIT);
		}
		else {
			sceneStyle.setGridType(GridType.GRID2D );
			sceneStyle.setNatureType(NatureType.IMPLICIT);
		}
		
		if ( type.equals("Reset") ||
		     type.equals("VerticalShear") ||
			 type.equals("FlexuralSlip") ||
			 type.equals("MovingLS") ||
			 type.equals("ChainMail") ||
			 type.equals("MassSpring") ){
			style.setAttribute( Kind.DEFORMATION.toString(), type );
		}
		else if ( 
				 type.equals("Dynamic") ||
			     type.equals("Static") ||
				 type.equals("StaticLS") ||
				 type.equals("FEM2D")  ) {
			style.setAttribute( Kind.DEFORMATION.toString(), "NodeLinksDeformation" );
			style.setAttribute( Kind.SOLVER.toString(), type );
		}
		else if ( type.equals("Thermal") ||
				  type.equals("Decompaction") ){
			style.setAttribute( Kind.DEFORMATION.toString(), "DilatationDeformation" );
			style.setAttribute( "DilatationType", type );
		}
		else {
			assert false : "This deformation parameter is not handled";
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
		this.notifier = new ViewNotifier();
	}
	
	@Override
	public void handleEvents( ControllerEventList eventList ) {
		
		if ( null == notifier ){
			return;
		}
		
		//test if one Move Event to trigger view redraw.
		
		Map< EnumEventAction, IControllerEvent<?> > summary = new HashMap<EnumEventAction, IControllerEvent<?>>();
		
		for( IControllerEvent<?> event : eventList ){
			summary.put(  event.getEventAction(), event );
		}
		
		for( IControllerEvent<?> event : summary.values() ){
			notifier.notifyViews(event);
		}
		
	}

	@Override
	public void handleEvents( 
			ControllerEventList eventList, 
			boolean forceRefresh ) {
		handleEvents(eventList);
	}

	public void removeListener(IViewListener view) {
		if ( null != notifier ){
			notifier.removeListener(view);
		}
	}

	public void addListener(IViewListener view) {
		if ( null != notifier ){
			notifier.addListener(view);
		}	
	}
	
}
