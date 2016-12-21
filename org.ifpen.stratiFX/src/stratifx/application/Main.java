package stratifx.application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import stratifx.application.controller.PlotController;


public class Main extends Application {

	private Stage primaryStage;
	
	private PlotController plotController;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("PlotFx");

		initRootLayout();
	
	}
	
	 /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
        	 // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/PlotView.fxml"));
            ScrollPane plotView = (ScrollPane) loader.load();
            
     
            // Show the scene containing the root layout.
            Scene scene = new Scene(plotView);
            primaryStage.setScene(scene);
            primaryStage.show();
            
            
            plotController = loader.getController();
            plotController.drawCanvas();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
  
	
	public static void main(String[] args) {
		launch(args);
	}
}
