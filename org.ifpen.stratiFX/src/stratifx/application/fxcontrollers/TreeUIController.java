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
package stratifx.application.fxcontrollers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import fr.ifp.kronosflow.geoscheduler.Geoscheduler;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerStep;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerTree;
import fr.ifp.kronosflow.model.graph.GraphEdge;
import fr.ifp.kronosflow.model.graph.Vertex;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import stratifx.application.IUIController;
import stratifx.application.StratiFXService;
import stratifx.application.UIAction;

public class TreeUIController
        implements
        Initializable,
        IUIController {

    @FXML
    TreeView<String> treeViewId;

    @Override
    public boolean handleAction(UIAction action) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        GeoschedulerSection section = (GeoschedulerSection) StratiFXService.instance.getSection();
        if (section != null) {
            Geoscheduler scheduler = section.getGeoscheduler();
            initializeTreeView(scheduler.getTree());
        }
    }

    private void addItem(TreeItem<String> item, GeoschedulerStep step) {
        GeoschedulerTree tree = step.getTree();

        List<TreeItem<String>> items = item.getChildren();;

        for (GraphEdge edge : tree.getEdgesOut(step)) {
            GeoschedulerStep child = tree.getTo(edge);

            TreeItem<String> childItem = new TreeItem<>(child.getName());

            addItem(childItem, child);

            items.add(childItem);

        }
    }

    private void initializeTreeView(GeoschedulerTree tree) {

        GeoschedulerStep root = tree.getRoot();
        TreeItem<String> rootItem = new TreeItem<>(tree.getRoot().getName());
        rootItem.setExpanded(true);

        addItem(rootItem, root);

        treeViewId.setRoot(rootItem);

    }

}
