package straticrush.interaction;


import straticrush.view.PatchView;
import fr.ifp.kronosflow.model.CtrlNode;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.implicit.MeshPatch;
import fr.ifp.jdeform.deformation.ChainMeshNodeMove;
import fr.ifp.jdeform.deformation.MassSpringNodeMove;
import fr.ifp.jdeform.deformation.NodeMoveController;
import fr.ifp.jdeform.deformation.TranslateNodeMove;
import no.geosoft.cc.graphics.GEvent;
import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GTransformer;


public class NodeMoveInteraction implements GInteraction {

	private GScene    scene_;
	private GSegment  selected_segment_;
	private CtrlNode  selected_node_;
	private int       x0_, y0_;
	private NodeMoveType type_;
	
	public enum NodeMoveType {
		TRANSLATE,
		CHAINMAIL,
		SPRINGMASS
	}
	

	public NodeMoveInteraction( GScene scene, NodeMoveType type ){
		scene_ = scene;
		selected_segment_ = null;
		selected_node_ = null;
		type_ = type;
	}
	

	public NodeMoveInteraction( GScene scene ){
		scene_ = scene;
		selected_segment_ = null;
		selected_node_ = null;
		type_ = NodeMoveType.TRANSLATE;
	}
	
	private NodeMoveController createNodeMove( Patch patch ){
		
		if ( patch instanceof MeshPatch ){
			switch( type_ ){
			default:
			case TRANSLATE:
				return new TranslateNodeMove(patch);
			case CHAINMAIL:
				return new ChainMeshNodeMove((MeshPatch)patch);
			case SPRINGMASS:
				return new MassSpringNodeMove((MeshPatch)patch);
			}
		}
		else
			return new TranslateNodeMove(patch);
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
					selected_node_ = view.selectNode(event.x, event.y);
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
				Patch patch = view.getObject();

				NodeMoveController node_move = createNodeMove(patch);

				GTransformer transformer = view.getTransformer();

				int[] d_pos = transformer.worldToDevice(selected_node_.getPosition());
				d_pos[0] += dx;
				d_pos[1] += dy;

				double[] w_pos = transformer.deviceToWorld(d_pos);

				node_move.setMove( selected_node_, w_pos );
				node_move.move();


			}
			x0_ = event.x;
			y0_ = event.y;
			break;

		case GEvent.BUTTON1_UP :
			selected_node_ = null;
			selected_segment_ = null;
			break;
			
		}
		
		scene_.refresh();

	}
	
}


