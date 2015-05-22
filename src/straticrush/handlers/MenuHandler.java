 
package straticrush.handlers;


import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;

import straticrush.parts.SamplePart;

public class MenuHandler {

	@Execute
	public void execute( @Active MPart part, MHandledItem handledItem ) {
		
		SamplePart sectionPart = (SamplePart)part.getObject();
		
		sectionPart.openMenu(handledItem.isSelected());
		
	}
}