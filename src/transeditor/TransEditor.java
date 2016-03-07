/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transeditor;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author phillip
 */
public class TransEditor extends Application {

    public static Stage mainStage;
    
    @Override
    public void start(Stage stage) throws Exception {
        
        URL location = getClass().getResource("TransEditorFXML.fxml");
        ResourceBundle unicodeRB = ResourceBundle.getBundle("transeditor.unicodes");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = (Parent) fxmlLoader.load(location.openStream());
        TransEditorFXMLController appCtrl = (TransEditorFXMLController) fxmlLoader.getController();
        appCtrl.initialize(location, unicodeRB);
        mainStage = stage;
        stage.setTitle("TransEditor");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        Scene scene = new Scene(root);
        

        
        stage.setScene(scene);
        stage.setOnCloseRequest((WindowEvent e) -> {
            if (appCtrl.modified() == true) {
                e.consume();
            } else {
                stage.close();
            }
        });
        stage.show();
        
        
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
