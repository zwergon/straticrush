package stratifx.application.interaction;


import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import stratifx.application.manipulator.CompositeManipulator;
import stratifx.application.manipulator.Vector2DManipulator;
import stratifx.canvas.graphics.GScene;


public class NodeMoveInteraction extends DeformationInteraction {

	
	public NodeMoveInteraction( GScene scene, String type ){
		super(scene);
		init(scene, type);
	}
	
	protected void init(GScene scene, String type) {		
		getCaller().setDeformation( createDeformation(type) );
	}

	@Override
	public CompositeManipulator createManipulator(GScene gscene, DeformationControllerCaller caller ) {
		return new Vector2DManipulator(gscene, caller);
	}
	
	

	
}


