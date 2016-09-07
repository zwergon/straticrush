package straticrush.view;

import java.awt.Insets;

import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GWindow;
import straticrush.interaction.StratiCrushServices;
import straticrush.interaction.ZoomInteraction;

public class StratiWindow extends GWindow {
	
	Plot plot;

		
	public StratiWindow (Object parent)
	{
		super(parent, new GColor(0.8f, 0.8f, 0.8f));
		
		// Create the graphic canvas

		// Definition of exact chart location inside window
		Insets insets = new Insets(80, 60, 20, 20);

		// Create a "background" device oriented annotation scene
		GScene annotationScene = new GScene (this);
		GObject annotation = new Annotation (insets);
		annotationScene.add (annotation);


		// Create a value specific "plot" scene
		plot = new Plot (this, insets);
		annotationScene.setUserData (plot);
		plot.shouldWorldExtentFitViewport (false);
		plot.shouldZoomOnResize (false);   
		
		StratiCrushServices.getInstance().setWindow(this);
		
		startInteraction ( new ZoomInteraction(plot) );
	}

	public Plot getPlot() {
		return plot;
	}
	
	
	
	
	

}
