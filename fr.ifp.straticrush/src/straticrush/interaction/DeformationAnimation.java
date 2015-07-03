package straticrush.interaction;

import no.geosoft.cc.graphics.GScene;

import org.eclipse.swt.widgets.Display;

import fr.ifp.jdeform.continuousdeformation.Deformation;
import fr.ifp.jdeform.deformation.DeformationController;

class DeformationAnimation implements Runnable {
	
	private int refreshDelay = 100;

	DeformationInteraction interaction;
	GScene scene;
	
	
	static public DeformationAnimation start(GScene scene, DeformationInteraction interaction){
		DeformationAnimation animation = new DeformationAnimation(scene,  interaction );

		Display.getDefault().timerExec(animation.refreshDelay, animation);
		
		return animation;
	}
	
	
	public DeformationAnimation( GScene scene, DeformationInteraction interaction ){
		this.scene = scene;
		this.interaction = interaction;
	}
	
	
	@Override
	public void run() {
		
		DeformationController controller = interaction.getController();
		
		int newState = controller.getState();
				
		if ( ( newState == Deformation.DEFORMING )  ){
			scene.refresh();
			Display.getDefault().timerExec(refreshDelay, this);
		}
	
		if ( ( newState == Deformation.DEFORMED) || ( newState == Deformation.CANCELED ) ){
			interaction.clearSolver();
		}
			
		if ( ( newState == Deformation.PREPARING ) || ( newState == Deformation.PREPARED ) ){
			Display.getDefault().timerExec(refreshDelay, this);
		}
		
	}
}

