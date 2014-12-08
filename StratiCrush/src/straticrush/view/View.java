package straticrush.view;

import straticrush.interaction.IViewListener;
import straticrush.interaction.StratiCrushServices;
import no.geosoft.cc.graphics.GObject;



public abstract class View extends GObject implements IViewListener {

	@Override
	public void destroy() {
		StratiCrushServices.getInstance().removeListener(this);	
	}

	@Override
	public void setUserData (Object object)
	{
		super.setUserData(object);
		StratiCrushServices.getInstance().addListener(this);	
	}
	
}
