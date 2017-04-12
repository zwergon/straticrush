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

import fr.ifp.kronosflow.model.style.Style;
import fr.ifp.kronosflow.model.style.StyleManager;


/**
 * Singleton to store parameters used in application.
 *
 * @author lecomtje
 *
 */
public class GParameters {

    Style style;

    static private GParameters instance = new GParameters();

    private GParameters() {
        style = StyleManager.getInstance().createStyle();
        style.setAttribute("name", "GParameters");
    }

    static public Style getStyle() {
        return instance.style;
    }

}
