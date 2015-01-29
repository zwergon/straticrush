package straticrush.interaction;

import no.geosoft.cc.graphics.GEvent;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import straticrush.view.PatchView;
import fr.ifp.jdeform.continuousdeformation.FlattenConstraint;
import fr.ifp.jdeform.deformation.FlattenDeformation;
import fr.ifp.kronosflow.geology.Paleobathymetry;
import fr.ifp.kronosflow.model.LinePointPair;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.algo.LineIntersection;

public class FlattenInteraction extends SolverInteraction {
	
	
	LineIntersection lineInter = null;
	

	public FlattenInteraction( GScene scene, String type ){
		super( scene, type );
		solverController.setDeformation( new FlattenDeformation() );
	}
	

	@Override
	public void event(GScene scene, GEvent event) {
		if ( scene != scene_ ){
			return;
		}

		switch (event.type) {
		case GEvent.BUTTON1_DOWN :
			GSegment selected = scene.findSegment (event.x, event.y);
			if ( selected !=  null ){
				GObject gobject = selected.getOwner();
				if ( gobject instanceof PatchView ){
				
					Patch patch = ((PatchView)gobject).getObject();
				
					createSelectedAndSurrounded(patch);
					
					selectedInterval = findHorizonFeature( scene.getTransformer().deviceToWorld(event.x, event.y) );
				
					if ( null != selectedInterval ){
						Paleobathymetry bathy = selectedInterval.getPatchLibrary().getPaleobathymetry();
						lineInter = new LineIntersection( bathy.getPaleoLine() );

						scene_.add(interaction_);
						for( Patch p : surroundedComposites ){
							interaction_.addOutline( p, true );
						}
						interaction_.addOutline( selectedComposite, false );
						interaction_.addInterval( selectedInterval );
					}					
					
					scene.refresh();
				}
			}

			x0_ = event.x;
			y0_ = event.y;

			break;

		case GEvent.BUTTON1_DRAG :
			
			interaction_.clearLines();
			if ( null != selectedInterval ) {
				
				translateComposite(scene, event);
				
				solverController.clear();
				LinePointPair I = lineInter.getFirstIntersection(selectedInterval.getInterval());
				if ( null != I ) {
					
					solverController.setPatch(selectedComposite); 
					
					Paleobathymetry bathy = selectedInterval.getPatchLibrary().getPaleobathymetry();
					solverController.setFlattenConstraint( new FlattenConstraint(selectedInterval, bathy.getPaleoLine()) );
					solverController.setPointConstraint( I );
					
					solverController.computeTargets();
				
					interaction_.addLine( solverController.getTargetLine() );
					interaction_.addLine( solverController.getDebugLine() );
					
				}
				
				interaction_.draw();;
				scene.refresh();

			}
			x0_ = event.x;
			y0_ = event.y;
			break;

		case GEvent.BUTTON1_UP :
			
			if ( null != selectedInterval ){

				solverController.move();
				solverController.clear();
				
				selectedComposite.remove();
				for( Patch surround : surroundedComposites ){
					surround.remove();
				}
				interaction_.removeSegments();;
				interaction_.remove();

				surroundedComposites.clear();
				selectedComposite = null;
				selectedInterval = null;
			}
			break;
			
		case GEvent.ABORT:
			/*flattenController.dispose();
			scene_.remove(interaction_);*/
			break;
		}
		
		scene_.refresh();

	}


	


	




}
