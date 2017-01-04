package stratifx.application.manipulator;

import stratifx.canvas.graphics.GObject;
import stratifx.canvas.interaction.GMouseEvent;

public interface IStratiManipulator {
	
	public boolean isActive();
	public boolean isManipulating();
	
	public void activate();
	public void deactivate();
	
	public GObject getGraphic();
	
	public void onMousePress( GMouseEvent event );
	public void onMouseMove( GMouseEvent event );
	public void onMouseRelease( GMouseEvent event );
	

}
