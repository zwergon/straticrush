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
package stratifx.application.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import stratifx.application.fxcontrollers.MenuParamInfo;
import stratifx.application.fxcontrollers.ParamInfo;
import stratifx.application.plot.PlotController;

import java.io.IOException;
import java.net.URL;
import java.util.Map;


public class Main extends Application {



	@Override
	public void start(Stage primaryStage) {
		StratiFXService.instance.setPrimaryStage(primaryStage);

		BorderPane rootLayout = initRootLayout();

		createCenterPlot( rootLayout);

		// Show the scene containing the root layout.
		Scene scene = new Scene(rootLayout, 1200, 1000);


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
            URL url = Main.class.getResource("/fxml/MainUI.fxml");
            loader.setLocation(url);
            BorderPane rootLayout = loader.load();
            
            StratiFXService.instance.registerController( IUIController.Type.MAIN, loader.getController() );

            MenuBar menuBar = (MenuBar)rootLayout.lookup("#MainMenuBar");


            for(Map.Entry<String, ParamInfo> entry : MenuParamInfo.getMenuParamInfos().entrySet()){
                String key = entry.getKey();

                if ( entry.getValue() instanceof MenuParamInfo ) {
                    MenuParamInfo menuParamInfo = (MenuParamInfo)entry.getValue();

                    Menu menu = findMenu(menuBar, menuParamInfo.getMenuId());
                    if (null == menu) continue;

                    MenuItem item = new MenuItem(key);
                    item.setOnAction(event -> {
                        StratiFXService.instance.broadCastAction(menuParamInfo.getUIAction());
                    });

                    menu.getItems().add(item);
                }
            }

            return rootLayout;
                 
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    private Menu _findMenu(Menu parent, String fxId ){

        String mId = parent.getId();
        if ((mId != null) && mId.equals(fxId)){
            return parent;
        }

        for(MenuItem item : parent.getItems()){
            if ( item instanceof Menu ) {
                Menu foundItem =_findMenu((Menu)item, fxId);
                if ( foundItem != null ){
                    return foundItem;
                }
            }
        }

        return null;
    }

    private Menu findMenu(MenuBar menuBar, String fxId) {
        Menu foundMenu = null;
        for( Menu menu : menuBar.getMenus()){
            foundMenu = _findMenu(menu, fxId);
            if (foundMenu != null){
                break;
            }
        }

        return foundMenu;
    }

    /**
     * Initializes the root layout.
     */
    public void createCenterPlot( BorderPane rootLayout ) {


        PlotController plotView = new PlotController();

        rootLayout.setCenter( plotView );

        plotView.initialize( rootLayout.getWidth(), rootLayout.getHeight() );


        StratiFXService.instance.registerController( IUIController.Type.PLOT, plotView );
            
           

    }
    
  
	
	public static void main(String[] args) {
		launch(args);
	}
}
