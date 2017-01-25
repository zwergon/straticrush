package stratifx.application.interaction;


import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import stratifx.application.manipulator.CompositeManipulator;
import stratifx.application.manipulator.Vector2DManipulator;
import stratifx.canvas.graphics.GScene;


public class NodeMoveInteraction extends DeformationInteraction {

	
	public NodeMoveInteraction( GScene scene ){
		super(scene);
	}
	
	@Override
	public CompositeManipulator createManipulator(GScene gscene, DeformationControllerCaller caller ) {
		return new Vector2DManipulator(gscene, caller);
	}
	
	

	
}


