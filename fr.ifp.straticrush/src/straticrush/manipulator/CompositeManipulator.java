package straticrush.manipulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import straticrush.interaction.StratiCrushServices;
import fr.ifp.jdeform.continuousdeformation.IDeformationItem;
import fr.ifp.jdeform.controllers.DeformationController;
import fr.ifp.jdeform.controllers.Scene;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.deformation.TranslateDeformation;
import fr.ifp.jdeform.deformation.items.NodeMoveItem;
import fr.ifp.kronosflow.geology.FaultFeature;
import fr.ifp.kronosflow.geology.StratigraphicEvent;
import fr.ifp.kronosflow.model.FeatureGeolInterval;
import fr.ifp.kronosflow.model.Interval;
import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.Node;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.PolyLineGeometry;

public abstract class CompositeManipulator implements IStratiManipulator {
	
	protected GScene gscene;
	
	protected GPatchObject interaction;
	
	protected Scene scene;
	
	protected boolean withTranslateMarker = true;
		
	List<IDeformationItem> items =  new ArrayList<IDeformationItem>();
		
	public CompositeManipulator( 
			GScene gscene, 
			DeformationControllerCaller caller){
		this.gscene = gscene;
		this.scene = caller.getScene();

	}
	

	@Override
	public boolean isActive(){
		return (interaction != null );
	}
	

	@Override
	public GObject getInteraction() {
		return interaction;
	}
	
	public boolean canDeform(){
		return !items.isEmpty();
	}
	

	public List<IDeformationItem> getItems() {
		return items;
	}
	

	@Override
	public void activate() {
		if ( null != scene.getSelected() ){
			interaction = new GPatchObject();
			gscene.add(interaction);
			for( Patch p : scene.getUnselected() ){
				interaction.addOutline( p, true );
			}
			interaction.addOutline( scene.getSelected(), false );
		}	
		StratiCrushServices.getInstance().addListener(interaction);
	}

	@Override
	public void deactivate() {
		StratiCrushServices.getInstance().removeListener(interaction);
		gscene.remove(interaction);
		interaction.removeSegments();;
		interaction.remove();
		interaction = null;		
		items.clear();
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
		
		
		Patch selected = scene.getSelected();
		for( KinObject object : selected.getChildren() ){
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
	
	protected void translateTo( double[] t ){

		DeformationController translateController = new DeformationController();
		translateController.setDeformation( new TranslateDeformation() );
		translateController.addDeformationItem( 
				new NodeMoveItem( t )
				);
		translateController.setScene( scene );
		translateController.prepare();
		translateController.move();

	}

}
