package stratifx.application.interaction;

import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import stratifx.application.manipulator.CompositeManipulator;
import stratifx.application.manipulator.HorizonManipulator;
import stratifx.canvas.graphics.GScene;

public class DilatationInteraction extends DeformationInteraction {

	public DilatationInteraction(GScene scene) {
		super(scene);
	}

	@Override
	public CompositeManipulator createManipulator(GScene gscene,
			DeformationControllerCaller caller) {
		return new HorizonManipulator(gscene, caller);
	}

}
