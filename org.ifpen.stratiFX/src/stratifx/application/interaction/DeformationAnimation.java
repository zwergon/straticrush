package stratifx.application.interaction;

import java.util.Timer;
import java.util.TimerTask;

import fr.ifp.jdeform.controllers.DeformationController;
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
		
		DeformationController controller = interaction.getController();
		
		switch( controller.getState() ){
		case DeformationController.DEFORMING:
			Platform.runLater( new Runnable() {
				@Override
				public void run() {
					interaction.update( );
				}
			});
			break;
		case DeformationController.DEFORMED:
		case DeformationController.CANCELED:
		case DeformationController.FAILED:
			Platform.runLater( new Runnable() {
				@Override
				public void run() {
					interaction.end();
				}
			});
			
			isStopped = true;
			break;
		default:
			break;
		}
					
		
		
		
	}
}

