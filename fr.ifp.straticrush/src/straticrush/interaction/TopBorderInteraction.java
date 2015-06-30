package straticrush.interaction;

import java.util.List;

import no.geosoft.cc.graphics.GKeyEvent;
import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;

import org.eclipse.swt.widgets.Display;

import straticrush.manipulator.AutoTargetsManipulator;
import straticrush.manipulator.CompositeManipulator;
import straticrush.manipulator.IStratiManipulator;
import straticrush.view.PatchView;
import fr.ifp.jdeform.continuousdeformation.Deformation;
import fr.ifp.jdeform.continuousdeformation.IDeformationItem;
import fr.ifp.jdeform.deformation.DeformationController;
import fr.ifp.jdeform.deformation.TargetsSolverDeformation;
import fr.ifp.jdeform.mechanical.ImplicitDynamicSolver;
import fr.ifp.jdeform.mechanical.ImplicitStaticSolver;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.algo.LineIntersection;

public class TopBorderInteraction extends DeformationInteraction {
	
	Runnable timer;
	

	public TopBorderInteraction( GScene scene, String type ){
		super( scene, type );
		
		Deformation deformation = StratiCrushServices.getInstance().createDeformation(type);
		deformationController.setDeformation( deformation );
		
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
	public CompositeManipulator createManipulator(GScene scene, Patch selectedComposite,
			List<Patch> surroundedComposites) {
		return new AutoTargetsManipulator(scene, selectedComposite, surroundedComposites );
		
	}
	




}
