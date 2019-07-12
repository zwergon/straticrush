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
package stratifx.application.interaction.deform;

import java.util.Timer;
import java.util.TimerTask;

import fr.ifp.kronosflow.deform.controllers.DeformationController;
import javafx.application.Platform;

class DeformationAnimation extends TimerTask {
	
	private int refreshDelay = 100;

	DeformationInteraction interaction;
	
	boolean isStopped = false;

	boolean isUpdating = false;
	
	static public Timer start(DeformationInteraction interaction){
		DeformationAnimation animation = new DeformationAnimation(interaction);
		
		Timer timer =  new Timer(true);
		timer.schedule(animation, 0, animation.refreshDelay );
		
		return timer;
	}
	
	private DeformationAnimation( DeformationInteraction interaction ){
		this.interaction = interaction;
	}
	
	@Override
	public void run() {
		
		if (isStopped || isUpdating ){
			return;
		}
		
		DeformationController controller = interaction.getController();
		
		switch( controller.getState() ){
		case DeformationController.DEFORMING:
			Platform.runLater( new Runnable() {
				@Override
				public void run() {
				    isUpdating = true;
				    interaction.update( );
				    isUpdating = false;
				}
			});
			break;
		case DeformationController.DEFORMED:
		case DeformationController.CANCELED:
		case DeformationController.FAILED:
			Platform.runLater( new Runnable() {
				@Override
				public void run() {
					interaction.end();
				}
			});
			
			isStopped = true;
			break;
		default:
			break;
		}
					
		
		
		
	}
}

