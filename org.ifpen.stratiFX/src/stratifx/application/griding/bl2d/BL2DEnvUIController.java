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

        BL2DStyle BL2DStyle = new BL2DStyle(GParameters.getInstanceStyle());

        element.setValue(BL2DStyle.getEnvElement());

        bps.setValue(BL2DStyle.getBORDERPOINTS());

        ics.setValue(BL2DStyle.getINNERCONTACTS());

        if(BL2DStyle.getEnvVerb()!=null){
            verb.setText(BL2DStyle.getEnvVerb());
        }

        if(BL2DStyle.getEnvHmin()!=null){
            hmin.setText(BL2DStyle.getEnvHmin());
        }
        if(BL2DStyle.getEnvHmax()!=null){
            hmax.setText(BL2DStyle.getEnvHmax());
        }

        bl2dPane.setVisible(false);

    }

    @FXML
    public void onEnvApplyAction(ActionEvent action){
        LOGGER.debug("onEnvApplyAction",getClass());
        BL2DStyle BL2DStyle = new BL2DStyle(GParameters.getInstanceStyle());
        BL2DStyle.setEnvElement(element.getValue().toString());
        BL2DStyle.setBORDERPOINTS(bps.getValue().toString());
        BL2DStyle.setINNERCONTACTS(ics.getValue().toString());

        if(!verb.getText().isEmpty()){
            BL2DStyle.setEnvVerb(verb.getText());
        }
        else{
            BL2DStyle.setEnvVerb(null);
        }

        if(!hmin.getText().isEmpty()){
            BL2DStyle.setEnvHmin(hmin.getText());
        }
        else {
            BL2DStyle.setEnvHmin(null);
        }

        if(!hmax.getText().isEmpty()){
            BL2DStyle.setEnvHmax(hmax.getText());
        }
        else {
            BL2DStyle.setEnvHmax(null);
        }
    }
}
