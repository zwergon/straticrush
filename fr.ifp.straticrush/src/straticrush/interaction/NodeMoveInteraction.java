package straticrush.interaction;


import java.util.List;

import no.geosoft.cc.graphics.GScene;
import straticrush.manipulator.CompositeManipulator;
import straticrush.manipulator.Vector2DManipulator;
import fr.ifp.jdeform.continuousdeformation.Deformation;
import fr.ifp.kronosflow.model.Patch;


public class NodeMoveInteraction extends DeformationInteraction {

	
	public NodeMoveInteraction( GScene scene, String type ){
		super(scene, type);
		init(scene, type);
	}
	
	public NodeMoveInteraction( GScene scene ){
		super(scene, "Translate");
		init( scene, "Translate" );
	}
	
	protected void init(GScene scene, String type) {		
		Deformation deformation = StratiCrushServices.getInstance().createDeformation(type);
		deformationController.setDeformation( deformation );
		withMarkerTranslation = false;
	}

	@Override
	public CompositeManipulator createManipulator(GScene scene,
			Patch selectedComposite, List<Patch> surroundedComposites) {
		return new Vector2DManipulator(scene, selectedComposite, surroundedComposites);
	}
	
	

	
}


