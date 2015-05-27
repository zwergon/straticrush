 
package straticrush.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;

import fr.ifp.kronosflow.model.factory.ModelFactory;

public class MeshTypeHandler {
	@Execute
	public void execute( MHandledItem handledItem ) {
		
		if ( handledItem.getElementId().equals("straticrush.handledmenuitem.line.grid") ){
			ModelFactory.setFactoryType("LineGridImplicit");
		}
		else if ( handledItem.getElementId().equals("straticrush.handledmenuitem.trgl.trgl") ){
			ModelFactory.setFactoryType("TrglTrglExplicit");
		}
		else if ( handledItem.getElementId().equals("straticrush.handledmenuitem.grid.grid") ){
			ModelFactory.setFactoryType("GridGridImplicit");
		}


		
	}
		
}