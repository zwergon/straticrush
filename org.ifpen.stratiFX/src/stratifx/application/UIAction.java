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
package stratifx.application;

public class UIAction {

    final public static int ZOOMONEONE = 1;
    final public static int OPEN = 2;
    final public static int SHOWPOINTS = 3;
    final public static int ZOOMRECT = 4;

    final public static int LAST = ZOOMRECT + 1;

    public static final int INTERACTION = UIAction.LAST + 1;
    public static final int PROPERTIES = UIAction.LAST + 2;

    int type;

    public UIAction(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
