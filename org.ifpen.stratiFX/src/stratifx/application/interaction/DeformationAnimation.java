package stratifx.application.interaction;



import java.util.Timer;
import java.util.TimerTask;

import fr.ifp.jdeform.controllers.DeformationController;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import javafx.application.Platform;

class DeformationAnimation extends TimerTask {
	
	private int refreshDelay = 100;

	DeformationInteraction interaction;
	
	boolean isStopped = false;
	
	

	static public Timer start(DeformationInteraction interaction){
		DeformationAnimation animation = new DeformationAnimation(interaction);
		
		Timer timer =  new Timer(true);
		timer.schedule(animation, 0, animation.refreshDelay );
		
		return timer;
	}
	
	
	private DeformationAnimation( DeformationInteraction interaction ){
		this.interaction = interaction;
	}
	
	
	@Override
	public void run() {
		
		if (isStopped){
			return;
		}
		
		DeformationControllerCaller caller = interaction.getCaller();
		
		DeformationController controller = caller.getController();
			
		int newState = controller.getState();
				
		if ( ( newState == DeformationController.DEFORMING )  ){
			Platform.runLater( new Runnable() {
				@Override
				public void run() {
					synchronized(caller.getScene()){
						interaction.update( );
					}
				}
			});
		}
	
		if (  ( newState == DeformationController.DEFORMED) || 
			  ( newState == DeformationController.CANCELED ) || 
			  ( newState == DeformationController.FAILED )){
			Platform.runLater( new Runnable() {
				@Override
				public void run() {
					interaction.end();
				}
			});
			
			isStopped = true;
				
		}
			
		if ( ( newState == DeformationController.PREPARING ) || 
			 ( newState == DeformationController.PREPARED ) ){
		}
		
		
		
		
		
	}
}

