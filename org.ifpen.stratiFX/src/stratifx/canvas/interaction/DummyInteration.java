/* 
 * Copyright 2017 lecomtje.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
