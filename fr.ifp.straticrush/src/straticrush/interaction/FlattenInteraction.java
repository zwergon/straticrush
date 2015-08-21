package straticrush.interaction;

import no.geosoft.cc.graphics.GScene;
import straticrush.manipulator.CompositeManipulator;
import straticrush.manipulator.MonoTargetManipulator;
import fr.ifp.jdeform.continuousdeformation.Deformation;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.deformation.TargetsSolverDeformation;
import fr.ifp.jdeform.mechanical.ImplicitDynamicSolver;
import fr.ifp.jdeform.mechanical.ImplicitStaticSolverLS;

public class FlattenInteraction extends DeformationInteraction {
	
		

	public FlattenInteraction( GScene scene, String type ){
		super( scene, type );
		
		Deformation deformation = StratiCrushServices.getInstance().createDeformation(type);
		
		getCaller().setDeformation( deformation );
		
		if ( deformation instanceof TargetsSolverDeformation ){
			TargetsSolverDeformation solverDeformation = (TargetsSolverDeformation)deformation;

			if ( type.equals("DynamicFEASolver") ){
				solverDeformation.setSolver( new ImplicitDynamicSolver(solverDeformation) );
			}
			else {
				solverDeformation.setSolver( new ImplicitStaticSolverLS(solverDeformation) );
			}
		}
		
	}
	


	@Override
	public CompositeManipulator createManipulator(GScene scene, DeformationControllerCaller caller ) {		
		return new MonoTargetManipulator(scene, caller);
	}
	




}
