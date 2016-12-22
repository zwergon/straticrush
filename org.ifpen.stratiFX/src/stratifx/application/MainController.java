package stratifx.application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class MainController implements Initializable, IUIController {

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	@FXML void onOneOneAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( UIAction.ZoomOneOne );
	}

	@Override
	public boolean handleAction(UIAction action) {
		// TODO Auto-generated method stub
		return false;
	}

}
