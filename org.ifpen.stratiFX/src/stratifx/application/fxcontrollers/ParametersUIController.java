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
import fr.ifp.kronosflow.model.style.Style;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.StringConverter;
import stratifx.application.GParameters;
import stratifx.application.IUIController;
import stratifx.application.StratiFXService;
import stratifx.application.UIAction;

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
        
        SceneStyle sceneStyle = new SceneStyle( GParameters.getStyle() );
        gridComboId.getSelectionModel().select(sceneStyle.getGridType());
        gridComboId.setOnAction((event)->{
            sceneStyle.setGridType(gridComboId.getValue().toString());
        });
        
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
    
    public void onSceneResetAction( ActionEvent event ){
        
    }
    
   
  /*  
    @FXML
    public void onTriangulationAction(ActionEvent event){
        SceneStyle sceneStyle = new SceneStyle(GParameters.getStyle());
        sceneStyle.setGridType("Trgl");   
    }
*/
}
