package stratifx.application;

import java.net.URL;
import java.util.ResourceBundle;

import fr.ifp.kronosflow.utils.LOGGER;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import stratifx.application.interaction.InteractionUIAction;
import stratifx.application.properties.PropertiesUIAction;

public class MainController implements Initializable, IUIController {
	
	Stage treeStage = null;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	@FXML void onOneOneAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( UIAction.ZoomOneOne );
	}
	
	@FXML void onTreeUIAction( ActionEvent action ){
		
		ToggleButton toggle = (ToggleButton)action.getSource();
		if ( toggle.isSelected() && ( null == treeStage ) ){
			
			try  {
				treeStage = new Stage();
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("../../fxml/TreeUI.fxml"));
				Pane rootLayout = loader.load();
				Scene scene = new Scene(rootLayout);
				treeStage.setScene(scene);
				treeStage.show();
				
				StratiFXService.instance.registerController( IUIController.Type.TREE, loader.getController() );
				
			}
			catch(Exception ex ){
				LOGGER.error("unable to open dialog" + ex.getMessage(), getClass() );
				
			}
	            
		}
		else {
			if ( null != treeStage ){
				StratiFXService.instance.removeController( IUIController.Type.TREE );
				treeStage.hide();
				treeStage.close();
				treeStage = null;
			}
		}
		
	}
	
	@FXML void onMenuOpenAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( UIAction.Open );
	}
	
	@FXML void onCloseAction( ActionEvent event ){
		Platform.exit();
		System.exit(0);
	}
	
	@FXML void onDeformResetAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new InteractionUIAction("Reset", "Reset") );
	}

	@FXML void onDeformNodeChainMailAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new InteractionUIAction("ChainMail", "NodeMove" ) );
	}

	@FXML void onDeformNodeMassAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new InteractionUIAction("MassSpring", "NodeMove" ) );
	}
	
	@FXML void onDeformTopVSAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new InteractionUIAction("VerticalShear", "Top" ) );
	}
	
	@FXML void onDeformTopMLSAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new InteractionUIAction("MovingLS", "Top" ) );
	}
	
	@FXML void onDeformTopFlexuralAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new InteractionUIAction("FlexuralSlip", "Top" ) );
	}

	@FXML void onDeformTopFEADynamicAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new InteractionUIAction("Dynamic", "Top" ) );
	}
	
	@FXML void onDeformTopFEAStaticAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new InteractionUIAction("Static", "Top" ) );
	}
	
	@FXML void onDeformTopFEAStaticLSAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new InteractionUIAction("StaticLS", "Top" ) );
	}
	
	@FXML void onDeformTopFEAFem2dAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new InteractionUIAction("FEM2D", "Top" ) );
	}
	
	@FXML void onPropertiesXYAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new PropertiesUIAction("XY") );
	}
	
	@FXML void onPropertiesPorosityAction( ActionEvent event ){
		StratiFXService.instance.broadCastAction( new PropertiesUIAction("Porosity") );
	}

	@Override
	public boolean handleAction(UIAction action) {
		// TODO Auto-generated method stub
		return false;
	}

}
