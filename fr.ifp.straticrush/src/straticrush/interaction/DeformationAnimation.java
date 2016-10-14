package straticrush.interaction;

import org.eclipse.swt.widgets.Display;

import fr.ifp.jdeform.controllers.DeformationController;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.deformation.Deformation;
import fr.ifp.jdeform.deformation.IDeformation;

class DeformationAnimation implements Runnable {
	
	private int refreshDelay = 100;

	DeformationInteraction interaction;
	
	
	static public DeformationAnimation start(DeformationInteraction interaction){
		DeformationAnimation animation = new DeformationAnimation(interaction );
		Display.getDefault().timerExec(animation.refreshDelay, animation);
		return animation;
	}
	
	
	private DeformationAnimation( DeformationInteraction interaction ){
		this.interaction = interaction;
	}
	
	
	@Override
	public void run() {
		
		DeformationControllerCaller caller = interaction.getCaller();
		
		DeformationController controller = caller.getController();
			
		int newState = controller.getState();
				
		if ( ( newState == DeformationController.DEFORMING )  ){
			
			synchronized(caller.getScene()){
				interaction.update( );
			}
			Display.getDefault().timerExec(refreshDelay, this);
		}
	
		if (  ( newState == DeformationController.DEFORMED) || 
			  ( newState == DeformationController.CANCELED ) || 
			  ( newState == DeformationController.FAILED )){
				interaction.end();
		}
			
		if ( ( newState == DeformationController.PREPARING ) || 
			 ( newState == DeformationController.PREPARED ) ){
			Display.getDefault().timerExec(refreshDelay, this);
		}
		
		
		
		
		
	}
}

