package straticrush.interaction;

import no.geosoft.cc.graphics.GScene;
import straticrush.manipulator.CompositeManipulator;
import straticrush.manipulator.Point2DManipulator;
import straticrush.manipulator.PointsManipulator;
import fr.ifp.jdeform.continuousdeformation.Deformation;
import fr.ifp.jdeform.continuousdeformation.DeformationFactory;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.deformation.DilatationDeformation;

public class DilatationInteraction extends DeformationInteraction {

	public DilatationInteraction(GScene scene, String type) {
		super(scene, type);
		getCaller().setDeformation( (Deformation)DeformationFactory.getInstance().createDeformation(type) );
	}

	@Override
	public CompositeManipulator createManipulator(GScene gscene,
			DeformationControllerCaller caller) {
		return new PointsManipulator(gscene, caller);
	}

}
