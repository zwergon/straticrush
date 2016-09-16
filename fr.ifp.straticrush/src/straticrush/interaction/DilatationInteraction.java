package straticrush.interaction;

import no.geosoft.cc.graphics.GScene;
import straticrush.manipulator.CompositeManipulator;
import straticrush.manipulator.HorizonManipulator;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;

public class DilatationInteraction extends DeformationInteraction {

	public DilatationInteraction(GScene scene, String type) {
		super(scene, type);
		getCaller().setDeformation( StratiCrushServices.getInstance().createDeformation(type) );
	}

	@Override
	public CompositeManipulator createManipulator(GScene gscene,
			DeformationControllerCaller caller) {
		return new HorizonManipulator(gscene, caller);
	}

}
