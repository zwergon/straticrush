package stratifx.canvas.interaction;

import stratifx.canvas.graphics.GScene;

public interface GInteraction
{
	
  public boolean start( GScene scene );
  public boolean stop( GScene scene );
  public boolean mouseEvent ( GScene scene, GMouseEvent event );
  public boolean keyEvent   ( GScene scene, GKeyEvent event );
}
