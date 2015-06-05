package straticrush.interaction;

import no.geosoft.cc.graphics.GKeyEvent;
import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import straticrush.view.PatchView;
import fr.ifp.jdeform.continuousdeformation.ContinuousDeformation.State;
import fr.ifp.jdeform.deformation.TargetsDeformationController;
import fr.ifp.jdeform.deformation.TargetsSolverDeformation;
import fr.ifp.jdeform.deformation.constraint.LinePairingItem;
import fr.ifp.kronosflow.events.DeformEvent;
import fr.ifp.kronosflow.geology.Paleobathymetry;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.LinePointPair;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PolyLine;
import fr.ifp.kronosflow.model.algo.LineIntersection;

public class FlattenInteraction extends SolverInteraction {
	
	Runnable timer;
	
	LineIntersection lineInter = null;
	
	static int refreshDelay = 500;
	
	
	class Annimation implements Runnable {

		TargetsDeformationController<Patch> controller;
		GScene scene;
		
		public Annimation( GScene scene, TargetsDeformationController<Patch> controller ){
			this.scene = scene;
			this.controller = controller;
			
		}
		
		@Override
		public void run() {
			
			State newState = controller.getState();
					
			if ( ( newState == State.DEFORMING )  ){
				scene_.refresh();
				Display.getDefault().timerExec(refreshDelay, this);
			}
		
			if ( newState == State.DEFORMED){
				clearSolver();
			}
				
			if ( ( newState == State.PREPARING ) || ( newState == State.PREPARED ) ){
				Display.getDefault().timerExec(refreshDelay, this);
			}
			
		}
	}
	
	
	

	public FlattenInteraction( GScene scene, String type ){
		super( scene, type );
		solverController.setDeformation( new TargetsSolverDeformation( type ) );
	}
	

	@Override
	public void event(GScene scene, GMouseEvent event) {
		if ( scene != scene_ ){
			return;
		}
		
		//can not interact during run of a simulation
		if ( solverController.getState() == State.DEFORMING  ){
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
					
					double[] wc = scene.getTransformer().deviceToWorld(event.x, event.y);
					selectedHorizon = findHorizonFeature( wc );
					selectedFault   = findFaultFeature( wc );

					if ( ( null != selectedHorizon ) || ( null != selectedFault ) ){
						scene_.add(interaction_);
						for( Patch p : surroundedComposites ){
							interaction_.addOutline( p, true );
						}
						interaction_.addOutline( selectedComposite, false );
					}
					
					if ( null != selectedHorizon ){
						selectedHorizon.getInterval().createExtension();
						
						Paleobathymetry bathy = selectedHorizon.getPatchLibrary().getPaleobathymetry();
						lineInter = new LineIntersection( bathy.getPolyline() );

						interaction_.addInterval( selectedHorizon );
					}
					
					if ( null != selectedFault ){
						interaction_.addInterval( selectedFault );
					}
					
					scene.refresh();
					
					
					
					StratiCrushServices.getInstance().addListener(interaction_);
				}
			}

			x0_ = event.x;
			y0_ = event.y;

			break;

		case GMouseEvent.BUTTON1_DRAG :
			
			interaction_.clearLines();
			if ( null != selectedHorizon ) {
				
				translateComposite(scene, event);
				
				solverController.clear();
				LinePointPair I = lineInter.getFirstIntersection(selectedHorizon.getInterval());
				if ( null != I ) {
					
					solverController.setPatch(selectedComposite); 
					
					LinePairingItem item = new LinePairingItem( selectedComposite, selectedHorizon, I);
					
					Paleobathymetry bathy = selectedHorizon.getPatchLibrary().getPaleobathymetry();
					solverController.addDeformationItem( item );
					
					
					solverController.computeTargets();
				
					for ( PolyLine line : solverController.getTargetLine()){
						interaction_.addLine( line );
					}
					//interaction_.addLine( solverController.getDebugLine() );
					
				}
				
				interaction_.draw();;
				scene.refresh();

			}
			x0_ = event.x;
			y0_ = event.y;
			break;

		case GMouseEvent.BUTTON1_UP :
			
			if ( null != selectedHorizon )  {
				//if solver can be launched
				if	 ( solverController.getState() == State.PREPARED )  {

					Job job = new Job("Move") {

						@Override
						protected IStatus run(IProgressMonitor monitor) {
							solverController.move();
							return Status.OK_STATUS;
						}
					};
					job.schedule();

					timer = new Annimation(scene,  solverController );

					Display.getDefault().timerExec(refreshDelay, timer);

				}
				else {
					clearSolver();
				}
			}
			break;
			
		case GMouseEvent.ABORT:
			/*flattenController.dispose();
			scene_.remove(interaction_);*/
			break;
		}
		
		

	}

	
	private void clearSolver() {
		StratiCrushServices.getInstance().removeListener(interaction_);
		solverController.clear();

		selectedComposite.remove();
		for( Patch surround : surroundedComposites ){
			surround.remove();
		}
		interaction_.removeSegments();;
		interaction_.remove();

		surroundedComposites.clear();
		selectedComposite = null;
		selectedHorizon = null;
		scene_.refresh();
	}

	

	@Override
	public void keyEvent( GKeyEvent event ) {
		
	}
	




}
