package stratifx.application.griding.bl2d;

import fr.ifp.kronosflow.utils.LOGGER;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import stratifx.application.main.GParameters;

import java.net.URL;
import java.util.ResourceBundle;

public class BL2DEnvUIController implements Initializable {

    @FXML
    AnchorPane bl2dPane;

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
    Label bl2denv;

    @FXML
    Label bp;

    @FXML
    ComboBox bps;

    @FXML
    Label ic;

    @FXML
    ComboBox ics;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        element.getItems().addAll(
                "Triangular",
                "Quad-dominant",
                "Quadrangular"
        );

        bps.getItems().addAll(
            "No",
                "Yes"
        );

        ics.getItems().addAll(
                "None",
                "Inner Points",
                "Inner Curves",
                "Inner Segments"
        );

        EnvStyle envStyle = new EnvStyle(GParameters.getStyle());

        element.setValue(envStyle.getEnvElement());

        bps.setValue(envStyle.getBORDERPOINTS());

        ics.setValue(envStyle.getINNERCONTACTS());

        if(envStyle.getEnvVerb()!=null){
            verb.setText(envStyle.getEnvVerb());
        }

        if(envStyle.getEnvHmin()!=null){
            hmin.setText(envStyle.getEnvHmin());
        }
        if(envStyle.getEnvHmax()!=null){
            hmax.setText(envStyle.getEnvHmax());
        }

        bl2dPane.setVisible(false);

    }

    @FXML
    public void onEnvApplyAction(ActionEvent action){
        LOGGER.debug("onEnvApplyAction",getClass());
        EnvStyle envStyle = new EnvStyle(GParameters.getStyle());
        envStyle.setEnvElement(element.getValue().toString());
        envStyle.setBORDERPOINTS(bps.getValue().toString());
        envStyle.setINNERCONTACTS(ics.getValue().toString());

        if(!verb.getText().isEmpty()){
            envStyle.setEnvVerb(verb.getText());
        }
        else{
            envStyle.setEnvVerb(null);
        }

        if(!hmin.getText().isEmpty()){
            envStyle.setEnvHmin(hmin.getText());
        }
        else {
            envStyle.setEnvHmin(null);
        }

        if(!hmax.getText().isEmpty()){
            envStyle.setEnvHmax(hmax.getText());
        }
        else {
            envStyle.setEnvHmax(null);
        }
    }
}
