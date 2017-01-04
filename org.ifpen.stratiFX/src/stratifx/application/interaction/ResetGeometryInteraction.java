package stratifx.application.interaction;

import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.controllers.scene.SceneBuilder;
import fr.ifp.jdeform.deformations.ResetDeformation;
import fr.ifp.kronosflow.model.Patch;
import stratifx.application.StratiFXService;
import stratifx.application.views.GPatchView;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.interaction.GInteraction;
import stratifx.canvas.interaction.GKeyEvent;
import stratifx.canvas.interaction.GMouseEvent;

public class ResetGeometryInteraction implements GInteraction {
	
	
	private GScene scene;
	DeformationControllerCaller controllerCaller;
	

	public ResetGeometryInteraction( GScene scene ) {
		
		StratiFXService service = StratiFXService.instance;
		this.scene = scene;
		controllerCaller = (DeformationControllerCaller) service.createCaller("Deformation");
		controllerCaller.setDeformation( new ResetDeformation() );
	}


	@Override
	public boolean start(GScene scene) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stop(GScene scene) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseEvent(GScene scene, GMouseEvent event) {
		if ( scene != this.scene ){
			return false;
		}

		switch (event.type) {
		case GMouseEvent.BUTTON_DOWN :
			GSegment selected = scene.findSegment (event.x, event.y);
			if ( selected !=  null ){
				GObject gobject = selected.getOwner();
				if ( gobject instanceof GPatchView ){
					GPatchView view = (GPatchView)gobject;
					Patch patch = view.getObject();
					controllerCaller.setScene( SceneBuilder.createDefaultScene(patch) );
					controllerCaller.applyAndNotify();;
					scene.refresh();		
				}
			}

			break;
		}
		
		return true;
	}

	@Override
	public boolean keyEvent(GScene scene, GKeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
