package stratifx.application.bl2d;

import fr.ifp.kronosflow.utils.LOGGER;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import stratifx.application.main.GParameters;
import stratifx.application.main.IUIController;
import stratifx.application.main.UIAction;

import java.net.URL;
import java.util.ResourceBundle;

public class BL2DEnvUIController implements Initializable, IUIController {

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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        element.getItems().addAll(
                "p1",
                "q1.0",
                "q1.1"
        );
        EnvStyle envStyle = new EnvStyle(GParameters.getStyle());

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

        bl2dPane.setVisible(false);

    }

    @Override
    public boolean handleAction(UIAction action) {
        return false;
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
}
