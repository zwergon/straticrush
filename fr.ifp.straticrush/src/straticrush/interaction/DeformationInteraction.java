package straticrush.interaction;

import java.util.ArrayList;
import java.util.List;

import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GKeyEvent;
import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import straticrush.manipulator.CompositeManipulator;
import straticrush.view.PatchView;
import fr.ifp.jdeform.continuousdeformation.IDeformation;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.kronosflow.geology.BoundaryFeature;
import fr.ifp.kronosflow.geology.StratigraphicEvent;
import fr.ifp.kronosflow.geoscheduler.Geoscheduler;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerLink;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.model.FeatureGeolInterval;
import fr.ifp.kronosflow.model.IPolyline;
import fr.ifp.kronosflow.model.Interval;
import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.Section;

public abstract class DeformationInteraction implements GInteraction {
	
	Job moveJob;
	
	private static final double  ZOOM_FACTOR = 0.9;
	private int     x0_, y0_;
	
	protected GScene    scene_;
			
	protected GeoschedulerLink link = null;
		
	/** horizons that may be a target for deformation ordered using straticolumn */
	protected List<IPolyline> potentialHorizonTargets = new ArrayList<IPolyline>();
		
	CompositeManipulator manipulator;
	
	
	public abstract CompositeManipulator createManipulator( 
			GScene gscene, 
			DeformationControllerCaller caller );
	

	public DeformationInteraction( GScene scene, String type ){
		scene_ = scene;
			
		
		Geoscheduler scheduler = getScheduler();
		link = new GeoschedulerLink( 
				scheduler.getCurrent(), 
				StratiCrushServices.getInstance().createDeformationCaller() );
	
	}
	
	
	public Geoscheduler getScheduler(){
		
		Section section = StratiCrushServices.getInstance().getSection();
		if ( section instanceof GeoschedulerSection ){
			return ((GeoschedulerSection)section).getGeoscheduler();
		}
		
		return null;

	}
	
	public void clearManipulator() {

		manipulator.deactivate();	
		scene_.refresh();
		moveJob = null;
		
	}

	
	public DeformationControllerCaller getCaller(){
		return (DeformationControllerCaller)link.getCaller();
	}


	@Override
	public void event(GScene scene, GMouseEvent event) {
		if ( scene != scene_ ){
			return;
		}
		
		if ( (moveJob != null ) && (moveJob.getState() != Job.NONE) ){
			return;
		}
		
		IDeformation deformation = getCaller().getDeformation();
		
		
		//can not interact during run of a simulation
		if ( deformation.isRunning() ){
			return;
		}

		switch (event.type) {
		case GMouseEvent.BUTTON1_DOWN :
			x0_ = event.x;
			y0_ = event.y;
			
			GSegment selected = scene.findSegment (event.x, event.y);
			if ( selected !=  null ){
				GObject gobject = selected.getOwner();
				if ( gobject instanceof PatchView ){
				
					Patch patch = ((PatchView)gobject).getObject();
				
					DeformationControllerCaller caller = getCaller();
					caller.revertAndNotify();
					
					caller.clear();
					caller.setPatch(patch);
					
					manipulator = createManipulator( scene, caller );
					if ( !manipulator.isActive() ){
						manipulator.activate();
					}
					
					manipulator.onMousePress(event);
					
					scene.redraw();
					
				}
			}

			break;

		case GMouseEvent.BUTTON1_DRAG :
		
			if ( ( null != manipulator ) && manipulator.isActive() ) {
				manipulator.onMouseMove(event);
				scene.refresh();
			}
			
			break;

		case GMouseEvent.BUTTON1_UP :
			
			
			if ( ( null != manipulator ) && manipulator.isActive() ) {
				manipulator.onMouseRelease(event);
				
				CompositeManipulator compositeManipulator = (CompositeManipulator)manipulator;

				if ( compositeManipulator.canDeform() ){
					moveJob = new Job("Move") {

						@Override
						protected IStatus run(IProgressMonitor monitor) {
							
							CompositeManipulator compositeManipulator = (CompositeManipulator)manipulator;
							
							DeformationControllerCaller deformationCaller = getCaller();
							deformationCaller.addItems( compositeManipulator.getItems() );
							deformationCaller.addRigidItems( compositeManipulator.getRigidItems());

							deformationCaller.apply();
							
							return Status.OK_STATUS;
						}


						@Override
						protected void canceling() {
							getCaller().cancel();
						}

					};
					moveJob.schedule();

					DeformationAnimation.start( scene, this );

				}
				else {
					clearManipulator();
				}

			}

			break;
			
		case GMouseEvent.ABORT:
			/*flattenController.dispose();
			scene_.remove(interaction_);*/
			break;
			
		case GMouseEvent.WHEEL_MOUSE_DOWN:
			scene_.zoom(ZOOM_FACTOR);
			break;

		case GMouseEvent.WHEEL_MOUSE_UP:
			scene_.zoom(1./ZOOM_FACTOR);
			break;
			
		case GMouseEvent.BUTTON2_DOWN :
			x0_ = event.x;
			y0_ = event.y;
			break;

		case GMouseEvent.BUTTON2_DRAG :

			int dx = event.x - x0_;
			int dy = event.y - y0_;

			scene_.pan( dx, dy);

			x0_ = event.x;
			y0_ = event.y;

			break;
				
		}
		
	 
		

	}
	

	@Override
	public void keyEvent( GKeyEvent event ) {
		if ( ( event.type == GKeyEvent.KEY_PRESSED ) && 
			 ( event.getKeyCode() == GKeyEvent.VK_ESCAPE ) && 
			 ( moveJob != null ) ){
			
			moveJob.cancel();
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
	

	
	
}
