package stratifx.application.manipulator;

import java.util.List;

import fr.ifp.jdeform.controllers.TranslationController;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.controllers.scene.Scene;
import fr.ifp.jdeform.controllers.scene.algo.TargetsExtractor;
import fr.ifp.jdeform.deformation.Deformation;
import fr.ifp.jdeform.deformation.IDeformationItem;
import fr.ifp.jdeform.deformation.IRigidItem;
import fr.ifp.kronosflow.geometry.Vector2D;
import fr.ifp.kronosflow.model.FeatureGeolInterval;
import fr.ifp.kronosflow.model.FeatureInterval;
import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.geology.FaultFeature;
import fr.ifp.kronosflow.model.geology.StratigraphicEvent;
import fr.ifp.kronosflow.polyline.IPolylineProvider;
import fr.ifp.kronosflow.polyline.Node;
import fr.ifp.kronosflow.polyline.PolyLineGeometry;
import stratifx.application.views.GPatchObject;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.interaction.GMouseEvent;

public abstract class CompositeManipulator implements IStratiManipulator {
	
	protected GScene gscene;
		
	protected GPatchObject selectedPatchGraphic;
	
	double[] start;

	double[] prev;
		
	protected DeformationControllerCaller deformationCaller;
		
	List<IDeformationItem> items;
	
	List<IRigidItem> rigidItems;
	
	TranslationController translateController;
	
	
	protected TargetsExtractor targetsExtractor;
	
	List<IPolylineProvider> potentialHorizon;

	public CompositeManipulator( 
			GScene gscene, 
			DeformationControllerCaller caller ){
		this.gscene = gscene;
		this.deformationCaller = caller;
		
		translateController = new TranslationController();
		translateController.setScene(caller.getScene());
		
		targetsExtractor = new TargetsExtractor(caller.getScene());
		
	}
	
	
	@Override
	public void onMousePress(GMouseEvent event) {
		start = gscene.getTransformer().deviceToWorld(event.x, event.y);
		prev  = Vector2D.copy(start);
	}
	
	
	@Override
	public void onMouseMove(GMouseEvent event) {
		
		double[] xy = gscene.getTransformer().deviceToWorld(event.x, event.y);
		translateTo(xy); // rigid body deformation
		computeTargets();
		
		if ( null != selectedPatchGraphic ){
			selectedPatchGraphic.redraw();
		}
	}
	
	 abstract protected void computeTargets();
	 
	 
	 protected void translateTo(double[] xy) {
		 double[] t = new double[] { xy[0] - prev[0], xy[1] - prev[1] };

		 translateController.setTranslation(t);
		 translateController.prepare();
		 translateController.move();
		 prev = Vector2D.copy(xy);
	 }

	 /**
	  * By default, many manipulators are atomic. Only one clic.
	  * A manipulator may be active ( visible ) but no more action is required for user. ( isManipulating == false ).
	  */
	@Override
	public boolean isManipulating() {
		return false;
	}

	@Override
	public boolean isActive(){
		return (selectedPatchGraphic != null );
	}
	

	@Override
	public GObject getGraphic() {
		return selectedPatchGraphic;
	}
	
	public boolean canDeform(){
		if (items == null) {
			return false;
		}

		if (items.isEmpty()) {
			return false;
		}

		return true;
	}
	

	public List<IDeformationItem> getItems() {
		return items;
	}
	
	public List<IRigidItem> getRigidItems(){
		return rigidItems;
	}
	
	

	@Override
	public void activate() {
		
		Scene scene = deformationCaller.getScene();
		
		createPotentialTargets();
		
		if ( null != scene.getSelected() ){
			selectedPatchGraphic = new GPatchObject();
			gscene.add(selectedPatchGraphic);
			for( Patch p : scene.getUnselected() ){
				selectedPatchGraphic.addOutline( p, true );
			}
			selectedPatchGraphic.addOutline( scene.getSelected(), false );
		}	
		
		if ( null != selectedPatchGraphic ){
			
			Deformation deformation = (Deformation)deformationCaller.getDeformation();
			selectedPatchGraphic.setDeformation( deformation );
			selectedPatchGraphic.redraw();
		}
		
		
	}

	@Override
	public void deactivate() {
		if ( null != selectedPatchGraphic ){
			selectedPatchGraphic.setDeformation( null );
			gscene.remove(selectedPatchGraphic);
			selectedPatchGraphic.removeSegments();;
			selectedPatchGraphic.remove();
			selectedPatchGraphic = null;	
		}
	}
	
	
	public void updateGraphics(){
		
		if ( null != selectedPatchGraphic ){
			selectedPatchGraphic.redraw();
		}
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
	

	protected void createPotentialTargets(){
		potentialHorizon = targetsExtractor.getHorizonTargets();
	}

	
	


}
