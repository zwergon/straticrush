package straticrush.interaction;

import straticrush.view.PatchView;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.algos.ComputeContact;
import no.geosoft.cc.graphics.GEvent;
import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;

public class ComputeContactInteraction implements GInteraction {

	Patch p1 = null;
	Patch p2 = null;
	GScene scene = null;
	
	public ComputeContactInteraction( GScene scene ){
		this.scene = scene;
	}
	
	@Override
	public void event(GScene scene, GEvent event) {
		
		if ( this.scene != scene ){
			return;
		}

		switch (event.type) {
		case GEvent.BUTTON1_DOWN:
			GSegment selected = scene.findSegment (event.x, event.y);
			if ( selected !=  null ){
				GObject gobject = selected.getOwner();
				if ( gobject instanceof PatchView ){
					PatchView view = (PatchView)gobject;
					if ( p1 == null ){
						p1 = view.getObject();
					}
					else if ( p2 == null ){
						p2 = view.getObject();
					}
				}
			}
			break;
		case GEvent.BUTTON1_UP:
			if ( ( p1 != null )&& ( p2 != null ) ){
				ComputeContact compute = new ComputeContact(p1);
				compute.execute(p2);
				p1 = null;
				p2 = null;
			}
		}

	}

}
