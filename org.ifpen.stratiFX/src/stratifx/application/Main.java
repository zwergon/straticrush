/* 
 * Copyright 2017 lecomtje.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stratifx.application;
	
import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Main extends Application {



	@Override
	public void start(Stage primaryStage) {
		StratiFXService.instance.setPrimaryStage(primaryStage);

		BorderPane rootLayout = initRootLayout();

		createCenterPlot( rootLayout);

		// Show the scene containing the root layout.
		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
		primaryStage.show();

		//StratiFXService.instance.fireAction(  IUIController.Type.PLOT, UIAction.DummyDraw );

		
		primaryStage.setOnCloseRequest(e -> {
			Platform.exit();
			System.exit(0);
		});
	}
	
	 /**
     * Initializes the root layout.
     */
    public BorderPane initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/fxml/MainUI.fxml"));
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
            loader.setLocation(Main.class.getResource("/fxml/PlotView.fxml"));
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
