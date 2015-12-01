package straticrush.manipulator;

import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GObject;

public interface IStratiManipulator {
	
	public boolean isActive();
	
	public void activate();
	public void deactivate();
	
	public GObject getGraphic();
	
	public void onMousePress( GMouseEvent event );
	public void onMouseMove( GMouseEvent event );
	public void onMouseRelease( GMouseEvent event );
	

}
