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
package stratifx.canvas.graphics;

public interface ICanvas {
	
	public void refresh();
	
	public void setClipArea(GRegion damageRegion);
	public void clear(GRect extent);
		
	public void render ( GSegment segment, GStyle style );
	public void render (GText text, GStyle style);
	public void render (GImage image);
	public void render (int[] x, int[] y, GImage image);
	
	public GRect getStringBox (String string, GFont font);
	
	
	  
}
