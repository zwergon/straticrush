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
package stratifx.application.graph;

import fr.ifp.jdeform.scene.Scene;
import fr.ifp.jdeform.scene.SceneBuilder;
import fr.ifp.jdeform.scene.network.FaultNetwork;
import fr.ifp.jdeform.scene.network.FaultNetworkBuilder;
import fr.ifp.jdeform.scene.network.IFaultNetworkBuilder;
import fr.ifp.jdeform.scene.sequence.DefaultSequenceBuilder;
import fr.ifp.jdeform.scene.sequence.ISequenceBuilder;
import fr.ifp.jdeform.scene.sequence.Sequence;
import fr.ifp.kronosflow.model.Section;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import stratifx.application.main.GParameters;
import stratifx.application.main.IUIController;
import stratifx.application.main.StratiFXService;
import stratifx.application.main.UIAction;

import java.net.URL;
import java.util.ResourceBundle;

public class GraphUIController
        implements
        Initializable,
        IUIController {

    GraphFX graphFX;

    @FXML
    ScrollPane paneId;

    @Override
    public boolean handleAction(UIAction action) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Section section = StratiFXService.instance.getSection();

        if ( section == null ){
            return;
        }

        Scene scene = SceneBuilder.createDefaultScene(section.getPatchLibrary(), GParameters.getStyle());

        IFaultNetworkBuilder builder = new FaultNetworkBuilder();

        FaultNetwork network = builder.create(scene);


        graphFX = new GraphFX(network.getGraph());
        graphFX.initialize();

        Layout layout = new RandomLayout(graphFX);
        layout.execute();

        paneId.setContent(graphFX.getCellLayer());

    }



}
