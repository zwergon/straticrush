package straticrush.interaction;

import no.geosoft.cc.graphics.GScene;
import straticrush.manipulator.AutoTargetsManipulator;
import straticrush.manipulator.CompositeManipulator;
import fr.ifp.jdeform.continuousdeformation.Deformation;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;

public class TopBorderInteraction extends DeformationInteraction {
	
	Runnable timer;
	

	public TopBorderInteraction( GScene scene, String type ){
		super( scene, type );
		
		Deformation deformation = StratiCrushServices.getInstance().createDeformation(type);
		getCaller().setDeformation( deformation );
				
	}
	



	@Override
	public CompositeManipulator createManipulator(GScene scene, DeformationControllerCaller caller ) {
		return new AutoTargetsManipulator(scene, caller );
		
	}
	




}
