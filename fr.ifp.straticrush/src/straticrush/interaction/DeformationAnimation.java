package straticrush.interaction;

import org.eclipse.swt.widgets.Display;

import fr.ifp.jdeform.continuousdeformation.Deformation;
import fr.ifp.jdeform.continuousdeformation.IDeformation;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;

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
		
		IDeformation deformation = caller.getDeformation();
			
		int newState = deformation.getState();
				
		if ( ( newState == Deformation.DEFORMING )  ){
			interaction.update( );
			Display.getDefault().timerExec(refreshDelay, this);
		}
	
		if (  ( newState == Deformation.DEFORMED) || 
			  ( newState == Deformation.CANCELED ) || 
			  ( newState == Deformation.FAILED )){
			interaction.end();
		}
			
		if ( ( newState == Deformation.PREPARING ) || ( newState == Deformation.PREPARED ) ){
			Display.getDefault().timerExec(refreshDelay, this);
		}
		
		
		
		
		
	}
}

