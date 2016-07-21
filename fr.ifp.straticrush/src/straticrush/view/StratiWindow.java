package straticrush.view;

import straticrush.interaction.StratiCrushServices;
import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GWindow;

public class StratiWindow extends GWindow{

	public StratiWindow(Object parent) {
		this(parent,null);
	}
	
	public StratiWindow (Object parent, GColor backgroundColor)
	{
		super(parent, backgroundColor);
		StratiCrushServices.getInstance().setWindow(this);
	}

	public Plot getPlot() {
		Plot scene = null;

		for( GScene sc : getScenes() ){
			if ( sc instanceof Plot ) {
				scene = (Plot)sc;
				break;
			}
		}

		return scene;
	}
	
	
	

}
