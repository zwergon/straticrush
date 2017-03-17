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

import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GFXSymbol;
import stratifx.canvas.graphics.GImage;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;

public class GTranslateObject extends GObject {

	GSegment line;
	
	int[] xy = new int[4];
	
	public GTranslateObject(){
		super("Translate");
		setVisibility( DATA_VISIBLE | SYMBOLS_VISIBLE );
	}
	
	public int[] getStart(){
		int[] start = new int[2];
		start[0] = xy[0];
		start[1] = xy[1];
		return start;
	}
	
	public int[] getEnd(){
		int[] end = new int[2];
		end[0] = xy[2];
		end[1] = xy[3];
		return end;
	}
	
	public int[] getDisplacement(){
		int[] displacement = new int[2];
		displacement[0] = xy[2]-xy[0];
		displacement[1] = xy[3]-xy[1];
		return displacement;
	}
	
	
	public void createMarker( int x, int y ){
		
		line = new GSegment();
		addSegment(line);
		
		GStyle style = new GStyle();
		style.setForegroundColor(GColor.BLUE);
		style.setLineWidth (2);
		line.setStyle (style);
		
		
		GStyle symbolStyle = new GStyle();
		symbolStyle.setForegroundColor (new GColor (0, 0, 255));
		symbolStyle.setBackgroundColor (new GColor (0, 0, 255));
		GImage square = new GFXSymbol (GFXSymbol.SYMBOL_SQUARE1);
		square.setStyle (symbolStyle);

		line.setVertexImage (square);
		
		xy[0] = xy[2] = x;
		xy[1] = xy[3] = y;
		line.setGeometry(xy);
			
	}
	
	
	public void moveTo( int x, int y ){
		if ( null != line ){
			xy[2] = x;
			xy[3] = y;
			line.setGeometry(xy);
		}
	}
}
