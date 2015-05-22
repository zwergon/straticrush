 
package straticrush.handlers;

import no.geosoft.cc.graphics.GObject;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;

import straticrush.parts.SamplePart;
import straticrush.view.Plot;

public class NodesHandler {
	@Execute
	public void execute( @Active MPart part, MHandledItem handledItem ) {
		
		SamplePart sectionPart = (SamplePart)part.getObject();
		
		boolean checked = handledItem.isSelected();

		Plot scene = sectionPart.getPlot();
		for ( GObject gobject : scene.getChildren() ){
			gobject.setVisibility( (checked)? GObject.SYMBOLS_VISIBLE : GObject.SYMBOLS_INVISIBLE );
		}
          
		scene.refresh();
		
	}
		
}