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

import com.cedarsoftware.util.io.JsonWriter;
import fr.ifp.kronosflow.model.style.IStyleProvider;
import fr.ifp.kronosflow.model.style.Style;
import fr.ifp.kronosflow.model.wrapper.IWrapper;
import fr.ifp.kronosflow.model.wrapper.WrapperFactory;
import fr.ifp.kronosflow.uids.IHandle;
import fr.ifp.kronosflow.uids.UID;
import fr.ifp.kronosflow.utils.LOGGER;
import stratifx.model.json.JSONParameters;
import stratifx.model.json.JSONSection;
import stratifx.model.persistable.AbstractPersisted;
import stratifx.model.persistable.PersistableSection;
import stratifx.model.persistable.PersistedParameters;
import stratifx.model.wrappers.SectionWrapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URL;


/**
 * Singleton to store parameters used in application.
 *
 * @author lecomtje
 *
 */
public class GParameters  implements IHandle, IStyleProvider {

    UID uid;

    Style style;

    boolean formatted = true;

    static private GParameters instance = new GParameters();

    static public GParameters instance(){
        if ( instance == null ){
            instance = new GParameters();
        }

        return instance;
    }

    public static Style getInstanceStyle() {
        return instance().getStyle();
    }

    @Override
    public UID getUID() {
        return uid;
    }

    @Override
    public Style getStyle() {
        return style;
    }

    private GParameters() {
        uid = new UID();
        style = new Style();
        style.setAttribute("name", "GParameters");
    }

    public void load(){
    }


    public void save(){
        URL url = Main.class.getResource("/stratifx/config.properties");

        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(url.getFile()));


            JSONParameters jsonSection = new JSONParameters(new PersistedParameters(this));

            if (formatted) {
                output.write(JsonWriter.formatJson(jsonSection.toJSONString()));
            } else {
                output.write(jsonSection.toJSONString());
            }

            output.close();

        }
        catch( Exception ex ){
            LOGGER.error("unable to save GParameters in config.propeties", getClass());
        }


    }

}
