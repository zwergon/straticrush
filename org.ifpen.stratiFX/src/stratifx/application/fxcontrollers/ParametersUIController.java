/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.fxcontrollers;

import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.model.factory.SceneStyle;
import fr.ifp.kronosflow.model.geology.BoundaryFeature;
import fr.ifp.kronosflow.model.geology.FaultFeature;
import fr.ifp.kronosflow.model.geology.GeologicLibrary;
import fr.ifp.kronosflow.model.geology.StratigraphicEvent;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import fr.ifp.kronosflow.utils.LOGGER;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import stratifx.application.bl2d.EnvStyle;
import stratifx.application.main.GParameters;
import stratifx.application.main.IUIController;
import stratifx.application.main.StratiFXService;
import stratifx.application.main.UIAction;
import stratifx.application.views.DisplayStyle;
import stratifx.application.views.StyleUIAction;

/**
 *
 * @author lecomtje
 */
public class ParametersUIController implements
        Initializable,
        IUIController {

    @FXML
    ListView<ParametersUIController.FXFeature> fxFeaturesListId;
    
    @FXML
    ComboBox gridComboId;

    @FXML
    CheckBox displayWithSolidId;

    @FXML
    CheckBox displayWithLineId;

    @FXML
    CheckBox displayWithSymbolId;

    @FXML
    CheckBox displayWithAnnotationId;

    @FXML
    ComboBox element;

    @FXML
    TextField verb;

    @FXML
    TextField hmin;

    @FXML
    TextField hmax;

    @FXML
    Label el;

    @FXML
    Label verbl;

    @FXML
    Label hminl;

    @FXML
    Label hmaxl;

    @FXML
    ComboBox griding;

    @FXML
    Label bl2denv;

    @FXML
    HBox appl;

    private final ObservableList<ParametersUIController.FXFeature> data = FXCollections.observableArrayList();
    
    GeoschedulerSection section;
    
    SceneStyle sceneStyle;

    class FXFeature {

        BoundaryFeature feature;

        private BooleanProperty selected = new SimpleBooleanProperty(false);

        public FXFeature(BoundaryFeature feature) {
            this.feature = feature;
            if ( (feature instanceof StratigraphicEvent) && !sceneStyle.hasUnusualBehaviour(feature, section) ) {
                selected.setValue(true);
            }
            
            if ( ( feature instanceof FaultFeature ) && sceneStyle.hasUnusualBehaviour(feature, section) ){
               selected.setValue(true); 
            }
        }

        public String getName() {
            String name = feature.getName();
            if (feature instanceof StratigraphicEvent) {
                name += " (Horizon)";
            }
            return name;
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }

        public boolean isSelected() {
            return selected.get();
        }

        public void setSelected(boolean selected) {
            this.selected.set(selected);
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        sceneStyle  = new SceneStyle( GParameters.getStyle() ); 
        
        section = (GeoschedulerSection) StratiFXService.instance.getSection();
        if (section != null) {
            initSceneParameters(section);
        }
        
        gridComboId.getItems().addAll( 
                "None", 
                "Trgl", 
                "Grid2D", 
                "Compact2D",
                "StratiGrid"
        );

        element.getItems().addAll(
                "p1",
                "q1.0",
                "q1.1"
        );

        griding.getItems().addAll(
                "None",
                "BL2DMesh"
        );


        EnvStyle envStyle = new EnvStyle(GParameters.getStyle());

        griding.setOnAction((event)->{
            if(griding.getValue().toString().equals("BL2DMesh")){
                bl2denv.setVisible(true);
                el.setVisible(true);
                element.setVisible(true);
                verbl.setVisible(true);
                verb.setVisible(true);
                hminl.setVisible(true);
                hmin.setVisible(true);
                hmaxl.setVisible(true);
                hmax.setVisible(true);
            }
            else{
                bl2denv.setVisible(false);
                el.setVisible(false);
                element.setVisible(false);
                verbl.setVisible(false);
                verb.setVisible(false);
                hminl.setVisible(false);
                hmin.setVisible(false);
                hmaxl.setVisible(false);
                hmax.setVisible(false);
            }
        });
        if(envStyle.getEnvElement()!=null){
            element.setValue(envStyle.getEnvElement());
        }
        else{
            element.setValue("p1");
        }

        if(envStyle.getEnvVerb()!=null){
            verb.setText(envStyle.getEnvVerb().toString());
        }
        if(envStyle.getEnvHmin()!=null){
            hmin.setText(envStyle.getEnvHmin());
        }
        if(envStyle.getEnvHmax()!=null){
            hmax.setText(envStyle.getEnvHmax());
        }

        SceneStyle sceneStyle = new SceneStyle( GParameters.getStyle() );
        gridComboId.getSelectionModel().select(sceneStyle.getGridType());
        gridComboId.setOnAction((event)->{
            sceneStyle.setGridType(gridComboId.getValue().toString());
        });

        DisplayStyle displayStyle = new DisplayStyle( GParameters.getStyle() );
        displayWithLineId.setSelected(displayStyle.getWithLines());
        displayWithSolidId.setSelected(displayStyle.getWithSolid());
        displayWithSymbolId.setSelected(displayStyle.getWithSymbol());
        displayWithAnnotationId.setSelected(displayStyle.getWithAnnotation());

    }

    private void initSceneParameters(GeoschedulerSection section) {
        GeologicLibrary geologicLibrary = section.getFeatures();
        
        List<BoundaryFeature> features = geologicLibrary.getGeologicFeaturesByClass(BoundaryFeature.class);
        //sort against their names.
        Collections.sort(features, new Comparator<BoundaryFeature>() {
            @Override
            public int compare(BoundaryFeature o1, BoundaryFeature o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        
        fxFeaturesListId.setCellFactory(CheckBoxListCell.forListView(
                FXFeature::selectedProperty,
                new StringConverter<FXFeature>() {
                    @Override
                    public String toString(FXFeature object) {
                        return object.getName();
                    }
                    
                    @Override
                    public FXFeature fromString(String string) {
                        return null;
                    }
                }));
        ;
        for (BoundaryFeature feature : features) {
            data.add(new FXFeature(feature));
        }
        fxFeaturesListId.setItems(data);
        
        
    }

    @Override
    public boolean handleAction(UIAction action) {
        return false;
    }
    

    @FXML
    public void onSceneApplyAction( ActionEvent event ){



        for( FXFeature fxFeature : data ){
            if ( fxFeature.feature instanceof StratigraphicEvent  ) {
                sceneStyle.setUnusualBehavior(section, fxFeature.feature, !fxFeature.isSelected() );
            }
            else if ( fxFeature.feature instanceof FaultFeature ){
                sceneStyle.setUnusualBehavior(section, fxFeature.feature, fxFeature.isSelected() );
            }
        }
        
    }

    @FXML
    public void onDisplayApplyAction( ActionEvent action ){

        LOGGER.debug("onDialogApplyAction", getClass());

        DisplayStyle displayStyle = new DisplayStyle( GParameters.getStyle() );

        displayStyle.setWithLines(displayWithLineId.isSelected());
        displayStyle.setWithSolid(displayWithSolidId.isSelected());
        displayStyle.setWithSymbol(displayWithSymbolId.isSelected());
        displayStyle.setWithAnnotation(displayWithAnnotationId.isSelected());

        StratiFXService.instance.broadCastAction(
                new StyleUIAction(displayStyle.getStyle())
        );

    }

    @FXML
    public void onEnvApplyAction(ActionEvent action){
        LOGGER.debug("onEnvApplyAction",getClass());
        EnvStyle envStyle = new EnvStyle(GParameters.getStyle());
        envStyle.setEnvElement(element.getValue().toString());
        if(!verb.getText().isEmpty()){
            envStyle.setEnvVerb(Integer.valueOf(verb.getText()));
        }
        else{
            envStyle.removeEnvVerb();
        }
        if(!hmin.getText().isEmpty()){
            envStyle.setEnvHmin(hmin.getText());
        }
        else{
            envStyle.removeEnvHmin();
        }
        if(!hmax.getText().isEmpty()){
            envStyle.setEnvHmax(hmax.getText());
        }
        else{
            envStyle.removeEnvHmax();
        }
    }

    @FXML
    public void onSceneResetAction( ActionEvent event ){
        
    }
    

}
