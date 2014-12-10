/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package straticrush.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author lecomtje
 */
public class StratiCrushMain extends Application {
    
    @Override
    public void start(Stage primaryStage) {
            //Setup the VBox Container and BorderPane
        BorderPane root = new BorderPane();
        VBox topContainer = new VBox();
        
        //Setup the Main Menu bar and the ToolBar
        MenuBar mainMenu = new MenuBar();
        ToolBar toolBar = new ToolBar();
     
        //Create SubMenu File.
        Menu file = new Menu("File");
        MenuItem openFile = new MenuItem("Open File");
        MenuItem exitApp = new MenuItem("Exit");
        file.getItems().addAll(openFile,exitApp);
        
        //Create SubMenu Edit.
        Menu edit = new Menu("Edit");
        MenuItem properties = new MenuItem("Properties");
        edit.getItems().add(properties);
        
        //Create SubMenu Help.
        Menu help = new Menu("Help");
        MenuItem visitWebsite = new MenuItem("Visit Website");
        help.getItems().add(visitWebsite);
        
        mainMenu.getMenus().addAll(file, edit, help);
        
        //Create some toolbar buttons
        Button openFileBtn = new Button();
        Button printBtn = new Button();
        Button snapshotBtn = new Button();
        
        System.out.println();
        //Add some button graphics
        openFileBtn.setGraphic(new ImageView( new Image( getClass().getResourceAsStream("../../zoomIn.gif")) ) );
        printBtn.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("../../zoomOut.gif"))));
        snapshotBtn.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("../../oneOne.gif"))));
        
        toolBar.getItems().addAll(openFileBtn, printBtn, snapshotBtn);
        
        //Add the ToolBar and Main Meu to the VBox
        topContainer.getChildren().add(mainMenu);
        topContainer.getChildren().add(toolBar);
        
        //Apply the VBox to the Top Border
        root.setTop(topContainer);
        
        Scene scene = new Scene(root, 300, 250);
        
        //Setup the Stage.
        primaryStage.setTitle("MenuExample");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
