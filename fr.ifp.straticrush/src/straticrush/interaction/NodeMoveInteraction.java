package straticrush.interaction;


import no.geosoft.cc.graphics.GScene;
import straticrush.manipulator.CompositeManipulator;
import straticrush.manipulator.Vector2DManipulator;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.deformation.Deformation;


public class NodeMoveInteraction extends DeformationInteraction {

	
	public NodeMoveInteraction( GScene scene, String type ){
		super(scene, type);
		init(scene, type);
	}
	
	protected void init(GScene scene, String type) {		
		Deformation deformation = StratiCrushServices.getInstance().createDeformation(type);
		getCaller().setDeformation( deformation );
	}

	@Override
	public CompositeManipulator createManipulator(GScene gscene, DeformationControllerCaller caller ) {
		return new Vector2DManipulator(gscene, caller);
	}
	
	

	
}


