package straticrush.interaction;

import fr.ifp.kronosflow.model.CtrlNode;
import fr.ifp.kronosflow.model.Patch;
import straticrush.view.PatchView;
import no.geosoft.cc.graphics.GEvent;
import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;

public class ResetGeometryInteraction implements GInteraction {
	
	
	private GScene scene_;
	
	public ResetGeometryInteraction( GScene scene ) {
		this.scene_ = scene;
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
					Patch patch = view.getObject();
					

					for( CtrlNode node : patch.getNodes() ){
						node.setPosition( node.getOriginalPos() );
					}
					patch.notifyListeners(null);
					scene.refresh();
					
				}
			}

			break;
		}
		

	}

}
