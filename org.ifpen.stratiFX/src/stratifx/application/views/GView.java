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
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GStyle;


/**
 * This is an abstract object to handle specific {@link GObject}
 * that are displayed in {@link Plot}.
 * @author lecomtje
 *
 */
public abstract class GView extends GObject {

	public GView(String name){
		super(name);
	}

	public GView(){
		super();
	}

	public void useSelectedStyle(){
		GStyle gstyle = getStyle();
		if ( gstyle != null ){
			if ( gstyle.getBackgroundColor() != null ) {
				gstyle.setBackgroundColor( gstyle.getBackgroundColor().brighter() );
			}
			if ( gstyle.getForegroundColor() != null ) {
				gstyle.setForegroundColor( gstyle.getForegroundColor().brighter() );
			}
			gstyle.setLineWidth( gstyle.getLineWidth() + 2 );
		}
		redraw();
	}

	public Object getModel(){
		return getUserData();
	}

	public abstract void setModel( Object object );

	public abstract void modelChanged( IControllerEvent<?> event );

}
