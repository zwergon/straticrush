package straticrush.interaction;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;

import straticrush.manipulator.AutoTargetsManipulator;
import straticrush.manipulator.CompositeManipulator;
import straticrush.manipulator.GPatchObject;
import straticrush.manipulator.IStratiManipulator;
import straticrush.view.PatchView;
import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GKeyEvent;
import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GTransformer;
import fr.ifp.jdeform.continuousdeformation.Deformation;
import fr.ifp.jdeform.continuousdeformation.IDeformationItem;
import fr.ifp.jdeform.deformation.DeformationController;
import fr.ifp.jdeform.deformation.TranslateDeformation;
import fr.ifp.jdeform.deformation.constraint.NodeMoveItem;
import fr.ifp.kronosflow.geology.BoundaryFeature;
import fr.ifp.kronosflow.geology.FaultFeature;
import fr.ifp.kronosflow.geology.StratigraphicEvent;
import fr.ifp.kronosflow.geometry.Vector2D;
import fr.ifp.kronosflow.model.CompositePatch;
import fr.ifp.kronosflow.model.FeatureGeolInterval;
import fr.ifp.kronosflow.model.IPolyline;
import fr.ifp.kronosflow.model.Interval;
import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.PolyLineGeometry;
import fr.ifp.kronosflow.model.algo.ComputeBloc;

public abstract class DeformationInteraction implements GInteraction {
	
	static int refreshDelay = 500;
	
	protected GScene    scene_;
	
	protected GPatchObject interaction;
	
	protected DeformationController deformationController = null;
	
	protected TranslateDeformation translateDeformation = null;

	protected Patch selectedComposite;
	
	protected PatchInterval  selectedHorizon;
	
	protected PatchInterval  selectedFault;
	
	protected List<Patch> surroundedComposites = new ArrayList<Patch>();
	
	/** horizons that may be a target for deformation ordered using straticolumn */
	protected List<IPolyline> potentialHorizonTargets = new ArrayList<IPolyline>();
	
	protected int       x0_, y0_;
	
	CompositeManipulator manipulator;
	
	
	public abstract CompositeManipulator createManipulator( 
			GScene scene, 
			Patch selectedComposite, 
			List<Patch> surroundedComposites );
	

	@SuppressWarnings("unchecked")
	public DeformationInteraction( GScene scene, String type ){
		scene_ = scene;
		
		selectedHorizon = null;
		selectedFault = null;
		
		deformationController = StratiCrushServices.getInstance().createDeformationController();
		
		translateDeformation =  new TranslateDeformation();
		
		
		 // Create a graphic node for holding the interaction graphics
	    interaction = new GPatchObject();    
	    
	}

	@Override
	public void event(GScene scene, GMouseEvent event) {
		if ( scene != scene_ ){
			return;
		}
		
		//can not interact during run of a simulation
		if ( deformationController.getState() == Deformation.DEFORMING  ){
			return;
		}

		switch (event.type) {
		case GMouseEvent.BUTTON1_DOWN :
			
			GSegment selected = scene.findSegment (event.x, event.y);
			if ( selected !=  null ){
				GObject gobject = selected.getOwner();
				if ( gobject instanceof PatchView ){
				
					Patch patch = ((PatchView)gobject).getObject();
				
					createSelectedAndSurrounded(patch);
					
					manipulator = createManipulator( scene, selectedComposite, surroundedComposites );
					if ( !manipulator.isActive() ){
						manipulator.activate();
					}
					
					manipulator.onMousePress(event);
					
					scene.refresh();
					
				}
			}

			x0_ = event.x;
			y0_ = event.y;

			break;

		case GMouseEvent.BUTTON1_DRAG :
		
			if ( ( null != manipulator ) && manipulator.isActive() ) {
				
				translateComposite(scene, event);
				
				manipulator.onMouseMove(event);
				

				scene.refresh();

			}
			x0_ = event.x;
			y0_ = event.y;
			break;

		case GMouseEvent.BUTTON1_UP :
			
			
			if ( ( null != manipulator ) && manipulator.isActive() ) {
				manipulator.onMouseRelease(event);
				
				deformationController.clear();
				deformationController.setPatch(selectedComposite); 

				for( IDeformationItem item : ((CompositeManipulator)manipulator).getItems() ){
					deformationController.addDeformationItem( item );
				}
				deformationController.prepare();
				deformationController.move();
			}
			
			
			clearSolver();
			
/*			if ( null != selectedHorizon )  {
				
				deformationController.clear();
				deformationController.setPatch(selectedComposite); 
				deformationController.addDeformationItem( item );
				deformationController.prepare();
				
				//if solver can be launched
				if	 ( deformationController.getState() == Deformation.PREPARED )  {

					Job job = new Job("Move") {

						@Override
						protected IStatus run(IProgressMonitor monitor) {
							deformationController.move();
							return Status.OK_STATUS;
						}
					};
					job.schedule();

					timer = new Annimation(scene,  deformationController );

					Display.getDefault().timerExec(refreshDelay, timer);

				}
				else {
					clearSolver();
				}
			}*/
			break;
			
		case GMouseEvent.ABORT:
			/*flattenController.dispose();
			scene_.remove(interaction_);*/
			break;
		}
		
		

	}
	

