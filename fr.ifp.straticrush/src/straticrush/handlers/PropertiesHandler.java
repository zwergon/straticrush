package straticrush.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.swt.widgets.Shell;

import straticrush.interaction.StratiCrushServices;
import straticrush.parts.SectionPart;
import fr.ifp.kronosflow.controllers.property.PropertyControllerCaller;

public class PropertiesHandler {
	
	@Execute
	public void execute(Shell shell, MPart part, MHandledItem handledItem ){


		PropertyControllerCaller caller = new PropertyControllerCaller( StratiCrushServices.getInstance() );
		if ( handledItem.getElementId().equals("straticrush.handledmenuitem.properties.surface") ){
			caller.setPropertyKey("Surface");
		}
		else if ( handledItem.getElementId().equals("straticrush.handledmenuitem.properties.xy") ){
			caller.setPropertyKey("XY");
		}
		else if ( handledItem.getElementId().equals("straticrush.handledmenuitem.properties.poisson") ){
			caller.setPropertyKey("Poisson");
		}
		else if ( handledItem.getElementId().equals("straticrush.handledmenuitem.properties.porosity") ){
			caller.setPropertyKey("Porosity");
		}

		caller.applyAndNotify();

		SectionPart sectionPart = (SectionPart)part.getObject();
		sectionPart.getWindow().getPlot().refresh();

	}

}
