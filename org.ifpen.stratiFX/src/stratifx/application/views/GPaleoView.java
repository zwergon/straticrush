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

import java.awt.Color;

import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.model.geology.Paleobathymetry;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GStyle;

public class GPaleoView extends GView {
	
	GPolyline gline = null;
	
	@Override
	public void setModel (Object object)
	{
		setUserData( object );
		
		createLine( (Paleobathymetry)object );	
	}
	


	@Override
	public void modelChanged( IControllerEvent<?> event ) {
		if ( event.getObject() == getUserData() ){
			redraw();
		}
	}

	
	private void createLine( Paleobathymetry bathy ){
		
		Color bcolor = bathy.getColor();
	
		gline = new GPolyline( bathy.getPolyline() );
		
		GStyle style = new GStyle();
		style.setForegroundColor(  new GColor( bcolor.getRed(), bcolor.getGreen(), bcolor.getBlue() ) );
		style.setLineWidth(1);
		gline.setStyle(style);
		
		add(gline);
		
		setVisibility( GObject.DATA_VISIBLE );
			
	}
}
