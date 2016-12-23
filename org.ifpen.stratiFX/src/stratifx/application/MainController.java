package stratifx.application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class MainController implements Initializable, IUIController {

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	@FXML void onOneOneAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( UIAction.ZoomOneOne );
	}
	
	@FXML void onMenuOpenAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( UIAction.Open );
	}
	
	@FXML void onCloseAction( ActionEvent event ){
		Platform.exit();
		System.exit(0);
	}

	@Override
	public boolean handleAction(UIAction action) {
		// TODO Auto-generated method stub
		return false;
	}

}
