package stratifx.application.griding.grid2d;

import fr.ifp.kronosflow.utils.LOGGER;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class Grid2DParamUIController implements Initializable {

    @FXML
    AnchorPane grid2dPane;

    @FXML
    TextField nxText;

    @FXML
    TextField nyText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        grid2dPane.setVisible(false);
    }

    @FXML
    public void onApplyAction(ActionEvent action){
        LOGGER.debug("onApplyAction",getClass());
    }
}