	@Override
	public void keyEvent( GKeyEvent event ) {
	}

	
	
	
	/**
	 * retrieves the {@link PatchInterval} of type c that is nearest of ori.
	 * @see findHorizonFeature
	 * @see findFaultFeature 
	 */
	protected <T> PatchInterval findFeature( double[] ori, Class<T> c ) {
		
		PatchInterval interval = null;
		double minDist = Double.POSITIVE_INFINITY;
		
		for( KinObject object : selectedComposite.getChildren() ){
			if ( object instanceof FeatureGeolInterval ){
				Interval fgInterval = ((FeatureGeolInterval)object).getInterval();
				if ( c.isInstance(fgInterval.getFeature()) ){
					PolyLineGeometry pgeom = new PolyLineGeometry(fgInterval);
					
					double dist = pgeom.minimalDistance( ori );
					if ( dist < minDist ){
						interval = (PatchInterval)object;
						minDist = dist;
					}
				}
			}
		}
		
		return interval;
	}
	
	protected PatchInterval findHorizonFeature( double[] ori ){
		return findFeature( ori, StratigraphicEvent.class );
	}
	
	protected PatchInterval findFaultFeature( double[] ori ){
		return findFeature( ori, FaultFeature.class );
	}
	
	
	protected void translateComposite(GScene scene, GMouseEvent event) {
	

		GTransformer transformer = scene.getTransformer();

		int[] oldPos = new int[2];
		oldPos[0] = x0_;  oldPos[1] = y0_;
		int[] newPos = new int[2];
		newPos[0] = event.x;  newPos[1] = event.y;
		double[] d_pos1 = transformer.deviceToWorld(oldPos);
		double[] d_pos2 = transformer.deviceToWorld(newPos);
		
		translateDeformation.setTranslation( Vector2D.substract(d_pos2, d_pos1) );
		translateDeformation.setPatch(selectedComposite);
		translateDeformation.deform();
	}
	
	
	
	protected void createSelectedAndSurrounded(Patch patch) {
		PatchLibrary library = patch.getPatchLibrary();
		List<Patch> availablePatches = library.getPatches();
		
		ComputeBloc computeBloc = new ComputeBloc(library);
		
		CompositePatch composite = (CompositePatch)computeBloc.getBloc( patch );
		selectedComposite = composite;
		
		availablePatches.removeAll( composite.getPatchs() );
		while( !availablePatches.isEmpty() ){
				composite = (CompositePatch)computeBloc.getBloc( availablePatches.get(0) );
				if ( null != composite ){
					surroundedComposites.add( composite );
					getPotentialTargets(composite);
				}
				availablePatches.removeAll( composite.getPatchs() );
		}
	}
	
	
	private void getPotentialTargets( Patch patch ){
		for( KinObject object : patch.getChildren() ){
			if ( object instanceof FeatureGeolInterval ){
				Interval fgInterval = ((FeatureGeolInterval)object).getInterval();
				
				BoundaryFeature bf = fgInterval.getFeature();
				
				if ( bf instanceof StratigraphicEvent ){
					StratigraphicEvent se = (StratigraphicEvent)bf;
					potentialHorizonTargets.add( fgInterval );
				}
			}
		}
		
		
	}
	
	
	class Annimation implements Runnable {

		DeformationController controller;
		GScene scene;
		
		public Annimation( GScene scene, DeformationController controller ){
			this.scene = scene;
			this.controller = controller;
			
		}
		
		@Override
		public void run() {
			
			int newState = controller.getState();
					
			if ( ( newState == Deformation.DEFORMING )  ){
				scene_.refresh();
				Display.getDefault().timerExec(refreshDelay, this);
			}
		
			if ( newState == Deformation.DEFORMED){
				clearSolver();
			}
				
			if ( ( newState == Deformation.PREPARING ) || ( newState == Deformation.PREPARED ) ){
				Display.getDefault().timerExec(refreshDelay, this);
			}
			
		}
	}
	
	
	
	private void clearSolver() {

		manipulator.deactivate();

		deformationController.clear();

		selectedComposite.remove();
		for( Patch surround : surroundedComposites ){
			surround.remove();
		}

		surroundedComposites.clear();
		selectedComposite = null;
		selectedHorizon = null;
		scene_.refresh();
	}

}
