package straticrush.interaction;

import no.geosoft.cc.graphics.GScene;
import straticrush.manipulator.CompositeManipulator;
import straticrush.manipulator.MonoTargetManipulator;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.deformation.Deformation;

public class FlattenInteraction extends DeformationInteraction {
	
		

	public FlattenInteraction( GScene scene, String type ){
		super( scene, type );
		
		Deformation deformation = StratiCrushServices.getInstance().createDeformation(type);
		
		getCaller().setDeformation( deformation );
			
	}
	


	@Override
	public CompositeManipulator createManipulator(GScene gscene, DeformationControllerCaller caller ) {		
		return new MonoTargetManipulator(gscene, caller );
	}




}
