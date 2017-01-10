package stratifx.application.interaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import fr.ifp.jdeform.controllers.DeformEvent;
import fr.ifp.jdeform.controllers.DeformationController;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.controllers.scene.Scene;
import fr.ifp.jdeform.controllers.scene.SceneBuilder;
import fr.ifp.jdeform.deformation.Deformation;
import fr.ifp.jdeform.deformation.DeformationFactory;
import fr.ifp.jdeform.deformation.DeformationFactory.Kind;
import fr.ifp.kronosflow.geoscheduler.Geoscheduler;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.model.FeatureGeolInterval;
import fr.ifp.kronosflow.model.FeatureInterval;
import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.factory.ModelFactory.GridType;
import fr.ifp.kronosflow.model.factory.ModelFactory.NatureType;
import fr.ifp.kronosflow.model.factory.SceneStyle;
import fr.ifp.kronosflow.model.filters.SvgExportPolylines;
import fr.ifp.kronosflow.model.geology.BoundaryFeature;
import fr.ifp.kronosflow.model.geology.StratigraphicEvent;
import fr.ifp.kronosflow.model.implicit.MeshPatch;
import fr.ifp.kronosflow.model.style.Style;
import fr.ifp.kronosflow.polyline.IPolyline;
import stratifx.application.GParameters;
import stratifx.application.StratiFXService;
import stratifx.application.manipulator.CompositeManipulator;
import stratifx.application.views.GPatchView;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.interaction.GInteraction;
import stratifx.canvas.interaction.GKeyEvent;
import stratifx.canvas.interaction.GMouseEvent;

public abstract class DeformationInteraction implements GInteraction {
	

	DeformationThread moveJob;
	
	Timer animationTimer;
	
	protected boolean isJobFinished = false;
	
	protected GScene    scene_;
			
	protected DeformationControllerCaller caller = null;
		
	/** horizons that may be a target for deformation ordered using straticolumn */
	protected List<IPolyline> potentialHorizonTargets = new ArrayList<IPolyline>();
		
	CompositeManipulator manipulator;
	
	
	public abstract CompositeManipulator createManipulator( 
			GScene gscene, 
			DeformationControllerCaller caller );
	

	public DeformationInteraction( GScene scene ){
		scene_ = scene;
		caller = (DeformationControllerCaller)StratiFXService.instance.createCaller("Deformation");
		manipulator = null;
	
	}
	
	
	public Geoscheduler getScheduler(){
		
		Section section = StratiFXService.instance.getSection();
		if ( section instanceof GeoschedulerSection ){
			return ((GeoschedulerSection)section).getGeoscheduler();
		}
		
		return null;
	}
	
	public void update(){
		manipulator.updateGraphics();
		scene_.refresh();
	}	
	
	public void end() {
		if ( moveJob != null ) {
			getCaller().publish();
			moveJob = null;
			
		}
		manipulator.deactivate();
		manipulator = null;
		scene_.refresh();	
		
		if ( null != animationTimer ){
			animationTimer.cancel();
			animationTimer = null;
		}
		
	}

	
	public DeformationControllerCaller getCaller(){
		return caller;
	}
	
	@Override
	public boolean start(GScene scene) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean stop(GScene scene) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public Patch getSelectedPatch(int x, int y ){
		GSegment selected = scene_.findSegment (x, y);
		if ( selected !=  null ){
			GObject gobject = selected.getOwner();
			while ( gobject != null ){
				if ( gobject instanceof GPatchView ){
					return ((GPatchView)gobject).getObject();
				}
				gobject = gobject.getParent();
			}
			
		}
			
		return null;
	}

