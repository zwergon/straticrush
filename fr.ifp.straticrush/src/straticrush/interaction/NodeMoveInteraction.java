package straticrush.interaction;


import java.util.HashSet;
import java.util.Set;

import straticrush.view.PatchView;
import fr.ifp.kronosflow.controller.interfaces.IDeformationController;
import fr.ifp.kronosflow.mesh.Node;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.algo.ComputeCompositePatch;
import fr.ifp.jdeform.continuousdeformation.Deformation;
import fr.ifp.jdeform.deformation.ChainMailDeformation;
import fr.ifp.jdeform.deformation.MassSpringNodeDeformation;
import fr.ifp.jdeform.deformation.NodeDeformation;
import fr.ifp.jdeform.deformation.TargetsSolverDeformation;
import fr.ifp.jdeform.deformation.TranslateDeformation;
import fr.ifp.jdeform.deformation.constraint.NodeMoveItem;
import no.geosoft.cc.graphics.GKeyEvent;
import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GTransformer;


public class NodeMoveInteraction extends DeformationInteraction {

	private GScene    scene_;
	private GSegment  selected_segment_;
	private Node  selected_node_;
	private int       x0_, y0_;
	
	
	GTranslateInteraction interaction_;
	
	
	public NodeMoveInteraction( GScene scene, String type ){
		super(scene, type);
		init(scene, type);
	}
	
	public NodeMoveInteraction( GScene scene ){
		super(scene, "Translate");
		init( scene, "Translate" );
	}
	
	protected void init(GScene scene, String type) {
		scene_ = scene;
		selected_segment_ = null;
		selected_node_ = null;
		
		interaction_ = new GTranslateInteraction();
		
		Deformation deformation = StratiCrushServices.getInstance().createDeformation(type);
		deformationController.setDeformation( deformation );
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
					PatchView view = (PatchView)gobject;
					
					createSelectedAndSurrounded((Patch)view.getObject());
					
					//create a composite to have a mesh !
				
					deformationController.setPatch(selectedComposite);
					
					GTransformer transformer = view.getTransformer();
					double[] w_pos = transformer.deviceToWorld(event.x, event.y);
					selected_node_ = selectNode(selectedComposite, w_pos);
					
					scene_.add(interaction_);
					
					interaction_.createMarker(event.x, event.y);
					selected_segment_ = selected;
				}
			}

			x0_ = event.x;
			y0_ = event.y;

			break;

		case GMouseEvent.BUTTON1_DRAG :
			
			if ( null != selected_segment_ ) {
				interaction_.moveTo( event.x, event.y );
			}
			x0_ = event.x;
			y0_ = event.y;
			break;

		case GMouseEvent.BUTTON1_UP :
			if ( null != selected_segment_ ) {

				PatchView view = (PatchView)selected_segment_.getOwner();
				GTransformer transformer = view.getTransformer();

				int[] displacement = interaction_.getDisplacement();
				
				int[] d_pos = transformer.worldToDevice(selected_node_.getPosition());
				d_pos[0] += displacement[0];
				d_pos[1] += displacement[1];

				
				double[] w_pos = transformer.deviceToWorld(d_pos);
				
				NodeMoveItem item = new NodeMoveItem(selected_node_);
				item.setTarget(w_pos);;
				deformationController.addDeformationItem( item  );
				deformationController.prepare();
				deformationController.move();
			}
			
			interaction_.removeSegments();;
			interaction_.remove();
			selected_node_ = null;
			selected_segment_ = null;
			break;
			
		case GMouseEvent.ABORT:
			break;
		}
		
		scene_.refresh();

	}
	
	@Override
	public void keyEvent( GKeyEvent event ) {
		
	}
	
	private Node selectNode( Patch patch, double[] pos  ){
		
		
		
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
	
}


