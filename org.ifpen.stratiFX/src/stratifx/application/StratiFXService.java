package stratifx.application;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.deformation.Deformation;
import fr.ifp.jdeform.deformation.DeformationFactory;
import fr.ifp.jdeform.deformation.DeformationFactory.Kind;
import fr.ifp.kronosflow.controllers.ControllerEventList;
import fr.ifp.kronosflow.controllers.IControllerService;
import fr.ifp.kronosflow.controllers.events.EnumEventAction;
import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.controllers.units.PatchDeleteEvent;
import fr.ifp.kronosflow.controllers.units.UnitRemovedItem;
import fr.ifp.kronosflow.extensions.IExtension;
import fr.ifp.kronosflow.extensions.ray.RayExtension;
import fr.ifp.kronosflow.geometry.RectD;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.geoscheduler.IGeoschedulerCaller;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.explicit.ExplicitPolyLine;
import fr.ifp.kronosflow.model.factory.ModelFactory.ComplexityType;
import fr.ifp.kronosflow.model.factory.ModelFactory.GridType;
import fr.ifp.kronosflow.model.factory.ModelFactory.NatureType;
import fr.ifp.kronosflow.model.factory.SceneStyle;
import fr.ifp.kronosflow.model.filters.SectionFactory;
import fr.ifp.kronosflow.model.style.Style;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.property.IPropertyAccessor;
import fr.ifp.kronosflow.property.image.ImagePropertyAccessor;
import fr.ifp.kronosflow.utils.KronosContext;
import fr.ifp.kronosflow.utils.LOGGER;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import stratifx.application.plot.GFXScene;
import stratifx.application.plot.PlotController;

public class StratiFXService implements IUIController, IControllerService {
	
	Section section;
	
	private Stage primaryStage;
	
	Map<IUIController.Type, IUIController> controllers;
	
	static public StratiFXService instance;
	
	static {
		instance = new StratiFXService();
	}
	
	protected StratiFXService() {
		controllers = new HashMap<IUIController.Type, IUIController>();
		
		KronosContext.registerClass( Section.class,  GeoschedulerSection.class );
		KronosContext.registerClass( PolyLine.class, ExplicitPolyLine.class );
		KronosContext.registerClass( IExtension.class, RayExtension.class );
		KronosContext.registerClass( IPropertyAccessor.class, ImagePropertyAccessor.class );
	}
	
	public void setPrimaryStage( Stage primaryStage ){
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle( "StratiFX" );
	}
	
	public Stage getPrimaryStage(){
		return primaryStage;
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
		switch(action.getType()){
		case UIAction.Open:
			return handleOpen();
		}	
		return false;
	}

	

	private boolean handleOpen() {
		
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(primaryStage);
		if ( file == null ){
			return false;
		}
		
		
		String filename = file.getAbsolutePath();

		String basename = filename.substring(0, filename.lastIndexOf('.'));

		LOGGER.debug("load " + basename , this.getClass() );

		section = KronosContext.make(Section.class);
		section.setName(basename);
		
		SceneStyle sceneStyle = new SceneStyle(section.getStyle());
		sceneStyle.setNatureType(NatureType.EXPLICIT);
		sceneStyle.setGridType(GridType.TRGL);
		sceneStyle.setComplexityType(ComplexityType.SINGLE);

		PatchLibrary patchLib = section.getPatchLibrary();

		Map<String,String> unitMap = SectionFactory.createBorders( filename, section );
		
		File f = new File(basename + ".xml");
		if(f.exists() && !f.isDirectory()) { 
			SectionFactory.createDummyUnit( basename + ".xml", section, unitMap);
		}
		else {
			f = new File(basename + ".unit");
			if(f.exists() && !f.isDirectory()) { 
				SectionFactory.createDummyUnit( basename + ".unit", section, unitMap);
			}
		}

		PlotController plot = (PlotController)controllers.get(IUIController.Type.PLOT);
		
		GFXScene gfxScene = plot.getGFXScene();
		gfxScene.destroyAll();

		// Create a graphic object
		for( Patch patch : patchLib.getPatches() ){
			gfxScene.createView( patch );   
		}

		gfxScene.createView( patchLib.getPaleobathymetry() );

		RectD bbox = patchLib.getBoundingBox();
		plot.setWorldExtent( bbox.left, bbox.top, bbox.width(), bbox.height());
		
		
		gfxScene.refresh();
		
		return true;
	}
	


	public IGeoschedulerCaller<?>  createCaller( String type ){
		if ( type.equals("Deformation") ){
			return new DeformationControllerCaller( this ) ;
		}
		/*else if ( type.equals("RemoveUnit") ){
			return new RemoveUnitCaller(this);
		}*/
		
	    return null;
	}

	public Section getSection() {
		return section;
	}

	@Override
	public void handleEvents(ControllerEventList eventList) {
		
		PlotController plot = (PlotController)controllers.get(IUIController.Type.PLOT);
		
		GFXScene gfxScene = plot.getGFXScene();

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
					gfxScene.destroyViews(patch);
				}
			}
			else {
				gfxScene.notifyViews(event);
			}
		}


	}

	@Override
	public void preHandle(ControllerEventList eventList) {
		// TODO Auto-generated method stub
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
