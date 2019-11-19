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
import fr.ifp.kronosflow.model.factory.SectionStyle;
import fr.ifp.kronosflow.model.geology.BoundaryFeature;
import fr.ifp.kronosflow.model.geology.FaultFeature;
import fr.ifp.kronosflow.model.geology.GeologicLibrary;
import fr.ifp.kronosflow.model.geology.StratigraphicEvent;
import fr.ifp.kronosflow.utils.LOGGER;
import fr.ifpen.kine.client.RegistrationException;
import fr.ifpen.kine.client.SimulationClient;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import stratifx.application.main.GParameters;
import stratifx.application.main.IUIController;
import stratifx.application.main.StratiFXService;
import stratifx.application.main.UIAction;
import stratifx.application.views.DisplayStyle;
import stratifx.application.views.StyleUIAction;
import stratifx.application.webkine.WebServiceStyle;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 *
 * @author lecomtje
 */
public class ParametersUIController implements
        Initializable,
        IUIController {

    @FXML
    ListView<ParametersUIController.FXFeature> fxFeaturesListId;

    private final ObservableList<ParametersUIController.FXFeature> data = FXCollections.observableArrayList();


    @FXML
    CheckBox displayWithSolidId;

    @FXML
    CheckBox displayWithLineId;

    @FXML
    CheckBox displayWithSymbolId;

    @FXML
    CheckBox displayWithAnnotationId;

    @FXML
    ComboBox gridingType;

    @FXML
    VBox Griding;

    Pane gridingPane;

    Map<String, Pane> gridingMap = new HashMap<>();

    @FXML
    ComboBox solverType;

    @FXML
    VBox Solvers;

    Pane solverPane;


    @FXML
    TextField hostText;

    @FXML
    Spinner<Integer> portSpinner;

    @FXML
    TextField loginText;

    @FXML
    PasswordField passwdField;

    @FXML
    CheckBox proxyCB;

    @FXML
    TextField proxyHostText;

    @FXML
    Spinner<Integer> proxyPortSpinner;


    GeoschedulerSection section;
    

    class FXFeature {

        BoundaryFeature feature;

        private BooleanProperty selected = new SimpleBooleanProperty(false);

        public FXFeature(BoundaryFeature feature) {
            this.feature = feature;

            SectionStyle sceneStyle = section.getSectionStyle();
            if ( (feature instanceof StratigraphicEvent) && !sceneStyle.hasUnusualBehaviour(feature) ) {
                selected.setValue(true);
            }
            
            if ( ( feature instanceof FaultFeature ) && sceneStyle.hasUnusualBehaviour(feature) ){
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
        

        
        section = (GeoschedulerSection) StratiFXService.instance.getSection();
        if (section != null) {
            initSceneParameters(section);
        }

        DisplayStyle displayStyle = new DisplayStyle( GParameters.getInstanceStyle() );
        displayWithLineId.setSelected(displayStyle.getWithLines());
        displayWithSolidId.setSelected(displayStyle.getWithSolid());
        displayWithSymbolId.setSelected(displayStyle.getWithSymbol());
        displayWithAnnotationId.setSelected(displayStyle.getWithAnnotation());


        initGridingTab();

        initSolverTab();

        initWebServiceTab();

    }

    private void initSolverTab() {

        ObservableList<String> items = solverType.getItems();

        for( ISolverParameter parameter : MenuParamInfo.getParameters( ISolverParameter.class )){
            ParamInfo mpi = (ParamInfo)parameter;

            String key = mpi.getKey();

            items.add(key);

            if ( mpi.getFxmlFile() != null ) {
                try {
                    Pane pane = FXMLLoader.load(getClass().getResource(mpi.getFxmlFile()));
                    Solvers.getChildren().add(pane);
                    mpi.setPane(pane);


                } catch (IOException ex) {
                    LOGGER.error("unable to create inside gridingPane", getClass());
                }
            }

        }

        if ( !items.isEmpty() ) {
            solverType.setValue(items.sorted().get(0));
            onSolverTypeAction();
        }

        solverPane = null;

        solverType.setOnAction((event)->{
            onSolverTypeAction();
        });


    }

    private void initWebServiceTab() {

        WebServiceStyle serviceStyle = new WebServiceStyle(GParameters.getInstanceStyle());

        hostText.setText( serviceStyle.getHost() );
        portSpinner.getValueFactory().setValue( serviceStyle.getPort() );
        loginText.setText( serviceStyle.getLogin());
        passwdField.setText(serviceStyle.getPassWord());


        boolean hasProxy = serviceStyle.hasProxy();
        proxyCB.setSelected(hasProxy);
        proxyHostText.setDisable(!hasProxy);
        proxyPortSpinner.setDisable(!hasProxy);
        proxyHostText.setText(serviceStyle.getProxy());
        proxyPortSpinner.getValueFactory().setValue( serviceStyle.getProxyPort() );

        proxyCB.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                proxyHostText.setDisable(!newValue);
                proxyPortSpinner.setDisable(!newValue);
            }
        });

    }

    private void initGridingTab() {

        ObservableList<String> items = gridingType.getItems();

        for( IGridingParameter parameter : MenuParamInfo.getParameters( IGridingParameter.class )){
            MenuParamInfo mpi = (MenuParamInfo)parameter;

            String key = mpi.getKey();

            items.add(key);

            if ( mpi.getFxmlFile() != null ) {
                try {
                    Pane pane = FXMLLoader.load(getClass().getResource(mpi.getFxmlFile()));
                    Griding.getChildren().add(pane);
                    mpi.setPane(pane);


                } catch (IOException ex) {
                    LOGGER.error("unable to create inside gridingPane", getClass());
                }
            }

        }

        if ( !items.isEmpty() ) {
            SceneStyle sceneStyle = new SceneStyle( GParameters.getInstanceStyle() );
            gridingType.setValue(sceneStyle.getGridType());
            onGridingTypeAction();
        }

        gridingPane = null;

        gridingType.setOnAction((event)->{
            onGridingTypeAction();
        });


    }



    private void onGridingTypeAction() {
        String key = gridingType.getValue().toString();

        SceneStyle sceneStyle = new SceneStyle( GParameters.getInstanceStyle() );
        sceneStyle.setGridType(key);


        ParamInfo gpi = MenuParamInfo.getParamInfo(key);
        gridingPane = gpi.getPane();
        if ( gridingPane != null ){
            gridingPane.setVisible(true);
            gridingPane.setManaged(true);
        }

        for( Node node : Griding.getChildren() ){
            if ( node instanceof Pane){
                Pane pane = (Pane)node;
                if ( pane != gridingPane ){
                    pane.setVisible(false);
                    pane.setManaged(false);
                }
            }
        }


    }

    private void onSolverTypeAction() {
        String key = solverType.getValue().toString();

        ParamInfo gpi = MenuParamInfo.getParamInfo(key);
        solverPane = gpi.getPane();
        if ( solverPane != null ){
            solverPane.setVisible(true);
        }

        for( Node node : Solvers.getChildren() ){
            if ( node instanceof Pane){
                Pane pane = (Pane)node;
                if ( pane != solverPane ){
                    pane.setVisible(false);
                }
            }
        }
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

        SectionStyle sceneStyle  = new SectionStyle( section.getStyle());
        for( FXFeature fxFeature : data ){
            if ( fxFeature.feature instanceof StratigraphicEvent  ) {
                sceneStyle.setUnusualBehavior(fxFeature.feature, !fxFeature.isSelected() );
            }
            else if ( fxFeature.feature instanceof FaultFeature ){
                sceneStyle.setUnusualBehavior(fxFeature.feature, fxFeature.isSelected() );
            }
        }
        
    }

    @FXML
    public void onDisplayApplyAction( ActionEvent action ){

        LOGGER.debug("onDialogApplyAction", getClass());

        DisplayStyle displayStyle = new DisplayStyle( GParameters.getInstanceStyle() );

        displayStyle.setWithLines(displayWithLineId.isSelected());
        displayStyle.setWithSolid(displayWithSolidId.isSelected());
        displayStyle.setWithSymbol(displayWithSymbolId.isSelected());
        displayStyle.setWithAnnotation(displayWithAnnotationId.isSelected());

        StratiFXService.instance.broadCastAction(
                new StyleUIAction(displayStyle.getStyle())
        );

    }

    @FXML
    public void onWebServiceApplyAction(ActionEvent action ){

        WebServiceStyle serviceStyle = new WebServiceStyle(GParameters.getInstanceStyle());
        serviceStyle.setLogin(loginText.getText());
        serviceStyle.setPassWord(passwdField.getText());
        serviceStyle.setHost(hostText.getText());
        serviceStyle.setPort(portSpinner.getValue());
        if ( proxyCB.isSelected() ){
            serviceStyle.setProxy(proxyHostText.getText(), proxyPortSpinner.getValue());
        }
        else {
            serviceStyle.setProxy("", 0);
        }

        StratiFXService.instance.broadCastAction(
                new StyleUIAction(serviceStyle.getStyle())
        );
    }

    @FXML
    public void onSceneResetAction( ActionEvent event ){

    }


    @FXML
    public void onWebServiceRegisterAction( ActionEvent event ) {

        WebServiceStyle serviceStyle = new WebServiceStyle(GParameters.getInstanceStyle());
        SimulationClient simulationClient = new SimulationClient();
        simulationClient.setBaseUrl(serviceStyle.getBaseUrl());

        String login = loginText.getText();
        String passwd = passwdField.getText();

        try {
            simulationClient.register(login, passwd);
        }
        catch( Exception exception ){
            if ( exception instanceof  RegistrationException ){

                LOGGER.error("unable to register user " + login + " : " + exception.getMessage(), getClass());
            }

        }
    }

}
