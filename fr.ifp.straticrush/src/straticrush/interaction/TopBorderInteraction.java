package straticrush.interaction;

import java.util.List;

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

import straticrush.manipulator.AutoTargetsManipulator;
import straticrush.manipulator.IStratiManipulator;
import straticrush.view.PatchView;
import fr.ifp.jdeform.continuousdeformation.Deformation;
import fr.ifp.jdeform.continuousdeformation.IDeformationItem;
import fr.ifp.jdeform.deformation.DeformationController;
import fr.ifp.jdeform.deformation.TargetsDeformation;
import fr.ifp.jdeform.deformation.TargetsSolverDeformation;
import fr.ifp.jdeform.deformation.constraint.PatchIntersectionItem;
import fr.ifp.jdeform.mechanical.ImplicitDynamicSolver;
import fr.ifp.jdeform.mechanical.ImplicitStaticSolver;
import fr.ifp.kronosflow.geology.Paleobathymetry;
import fr.ifp.kronosflow.model.IPolyline;
import fr.ifp.kronosflow.model.LinePointPair;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.PolyLine;
import fr.ifp.kronosflow.model.algo.LineIntersection;

public class TopBorderInteraction extends DeformationInteraction {
	
	Runnable timer;
	
	LineIntersection lineInter = null;
	
	static int refreshDelay = 500;
	
	IStratiManipulator manipulator;
	
	
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
	
	
	

	public TopBorderInteraction( GScene scene, String type ){
		super( scene, type );
		
		Deformation deformation = StratiCrushServices.getInstance().createDeformation(type);
		deformationController.setDeformation( deformation );
		
		if ( deformation instanceof TargetsSolverDeformation ){
			TargetsSolverDeformation solverDeformation = (TargetsSolverDeformation)deformation;

			if ( type.equals("DynamicFEASolver") ){
				solverDeformation.setSolver( new ImplicitDynamicSolver(solverDeformation) );
			}
			else {
				solverDeformation.setSolver( new ImplicitStaticSolver(solverDeformation) );
			}
		}
		
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
					
					manipulator = new AutoTargetsManipulator( scene, selectedComposite, surroundedComposites );
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
			
			
			
			deformationController.clear();
			deformationController.setPatch(selectedComposite); 
			
			for( IDeformationItem item : ((AutoTargetsManipulator)manipulator).getItems() ){
				deformationController.addDeformationItem( item );
			}
			deformationController.prepare();
			deformationController.move();
			
			
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

	

	@Override
	public void keyEvent( GKeyEvent event ) {
		
	}
	




}
