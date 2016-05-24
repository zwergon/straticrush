package straticrush.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.swt.widgets.Shell;

import fr.ifp.kronosflow.properties.PropertyComputer;
import fr.ifp.kronosflow.properties.SurfacePropertyComputer;
import straticrush.parts.SectionPart;

public class PropertiesHandler {
	
	@Execute
	public void execute(Shell shell, MPart part, MHandledItem handledItem ){
	    
		
		SectionPart sectionPart = (SectionPart)part.getObject();
		
		PropertyComputer computer = null;
		if ( handledItem.getElementId().equals("straticrush.handledmenuitem.properties.surface") ){
			computer = new SurfacePropertyComputer( sectionPart.getSection() );
		}
		
		computer.compute();

	}

}
