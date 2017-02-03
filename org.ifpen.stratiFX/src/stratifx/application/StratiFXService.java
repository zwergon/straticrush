package stratifx.application;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ifp.jdeform.controllers.events.DeformEvent;
import fr.ifp.jdeform.controllers.events.RecomputeAllPatchsEvent;
import fr.ifp.jdeform.controllers.events.UndoDeformationEvent;
import fr.ifp.kronosflow.controllers.ControllerEventList;
import fr.ifp.kronosflow.controllers.IControllerService;
import fr.ifp.kronosflow.controllers.events.EnumEventAction;
import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.controllers.property.PropertyController;
import fr.ifp.kronosflow.controllers.property.PropertyControllerCaller;
import fr.ifp.kronosflow.controllers.property.PropertyEvent;
import fr.ifp.kronosflow.controllers.units.PatchAddEvent;
import fr.ifp.kronosflow.controllers.units.PatchDeleteEvent;
import fr.ifp.kronosflow.controllers.units.UnitRemovedItem;
import fr.ifp.kronosflow.extensions.IExtension;
import fr.ifp.kronosflow.extensions.ray.RayExtension;
import fr.ifp.kronosflow.geometry.RectD;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.explicit.ExplicitPolyLine;
import fr.ifp.kronosflow.model.factory.ModelFactory.ComplexityType;
import fr.ifp.kronosflow.model.factory.ModelFactory.GridType;
import fr.ifp.kronosflow.model.factory.ModelFactory.NatureType;
import fr.ifp.kronosflow.model.factory.SceneStyle;
import fr.ifp.kronosflow.model.filters.SectionFactory;
import fr.ifp.kronosflow.model.property.ImagePropertyAccessor;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.property.IPropertyAccessor;
import fr.ifp.kronosflow.utils.KronosContext;
import fr.ifp.kronosflow.utils.LOGGER;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import stratifx.application.plot.GFXScene;
import stratifx.application.plot.PlotController;
import stratifx.application.properties.PorosityComputer;
import stratifx.application.properties.PropertiesUIAction;
import stratifx.application.properties.XYPropertyComputer;
import stratifx.application.views.GView;

public class StratiFXService implements IUIController, IControllerService {
	
	GeoschedulerSection section;
	
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
		
		PropertyController.registerBuilder("XY", new XYPropertyComputer.Builder() );
		PropertyController.registerBuilder("Porosity", new PorosityComputer.Builder());
		//PropertyController.registerBuilder("Poisson", new PoissonComputer.Builder());
		//PropertyController.registerBuilder("Surface", new SurfacePropertyComputer.Builder() );
		
		//PropertyController.registerBuilder("Strate Orientation", new StrateOrientationComputer.Builder() );
		//PropertyController.registerBuilder("SolidSurface", new SolidSurfaceComputer.Builder() );
		
		
	}
	
	public Section getSection() {
		return section;
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
	
	public void removeController(Type type) {
		controllers.remove(type);
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
		
		case UIAction.Properties:
			return handleProperties( (PropertiesUIAction) action );
		}
		
		return false;
	}

	

	private boolean handleProperties(PropertiesUIAction action) {
		
		PropertyControllerCaller caller = new PropertyControllerCaller( this );
		caller.setPropertyKey( action.getProperty() );
		caller.applyAndNotify();

		PlotController plot = (PlotController)controllers.get(IUIController.Type.PLOT);

		GFXScene gfxScene = plot.getGFXScene();
		gfxScene.refresh();

		return true;

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

		section = new GeoschedulerSection();
		section.setName(basename);
		
		SceneStyle sceneStyle = new SceneStyle(section.getStyle());
		sceneStyle.setNatureType(NatureType.EXPLICIT);
		sceneStyle.setGridType(GridType.LINE);
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
		bbox.inset(-bbox.width()/10., -bbox.height()/10.);
		plot.setWorldExtent( bbox.left, bbox.top, bbox.width(), bbox.height());
		
		
		gfxScene.refresh();
		
		return true;
	}
	

	@Override
	public void preHandle(ControllerEventList eventList) {
		// TODO Auto-generated method stub
	}

	@Override
	public void handleEvents(ControllerEventList eventList) {
		
		PlotController plot = (PlotController)controllers.get(IUIController.Type.PLOT);
		
		GFXScene gfxScene = plot.getGFXScene();

	
		Map< EnumEventAction, IControllerEvent<?> > summary = new HashMap<EnumEventAction, IControllerEvent<?>>();

		for( IControllerEvent<?> event : eventList ){
			summary.put(  event.getEventAction(), event );
		}

		for( IControllerEvent<?> event : summary.values() ){

			LOGGER.debug("handle "+ event.getClass().getSimpleName(), getClass());
			if ( event instanceof PatchDeleteEvent ){
				PatchDeleteEvent pde = (PatchDeleteEvent)event;
				UnitRemovedItem removeItem = (UnitRemovedItem)pde.getObject();
				for ( Patch patch : removeItem.getPatches() ){
					gfxScene.destroyViews(patch);
				}
			}
			else if ( event instanceof PatchAddEvent ){
				PatchAddEvent pae = (PatchAddEvent)event;
				for( Patch patch : pae.getObject() ){
					GView view = gfxScene.createView(patch);
					view.redraw();
				}
			}
			else if ( ( event instanceof DeformEvent ) ||
			 		  ( event instanceof UndoDeformationEvent ) ||
					  ( event instanceof PropertyEvent ) ){
				gfxScene.notifyViews(event);
			}
			
		}


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
