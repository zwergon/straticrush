package stratifx.application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import stratifx.application.interaction.InterationUIAction;

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
	
	@FXML void onDeformResetAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new InterationUIAction("Reset") );
	}

	@FXML void onDeformNodeChainMailAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new InterationUIAction("ChainMail", "Top" ) );
	}

	@FXML void onDeformNodeMassAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new InterationUIAction("MassSpring", "Top" ) );
	}
	
	@FXML void onDeformTopVSAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new InterationUIAction("VerticalShear", "Top" ) );
	}
	@FXML void onDeformTopMLSAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new InterationUIAction("MovingLS", "Top" ) );
	}
	@FXML void onDeformTopFlexuralAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new InterationUIAction("FlexuralSlip", "Top" ) );
	}

	@FXML void onDeformTopFEADynamicAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new InterationUIAction("Dynamic", "Top" ) );
	}
	@FXML void onDeformTopFEAStaticAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new InterationUIAction("Static", "Top" ) );
	}
	@FXML void onDeformTopFEAStaticLSAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new InterationUIAction("StaticLS", "Top" ) );
	}
	@FXML void onDeformTopFEAFem2dAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new InterationUIAction("FEM2D", "Top" ) );
	}

	@Override
	public boolean handleAction(UIAction action) {
		// TODO Auto-generated method stub
		return false;
	}

}
