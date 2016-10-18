package straticrush.interaction;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.geosoft.cc.utils.GParameters;
import straticrush.caller.RemoveUnitCaller;
import straticrush.view.Plot;
import straticrush.view.StratiWindow;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.deformation.Deformation;
import fr.ifp.jdeform.deformation.DeformationFactory;
import fr.ifp.jdeform.deformation.DeformationFactory.Kind;
import fr.ifp.jdeform.deformations.ResetDeformation;
import fr.ifp.kronosflow.controllers.ControllerEventList;
import fr.ifp.kronosflow.controllers.IControllerService;
import fr.ifp.kronosflow.controllers.events.EnumEventAction;
import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.controllers.units.PatchDeleteEvent;
import fr.ifp.kronosflow.controllers.units.UnitRemovedItem;
import fr.ifp.kronosflow.geoscheduler.IGeoschedulerCaller;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.factory.ModelFactory.GridType;
import fr.ifp.kronosflow.model.factory.ModelFactory.NatureType;
import fr.ifp.kronosflow.model.factory.SceneStyle;
import fr.ifp.kronosflow.model.style.Style;

public class StratiCrushServices  implements IControllerService {
	
	private StratiWindow window;
	
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
	
	public IGeoschedulerCaller<?>  createCaller( String type ){
		if ( type.equals("Deformation") ){
			return new DeformationControllerCaller( this ) ;
		}
		else if ( type.equals("RemoveUnit") ){
			return new RemoveUnitCaller(this);
		}
		
	    return null;
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
			sceneStyle.setGridType(GridType.TRGL );
			sceneStyle.setNatureType(NatureType.EXPLICIT);
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
	}
	
	@Override
	public void handleEvents( ControllerEventList eventList ) {
		
		Plot plot = window.getPlot();
		
		//test if one Move Event to trigger view redraw.
		
		Map< EnumEventAction, IControllerEvent<?> > summary = new HashMap<EnumEventAction, IControllerEvent<?>>();
		
		for( IControllerEvent<?> event : eventList ){
			summary.put(  event.getEventAction(), event );
		}
		
		for( IControllerEvent<?> event : summary.values() ){
			
			if ( event instanceof PatchDeleteEvent ){
				PatchDeleteEvent pde = (PatchDeleteEvent)event;
				UnitRemovedItem removeItem = (UnitRemovedItem)pde.getObject();
				for ( Patch patch : removeItem.getPatches() ){
					plot.destroyViews(patch);
				}
			}
			else {
				plot.notifyViews(event);
			}
		}
		
	}

	@Override
	public void preHandle(ControllerEventList eventList) {
		handleEvents(eventList);
	}

	@Override
	public List<String> deactivateActiveManipulators() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void activateManipulators(Collection<String> handlerIds) {
		// TODO Auto-generated method stub
		
	}

	
	
}
