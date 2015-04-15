package straticrush.interaction;

import no.geosoft.cc.graphics.GKeyEvent;
import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import straticrush.view.PatchView;
import fr.ifp.jdeform.continuousdeformation.FlattenConstraint;
import fr.ifp.jdeform.continuousdeformation.LPPConstraint;
import fr.ifp.jdeform.deformation.FlattenDeformation;
import fr.ifp.jdeform.geometric.OrientedShear;
import fr.ifp.jdeform.geometric.VerticalShear;
import fr.ifp.kronosflow.geology.Paleobathymetry;
import fr.ifp.kronosflow.model.LinePointPair;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.algo.LineIntersection;

public class FlattenInteraction extends SolverInteraction {
	
	
	LineIntersection lineInter = null;
	

	public FlattenInteraction( GScene scene, String type ){
		super( scene, type );
		solverController.setDeformation( new OrientedShear(1) );
	}
	

	@Override
	public void event(GScene scene, GMouseEvent event) {
		if ( scene != scene_ ){
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
					
					Paleobathymetry bathy = selectedHorizon.getPatchLibrary().getPaleobathymetry();
					solverController.addConstraint( new FlattenConstraint(selectedHorizon, bathy.getPolyline()) );
					solverController.addConstraint( new LPPConstraint(I) );
					
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

		case GMouseEvent.BUTTON1_UP :
			
			if ( null != selectedHorizon ){

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
				selectedHorizon = null;
			}
			break;
			
		case GMouseEvent.ABORT:
			/*flattenController.dispose();
			scene_.remove(interaction_);*/
			break;
		}
		
		scene_.refresh();

	}


	

	@Override
	public void keyEvent( GKeyEvent event ) {
		
	}
	




}
