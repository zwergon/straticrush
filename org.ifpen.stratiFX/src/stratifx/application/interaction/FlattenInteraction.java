package stratifx.application.interaction;

import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.deformation.Deformation;
import stratifx.application.StratiFXService;
import stratifx.application.manipulator.CompositeManipulator;
import stratifx.application.manipulator.MonoTargetManipulator;
import stratifx.canvas.graphics.GScene;

public class FlattenInteraction extends DeformationInteraction {
	
		

	public FlattenInteraction( GScene scene ){
		super( scene );
	}
	


	@Override
	public CompositeManipulator createManipulator(GScene gscene, DeformationControllerCaller caller ) {		
		return new MonoTargetManipulator(gscene, caller );
	}




}
