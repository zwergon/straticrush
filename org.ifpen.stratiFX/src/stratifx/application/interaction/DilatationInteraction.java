package stratifx.application.interaction;

import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import stratifx.application.StratiFXService;
import stratifx.application.manipulator.CompositeManipulator;
import stratifx.application.manipulator.HorizonManipulator;
import stratifx.canvas.graphics.GScene;

public class DilatationInteraction extends DeformationInteraction {

	public DilatationInteraction(GScene scene, String type) {
		super(scene);
		getCaller().setDeformation( createDeformation(type) );
	}

	@Override
	public CompositeManipulator createManipulator(GScene gscene,
			DeformationControllerCaller caller) {
		return new HorizonManipulator(gscene, caller);
	}

}
