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

import fr.ifp.kronosflow.model.property.EnumProperty;
import java.net.URL;
import java.util.ResourceBundle;

import fr.ifp.kronosflow.utils.LOGGER;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import stratifx.application.interaction.InteractionUIAction;
import stratifx.application.properties.PropertiesUIAction;

public class MainController implements Initializable, IUIController {

    Stage treeStage = null;
    Stage propertyStage = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    void onOneOneAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(UIAction.ZOOMONEONE);
    }
    
     @FXML
    void onZoomRectAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(UIAction.ZOOMRECT);
    }
    
     @FXML
    void onShowPointsAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(UIAction.SHOWPOINTS);
    }

    @FXML
    void onTreeUIAction(ActionEvent action) {

        ToggleButton toggle = (ToggleButton) action.getSource();
        if (toggle.isSelected() && (null == treeStage)) {

            try {
                treeStage = new Stage();
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/TreeUI.fxml"));
                Pane rootLayout = loader.load();
                Scene scene = new Scene(rootLayout);
                treeStage.setScene(scene);
                treeStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        toggle.setSelected(false);
                        treeStage = null;
                    }
                });
                treeStage.show();

                StratiFXService.instance.registerController(IUIController.Type.TREE, loader.getController());

            } catch (Exception ex) {
                LOGGER.error("unable to open dialog" + ex.getMessage(), getClass());

            }

        } else {
            if (null != treeStage) {
                StratiFXService.instance.removeController(IUIController.Type.TREE);
                treeStage.hide();
                treeStage.close();
                treeStage = null;
            }
        }

    }

    @FXML
    void onPropertyUIAction(ActionEvent action) {

        ToggleButton toggle = (ToggleButton) action.getSource();
        if (toggle.isSelected() && (null == propertyStage)) {

            try {
                propertyStage = new Stage();
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/PropertyUI.fxml"));
                Pane rootLayout = loader.load();
                Scene scene = new Scene(rootLayout);
                propertyStage.setScene(scene);
                propertyStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        toggle.setSelected(false);
                        propertyStage = null;
                    }
                });
                propertyStage.show();

                StratiFXService.instance.registerController(IUIController.Type.PROPERTY, loader.getController());

            } catch (Exception ex) {
                LOGGER.error("unable to open dialog" + ex.getMessage(), getClass());

            }

        } else {
            if (null != propertyStage) {
                StratiFXService.instance.removeController(IUIController.Type.PROPERTY);
                propertyStage.hide();
                propertyStage.close();
                propertyStage = null;
            }
        }

    }

    @FXML
    void onMenuOpenAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(UIAction.OPEN);
    }

    @FXML
    void onCloseAction(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void onToolsDisplacementsPatchAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(new InteractionUIAction("Displacements", "PatchDisplacements"));
    }

    @FXML
    void onToolsDisplacementsFaultAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(new InteractionUIAction("Displacements", "FaultDisplacements"));

    }
    
    @FXML
    void onToolsTriangulationAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(new InteractionUIAction("Triangulation", "Triangulation"));
    }

    @FXML
    void onToolsTangentAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(new InteractionUIAction("Tangent", "Tangent"));
    }

    @FXML
    void onToolsResetAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(new InteractionUIAction("Reset", "Reset"));
    }

    @FXML
    void onDeformNodeChainMailAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(new InteractionUIAction("ChainMail", "NodeMove"));
    }

    @FXML
    void onDeformNodeMassAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(new InteractionUIAction("MassSpring", "NodeMove"));
    }

    @FXML
    void onDeformTopVSAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(new InteractionUIAction("VerticalShear", "Top"));
    }

    @FXML
    void onDeformTopMLSAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(new InteractionUIAction("MovingLS", "Top"));
    }

    @FXML
    void onDeformTopFlexuralAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(new InteractionUIAction("FlexuralSlip", "Top"));
    }

    @FXML
    void onDeformTopFEADynamicAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(new InteractionUIAction("Dynamic", "Top"));
    }

    @FXML
    void onDeformTopFEAStaticAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(new InteractionUIAction("Static", "Top"));
    }

    @FXML
    void onDeformTopFEAStaticLSAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(new InteractionUIAction("StaticLS", "Top"));
    }

    @FXML
    void onDeformTopFEAFem2dAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(new InteractionUIAction("FEM2D", "Top"));
    }

    @FXML
    void onDeformDilatationDecompactionAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(new InteractionUIAction("Decompaction", "Dilatation"));
    }

    @FXML
    void onTopologyRemoveUnitAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(new InteractionUIAction("None", "RemoveUnit"));
    }

    @FXML
    void onPropertiesXYAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(new PropertiesUIAction(EnumProperty.XY));
    }

    @FXML
    void onPropertiesPorosityAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(new PropertiesUIAction(EnumProperty.POROSITY));
    }

    @FXML
    void onPropertiesElongationAction(ActionEvent event) {
        StratiFXService.instance.broadCastAction(new PropertiesUIAction(EnumProperty.ELONGATION));
    }

    @Override
    public boolean handleAction(UIAction action) {
        // TODO Auto-generated method stub
        return false;
    }

}
