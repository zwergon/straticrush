 
package straticrush.handlers;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import straticrush.parts.SectionPart;
import straticrush.view.Plot;

public class OneOneHandler {
    @Execute
    public void execute( @Active MPart part ) {


        SectionPart sectionPart = (SectionPart)part.getObject();
        Plot scene = sectionPart.getWindow().getPlot();
        scene.unzoom();

    }

}