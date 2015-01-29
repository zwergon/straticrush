package straticrush.interaction;

import java.util.ArrayList;
import java.util.List;

import no.geosoft.cc.graphics.GEvent;
import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GTransformer;
import fr.ifp.jdeform.deformation.SolverDeformationController;
import fr.ifp.jdeform.deformation.TranslateNodeMove;
import fr.ifp.kronosflow.geology.BoundaryFeature;
import fr.ifp.kronosflow.geology.StratigraphicEvent;
import fr.ifp.kronosflow.geometry.Vector2D;
import fr.ifp.kronosflow.model.CompositePatch;
import fr.ifp.kronosflow.model.FeatureGeolInterval;
import fr.ifp.kronosflow.model.Interval;
import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.PolyLineGeometry;
import fr.ifp.kronosflow.model.algo.ComputeBloc;

public abstract class SolverInteraction implements GInteraction {
	
	protected GScene    scene_;
	
	protected GPatchInteraction interaction_;
	
	protected PatchInterval  selectedInterval;
	
	protected SolverDeformationController<Patch> solverController = null;
	
	protected TranslateNodeMove translateController = null;

	protected Patch selectedComposite;
	
	protected List<Patch> surroundedComposites = new ArrayList<Patch>();
	
	protected int       x0_, y0_;
	

	
	@Override
	abstract public void event(GScene scene, GEvent event);
	
	
	@SuppressWarnings("unchecked")
	public SolverInteraction( GScene scene, String type ){
		scene_ = scene;
		
		selectedInterval = null;
		
		solverController = (SolverDeformationController<Patch> )StratiCrushServices.getInstance().createController(type);
		
		translateController = (TranslateNodeMove)StratiCrushServices.getInstance().createController("Translate");
		
		 // Create a graphic node for holding the interaction graphics
	    interaction_ = new GPatchInteraction();    
	}
	
	protected PatchInterval findHorizonFeature( double[] ori ) {
		
		PatchInterval interval = null;
		double minDist = Double.POSITIVE_INFINITY;
		
		for( KinObject object : selectedComposite.getChildren() ){
			if ( object instanceof FeatureGeolInterval ){
				Interval fgInterval = ((FeatureGeolInterval)object).getInterval();
				BoundaryFeature bf = (BoundaryFeature)fgInterval.getFeature();
				if ( bf instanceof StratigraphicEvent ){
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
	
	
	protected void translateComposite(GScene scene, GEvent event) {
		translateController.setPatch(selectedComposite); 

		GTransformer transformer = scene.getTransformer();

		int[] oldPos = new int[2];
		oldPos[0] = x0_;  oldPos[1] = y0_;
		int[] newPos = new int[2];
		newPos[0] = event.x;  newPos[1] = event.y;
		double[] d_pos1 = transformer.deviceToWorld(oldPos);
		double[] d_pos2 = transformer.deviceToWorld(newPos);
		
		translateController.setTranslation( Vector2D.substract(d_pos2, d_pos1) );
		translateController.move();
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
				}
				availablePatches.removeAll( composite.getPatchs() );
		}
	}
	

}
