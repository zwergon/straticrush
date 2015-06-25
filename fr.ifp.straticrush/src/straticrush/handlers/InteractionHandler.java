 
package straticrush.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import straticrush.parts.SectionPart;

public class InteractionHandler {
	@Execute
	public void execute( 
			@Active MPart part, 
			@Named("straticrush.commandparameter.interactiontype") String interactionType,
			@Optional @Named("straticrush.commandparameter.deformationtype") String deformationType) {

	    SectionPart sectionPart = (SectionPart)part.getObject();
	    sectionPart.startInteraction( interactionType, deformationType );
	   
	}
		
}