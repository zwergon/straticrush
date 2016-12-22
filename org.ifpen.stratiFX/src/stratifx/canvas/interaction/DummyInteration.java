package stratifx.canvas.interaction;

import stratifx.canvas.graphics.GScene;

public class DummyInteration implements GInteraction {

	boolean clicked;
	
	@Override
	public boolean mouseEvent(GScene scene, GMouseEvent event) {
		
		switch( event.getType() ){
		case GMouseEvent.BUTTON_DOWN:
			clicked = true;
			break;
		case GMouseEvent.BUTTON_UP:
			System.out.println(event);
			clicked = false;
			break;
		}
		
		if ( clicked ){
			System.out.println(event);
		}
		return true;
	}

	@Override
	public boolean keyEvent(GScene scene, GKeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean start(GScene scene) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stop(GScene scene) {
		// TODO Auto-generated method stub
		return false;
	}

	

}
