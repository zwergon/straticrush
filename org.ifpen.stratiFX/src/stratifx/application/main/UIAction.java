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
package stratifx.application.main;


public class UIAction<T> {


    final public static int OPEN = 1;
    final public static int SAVE = 2;
    final public static int LOAD = 3;

    final public static int ZOOMONEONE = 6;
    final public static int ZOOMRECT = 7;
    

    final public static int LAST = ZOOMRECT + 1;

    public static final int INTERACTION = UIAction.LAST + 1;
    public static final int PROPERTIES = UIAction.LAST + 2;
    public static final int STYLE = UIAction.LAST + 3;
    public static final int EVENT = UIAction.LAST + 4;

    private int type;

    private T data;

    protected UIAction(int type) {
        this(type, null);
    }

    protected UIAction(int type, T data){
        this.type = type;
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public T getData() { return data; }
}
