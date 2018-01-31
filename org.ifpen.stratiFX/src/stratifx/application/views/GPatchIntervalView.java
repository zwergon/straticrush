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
package stratifx.application.views;

import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GStyle;

public class GPatchIntervalView extends GView {
	
	private GInterval gline;

	public void setModel (Object object)
	{
		setUserData( object );
		
		PatchInterval interval = getPatchInterval();
			
		gline = new GInterval( interval.getInterval() );
		add(gline);
		
		gline.draw();

		
		GStyle style = new GStyle();
		style.setForegroundColor ( new GColor( interval.getColor().getRGB() ) );
		style.setLineWidth (3);
		setStyle (style);	
	}
	
	
	PatchInterval getPatchInterval(){
		return (PatchInterval)getUserData();
	}
	
	private Patch getPatch(){
		PatchInterval interval = getPatchInterval();
		return interval.getPatch();
	}
	
	
	@Override
	protected void draw() {
		if ( null != gline ){
			gline.draw();
		}
	}
	
	@Override
	public void modelChanged( IControllerEvent<?> event ) {
		if ( event.getObject() == getPatch() && null != gline ){
			gline.draw();
		}
	}

}