	@Override
	public boolean mouseEvent ( GScene scene, GMouseEvent event ) {
		if ( scene != scene_ ){
			return false;
		}
		
		if ( moveJob != null  ){
			return false;
		}
		
		DeformationController controller = getCaller().getController();
		
		
		//can not interact during run of a simulation
		if ( controller.isRunning() ){
			return false;
		}

		switch (event.type) {
		case GMouseEvent.BUTTON_DOWN :
			
			
			if ( manipulator == null ){
				Patch patch = getSelectedPatch(event.x, event.y );
				if ( patch !=  null ){

					DeformationControllerCaller caller = getCaller();
					//caller.revert();

					caller.clear();
					caller.setScene( createScene(patch) );
					manipulator = createManipulator( scene, caller );
					manipulator.activate();
				}
			}
		
			
			if ( ( manipulator != null )  && manipulator.isActive() ){
				manipulator.onMousePress(event);
			}

			scene_.refresh();
			
			break;

		case GMouseEvent.BUTTON_DRAG :
		
			if ( ( null != manipulator ) && manipulator.isActive() ) {
				manipulator.onMouseMove(event);
				scene.refresh();
			}
			
			break;

		case GMouseEvent.BUTTON_UP :
			
			
			if ( ( null != manipulator ) && manipulator.isActive() ) {
				manipulator.onMouseRelease(event);
				
				CompositeManipulator compositeManipulator = (CompositeManipulator)manipulator;
				
				if ( !manipulator.isManipulating() ){

					if ( compositeManipulator.canDeform() ){	

						DeformationControllerCaller deformationCaller = getCaller();
						deformationCaller.hasPostDeform(false);
						deformationCaller.addItems( compositeManipulator.getItems() );
						deformationCaller.addRigidItems( compositeManipulator.getRigidItems());

						moveJob = new DeformationThread(deformationCaller);						
						moveJob.start();

						animationTimer = DeformationAnimation.start( this );

					}
					else {
						end();
					}
				}
				else {
					scene_.refresh();
				}
			}
			break;
					
		}
		
	 
		return true;

	}
	

	@Override
	public boolean keyEvent   ( GScene scene, GKeyEvent event ) {
		if ( event.type == GKeyEvent.KEY_PRESSED ) {
			switch( event.getKeyCode() ){
			case GKeyEvent.VK_ESCAPE:
				if ( moveJob != null ) {
					moveJob.cancel();
				}
				break;
			case GKeyEvent.VK_Z:
				if ( ( event.getKeyModifiers() == GKeyEvent.CTRL_MASK ) && 
					 ( moveJob == null ) ){	
					getCaller().revert();
					getCaller().publish();
					scene_.refresh();
				}
				break;
			
			case GKeyEvent.VK_Y:
				if ( ( event.getKeyModifiers() == GKeyEvent.CTRL_MASK ) && 
					 ( moveJob == null ) ){	
					getCaller().getEventList().add( new DeformEvent(getCaller().getScene()) );
					getCaller().publish();
					scene_.refresh();
				}
				break;
			case GKeyEvent.VK_P:
				if ( ( event.getKeyModifiers() == GKeyEvent.CTRL_MASK ) && 
					 ( moveJob == null ) ){	
					Section section = StratiFXService.instance.getSection();
					SvgExportPolylines exporter = new SvgExportPolylines("/tmp/section.svg");
					for( Patch patch : section.getPatchLibrary().getPatches() ){
						exporter.add(patch.getBorder(),null,50,null);
					}
					exporter.export();
				}
				break;
			default:
				break;	
			}
			
		}	
		
		return true;
	}

	private Scene createScene( Patch patch ){
		
		if ( ( patch.getPatchLibrary().getPatches().size() == 1 ) &&
			 ( patch instanceof MeshPatch ) ){
			return new Scene(patch);
		}
		
		
		return SceneBuilder.createDefaultScene(patch, GParameters.getStyle() );
	}

	
	private void getPotentialTargets( Patch patch ){
		for( KinObject object : patch.getChildren() ){
			if ( object instanceof FeatureGeolInterval ){
				FeatureInterval fgInterval = ((FeatureGeolInterval)object).getInterval();
				
				BoundaryFeature bf = fgInterval.getFeature();
				
				if ( bf instanceof StratigraphicEvent ){
					StratigraphicEvent se = (StratigraphicEvent)bf;
					potentialHorizonTargets.add( fgInterval );
				}
			}
		}
	}
	
	
	class DeformationThread extends Thread {
		
		DeformationControllerCaller deformationCaller;
		
		public DeformationThread( DeformationControllerCaller deformationCaller ) {
			this.deformationCaller = deformationCaller;
		}
		
		@Override
		public void run() {
			deformationCaller.apply();
		}
		
		public void cancel(){
			deformationCaller.cancel();
		}
	}
	
	
	protected Deformation createDeformation( String type ){

		Style style = GParameters.getStyle();

		SceneStyle sceneStyle = new SceneStyle(style);
		if ( 	type.equals("VerticalShear") ||
				type.equals("FlexuralSlip") ||
				type.equals("MovingLS")){
			sceneStyle.setGridType(GridType.LINE);
			sceneStyle.setNatureType(NatureType.EXPLICIT);
		}
		else {
			sceneStyle.setGridType(GridType.TRGL );
			sceneStyle.setNatureType(NatureType.EXPLICIT);
		}

		if ( 	type.equals("VerticalShear") ||
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



	

	
	
}
