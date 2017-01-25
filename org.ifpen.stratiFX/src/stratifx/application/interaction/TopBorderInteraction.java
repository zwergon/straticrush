package stratifx.application.interaction;

import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import stratifx.application.manipulator.AutoTargetsManipulator;
import stratifx.application.manipulator.CompositeManipulator;
import stratifx.canvas.graphics.GScene;

public class TopBorderInteraction extends DeformationInteraction {
	
	public TopBorderInteraction( GScene scene ){
		super( scene );
	}
	

	@Override
	public CompositeManipulator createManipulator(GScene gscene, DeformationControllerCaller caller ) {
		return new AutoTargetsManipulator(gscene, caller);	
	}
	




}
