package straticrush.interaction;

import java.util.ArrayList;
import java.util.List;

import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GTransformer;
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
	
	protected GScene    scene_;
	
	protected GPatchInteraction interaction_;
	
	
	protected DeformationController deformationController = null;
	
	protected TranslateDeformation translateDeformation = null;

	protected Patch selectedComposite;
	
	protected PatchInterval  selectedHorizon;
	
	protected PatchInterval  selectedFault;
	
	protected List<Patch> surroundedComposites = new ArrayList<Patch>();
	
	/** horizons that may be a target for deformation ordered using straticolumn */
	protected List<IPolyline> potentialHorizonTargets = new ArrayList<IPolyline>();
	
	protected int       x0_, y0_;
	

	
	@Override
	abstract public void event(GScene scene, GMouseEvent event);
	
	
	@SuppressWarnings("unchecked")
	public DeformationInteraction( GScene scene, String type ){
		scene_ = scene;
		
		selectedHorizon = null;
		selectedFault = null;
		
		deformationController = StratiCrushServices.getInstance().createDeformationController();
		
		translateDeformation =  new TranslateDeformation();
		
		
		 // Create a graphic node for holding the interaction graphics
	    interaction_ = new GPatchInteraction();    
	    
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
		translateDeformation.deform( selectedComposite );
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

}
