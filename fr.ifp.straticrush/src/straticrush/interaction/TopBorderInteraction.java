package straticrush.interaction;

import java.util.List;

import no.geosoft.cc.graphics.GScene;
import straticrush.manipulator.AutoTargetsManipulator;
import straticrush.manipulator.CompositeManipulator;
import fr.ifp.jdeform.continuousdeformation.Deformation;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.deformation.TargetsSolverDeformation;
import fr.ifp.jdeform.mechanical.ImplicitDynamicSolver;
import fr.ifp.jdeform.mechanical.ImplicitStaticSolver;
import fr.ifp.kronosflow.model.Patch;

public class TopBorderInteraction extends DeformationInteraction {
	
	Runnable timer;
	

	public TopBorderInteraction( GScene scene, String type ){
		super( scene, type );
		
		Deformation deformation = StratiCrushServices.getInstance().createDeformation(type);
		getCaller().setDeformation( deformation );
		
		if ( deformation instanceof TargetsSolverDeformation ){
			TargetsSolverDeformation solverDeformation = (TargetsSolverDeformation)deformation;

			if ( type.equals("DynamicFEASolver") ){
				solverDeformation.setSolver( new ImplicitDynamicSolver(solverDeformation) );
			}
			else {
				solverDeformation.setSolver( new ImplicitStaticSolver(solverDeformation) );
			}
		}
		
	}
	



	@Override
	public CompositeManipulator createManipulator(GScene scene, DeformationControllerCaller caller ) {
		return new AutoTargetsManipulator(scene, caller );
		
	}
	




}
