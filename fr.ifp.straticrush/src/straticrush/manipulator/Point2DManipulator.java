package straticrush.manipulator;

import no.geosoft.cc.graphics.GScene;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;

public class Point2DManipulator extends Vector2DManipulator {

	public Point2DManipulator(GScene gscene, DeformationControllerCaller caller) {
		super(gscene, caller);
		enableTranslate(false);
	}

}
