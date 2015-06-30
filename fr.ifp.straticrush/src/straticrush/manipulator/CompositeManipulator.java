package straticrush.manipulator;

import java.util.ArrayList;
import java.util.List;

import straticrush.interaction.StratiCrushServices;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import fr.ifp.jdeform.continuousdeformation.IDeformationItem;
import fr.ifp.jdeform.deformation.constraint.LinePairingItem;
import fr.ifp.kronosflow.geology.FaultFeature;
import fr.ifp.kronosflow.geology.StratigraphicEvent;
import fr.ifp.kronosflow.mesh.Node;
import fr.ifp.kronosflow.model.FeatureGeolInterval;
import fr.ifp.kronosflow.model.Interval;
import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.PolyLineGeometry;

public abstract class CompositeManipulator implements IStratiManipulator {
	
	protected GScene scene;
	
	protected GPatchObject interaction;
	
	protected Patch selectedComposite;
	
	protected List<Patch> surroundedComposites;
	
	List<IDeformationItem> items =  new ArrayList<IDeformationItem>();
	
	public CompositeManipulator( 
			GScene scene, 
			Patch selectedComposite, 
			List<Patch> surroundedComposites ){
		this.scene = scene;
		this.selectedComposite = selectedComposite;
		this.surroundedComposites = surroundedComposites;
	}
	

	@Override
	public boolean isActive(){
		return (interaction != null );
	}
	

	@Override
	public GObject getInteraction() {
		return interaction;
	}
	

	public List<IDeformationItem> getItems() {
		return items;
	}
	

	@Override
	public void activate() {
		if ( null != selectedComposite ){
			interaction = new GPatchObject();
			scene.add(interaction);
			for( Patch p : surroundedComposites ){
				interaction.addOutline( p, true );
			}
			interaction.addOutline( selectedComposite, false );
		}	
		StratiCrushServices.getInstance().addListener(interaction);
	}

	@Override
	public void deactivate() {
		StratiCrushServices.getInstance().removeListener(interaction);
		scene.remove(interaction);
		interaction.removeSegments();;
		interaction.remove();
		interaction = null;
	}
	
	
	protected Node selectNode( Patch patch, double[] pos  ){

		double distance = Double.MAX_VALUE;
		Node nearest_node = null;
		for( Node ctl_node : patch.getNodes() ){

			double cur_distance = ctl_node.distance(pos);
			if ( cur_distance < distance ){
				distance = cur_distance;
				nearest_node = ctl_node;
			}
		}		
		return nearest_node;
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
	
	

}
