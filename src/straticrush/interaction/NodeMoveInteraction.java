package straticrush.interaction;


import java.util.HashSet;
import java.util.Set;

import straticrush.view.PatchView;
import fr.ifp.kronosflow.mesh.Node;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.algo.ComputeCompositePatch;
import fr.ifp.jdeform.deformation.NodeMoveController;
import no.geosoft.cc.graphics.GEvent;
import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GTransformer;


public class NodeMoveInteraction implements GInteraction {

	private GScene    scene_;
	private GSegment  selected_segment_;
	private Node  selected_node_;
	private int       x0_, y0_;
	NodeMoveController<Patch> nodeMove = null;
	
	

	@SuppressWarnings("unchecked")
	public NodeMoveInteraction( GScene scene, String type ){
		scene_ = scene;
		selected_segment_ = null;
		selected_node_ = null;
		nodeMove = (NodeMoveController<Patch>)StratiCrushServices.getInstance().createController(type);
	}
	

	@SuppressWarnings("unchecked")
	public NodeMoveInteraction( GScene scene ){
		scene_ = scene;
		selected_segment_ = null;
		selected_node_ = null;
		nodeMove = (NodeMoveController<Patch>)StratiCrushServices.getInstance().createController("Translate");
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
					PatchView view = (PatchView)gobject;
					
					Set<Patch> set = new HashSet<Patch>();
					set.add( (Patch)view.getObject() );
					Patch composite = new ComputeCompositePatch().createComposite(set);
					
					GTransformer transformer = view.getTransformer();
					double[] w_pos = transformer.deviceToWorld(event.x, event.y);
					
					nodeMove.setPatch(composite); 
					
					selected_node_ = selectNode(composite, w_pos);
					selected_segment_ = selected;
				}
			}

			x0_ = event.x;
			y0_ = event.y;

			break;

		case GEvent.BUTTON1_DRAG :
			int dx = event.x - x0_;
			int dy = event.y - y0_;
			if ( null != selected_segment_ ) {


				PatchView view = (PatchView)selected_segment_.getOwner();
				

				GTransformer transformer = view.getTransformer();

				int[] d_pos = transformer.worldToDevice(selected_node_.getPosition());
				d_pos[0] += dx;
				d_pos[1] += dy;

				double[] w_pos = transformer.deviceToWorld(d_pos);

				nodeMove.setMove( selected_node_, w_pos );
				nodeMove.move();


			}
			x0_ = event.x;
			y0_ = event.y;
			break;

		case GEvent.BUTTON1_UP :
			selected_node_ = null;
			selected_segment_ = null;
			break;
			
		case GEvent.ABORT:
			nodeMove.dispose();
			break;
		}
		
		scene_.refresh();

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


