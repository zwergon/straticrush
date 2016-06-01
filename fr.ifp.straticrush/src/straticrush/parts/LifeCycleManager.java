package straticrush.parts;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;

import straticrush.properties.PoissonComputer;
import fr.ifp.kronosflow.property.controllers.PropertyController;

public class LifeCycleManager {
	
	@PostContextCreate
	public void startup(IEclipseContext context) {
		PropertyController.registerBuilder("Poisson", new PoissonComputer.Builder());
	}

}
