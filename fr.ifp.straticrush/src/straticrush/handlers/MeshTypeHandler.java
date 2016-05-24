 
package straticrush.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import straticrush.parts.SectionPart;
import fr.ifp.kronosflow.model.factory.ModelFactory;

public class MeshTypeHandler {
	
	
	@Execute
	public void execute(Shell shell, MPart part, MHandledItem handledItem ){
	    
		FileDialog dialog = new FileDialog(shell);
		String file = dialog.open();
		
		//no file selected or error, return.
		if ( file == null ){
		    return;
		}
		
		String basename = file.substring(0, file.lastIndexOf('.'));
		
		SectionPart sectionPart = (SectionPart)part.getObject();
		
		
		if ( handledItem.getElementId().equals("straticrush.handledmenuitem.freefem") ){
			sectionPart.loadMesh(basename);
		}

	
	}
	
		
}