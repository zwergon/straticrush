package straticrush.parts;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;

import straticrush.properties.PoissonComputer;
import straticrush.properties.PorosityComputer;
import straticrush.properties.StrateOrientationComputer;
import straticrush.properties.SurfacePropertyComputer;
import straticrush.properties.XYPropertyComputer;
import fr.ifp.kronosflow.controllers.property.PropertyController;

public class LifeCycleManager {
	
	@PostContextCreate
	public void startup(IEclipseContext context) {
		
		PropertyController.registerBuilder("Porosity", new PorosityComputer.Builder());
		PropertyController.registerBuilder("Poisson", new PoissonComputer.Builder());
		PropertyController.registerBuilder("Surface", new SurfacePropertyComputer.Builder() );
		PropertyController.registerBuilder("XY", new XYPropertyComputer.Builder() );
		PropertyController.registerBuilder("Strate Orientation", new StrateOrientationComputer.Builder() );
	}

}
