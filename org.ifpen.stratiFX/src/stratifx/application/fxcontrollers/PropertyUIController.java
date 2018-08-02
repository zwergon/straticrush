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

import fr.ifp.kronosflow.controllers.property.PropertyEvent;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.model.property.PropertyStyle;
import fr.ifp.kronosflow.kernel.property.Property;
import fr.ifp.kronosflow.kernel.property.PropertyDB;
import fr.ifp.kronosflow.kernel.property.PropertyInfo;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import stratifx.application.main.IUIController;
import stratifx.application.main.StratiFXService;
import stratifx.application.main.UIAction;
import stratifx.application.plot.GFXScene;
import stratifx.application.plot.PlotController;

/**
 *
 * @author lecomtje
 */
public class PropertyUIController
        implements
        Initializable,
        IUIController {

    @FXML
    TableView<FXProperty> tableViewId;

    @FXML
    TableColumn<FXProperty, String> nameColumnId;

    @FXML
    TableColumn<FXProperty, String> typeColumnId;

    private final ObservableList<FXProperty> data = FXCollections.observableArrayList();

    static public class FXProperty {

        Property property;

        SimpleStringProperty name;
        SimpleStringProperty kind;

        public FXProperty(Property property) {
            this.property = property;
            PropertyInfo pInfo = property.getPropertyInfo();
            name = new SimpleStringProperty(pInfo.getName());
            kind = new SimpleStringProperty(pInfo.getKind().toString());
        }

        public String getName() {
            return name.get();
        }

        public String getKind() {
            return kind.get();
        }

        public Property getProperty() {
            return property;
        }

    }
    
    
   
    @Override
    public boolean handleAction(UIAction action) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        nameColumnId.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumnId.setCellValueFactory(new PropertyValueFactory<>("kind"));

        GeoschedulerSection section = (GeoschedulerSection) StratiFXService.instance.getSection();
        if (section != null) {
            PropertyDB pDB = section.getPropertyDB();
            for (Property property : pDB.getAllProperties()) {
                data.add(new FXProperty(property));
            }
            tableViewId.setItems(data);
        }

        tableViewId.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> changeProperty(section, newValue));
    }

    private void changeProperty(GeoschedulerSection section, FXProperty fxProperty) {

        PropertyStyle propStyle = new PropertyStyle(section.getStyle());
        propStyle.setCurrent(fxProperty.getProperty());

        PlotController plot = (PlotController) StratiFXService.instance.getController(IUIController.Type.PLOT);
        GFXScene gfxScene = plot.getGFXScene();
        gfxScene.notifyViews(new PropertyEvent(fxProperty.getProperty()));

        gfxScene.refresh();
    }

    @FXML
    public void onResetPropertyAction(ActionEvent event) {

        GeoschedulerSection section = (GeoschedulerSection) StratiFXService.instance.getSection();
        PropertyStyle propStyle = new PropertyStyle(section.getStyle());
        propStyle.setCurrent(null);

        PlotController plot = (PlotController) StratiFXService.instance.getController(IUIController.Type.PLOT);
        GFXScene gfxScene = plot.getGFXScene();
        gfxScene.notifyViews(new PropertyEvent(null));

        gfxScene.refresh();
    }
    
   


}
