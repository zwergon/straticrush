package straticrush.interaction;

import fr.ifp.jdeform.deformation.ResetController;
import fr.ifp.kronosflow.model.Patch;
import straticrush.view.PatchView;
import no.geosoft.cc.graphics.GKeyEvent;
import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;

public class ResetGeometryInteraction implements GInteraction {
	
	
	private GScene scene;
	ResetController controller;
	
	@SuppressWarnings("unchecked")
	public ResetGeometryInteraction( GScene scene ) {
		this.scene = scene;
		controller = (ResetController)StratiCrushServices.getInstance().createController("Reset");
	}

	@Override
	public void event(GScene scene, GMouseEvent event) {
		if ( scene != this.scene ){
			return;
		}

		switch (event.type) {
		case GMouseEvent.BUTTON1_DOWN :
			GSegment selected = scene.findSegment (event.x, event.y);
			if ( selected !=  null ){
				GObject gobject = selected.getOwner();
				if ( gobject instanceof PatchView ){
					PatchView view = (PatchView)gobject;
					Patch patch = view.getObject();
					controller.setPatch( patch );
					controller.move();
					scene.refresh();		
				}
			}

			break;
		}
		

	}
	
	@Override
	public void keyEvent( GKeyEvent event ) {
		
	}

}
