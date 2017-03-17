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

public interface GInteraction
{
	
  public boolean start( GScene scene );
  public boolean stop( GScene scene );
  public boolean mouseEvent ( GScene scene, GMouseEvent event );
  public boolean keyEvent   ( GScene scene, GKeyEvent event );
}
