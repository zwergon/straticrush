package stratifx.application.manipulator;

import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import stratifx.canvas.graphics.GScene;

public class Point2DManipulator extends Vector2DManipulator {

	public Point2DManipulator(GScene gscene, DeformationControllerCaller caller) {
		super(gscene, caller);
		enableTranslate(false);
	}

}
