package stratifx.application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Main extends Application {

	private Stage primaryStage;
	

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("StratiFX");

		BorderPane rootLayout = initRootLayout();

		createCenterPlot( rootLayout);

		// Show the scene containing the root layout.
		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
		primaryStage.show();

		StratiFXService.instance.fireAction(  IUIController.Type.PLOT, UIAction.DummyDraw );

	}
	
	 /**
     * Initializes the root layout.
     */
    public BorderPane initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("MainUI.fxml"));
            BorderPane rootLayout = (BorderPane) loader.load();
            
            StratiFXService.instance.registerController( IUIController.Type.MAIN, loader.getController() );
            
            return rootLayout;
                 
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
	
	 /**
     * Initializes the root layout.
     */
    public void createCenterPlot( BorderPane rootLayout ) {
        try {
            // Load root layout from fxml file.
        	 // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("plot/PlotView.fxml"));
            AnchorPane plotView = (AnchorPane)loader.load();
            rootLayout.setCenter( plotView );
            
            StratiFXService.instance.registerController( IUIController.Type.PLOT, loader.getController() );
            
           
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
  
	
	public static void main(String[] args) {
		launch(args);
	}
}
