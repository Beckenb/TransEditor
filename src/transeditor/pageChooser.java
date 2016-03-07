/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transeditor;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author phillip
 */
public class pageChooser {
    
    static Integer pagenumber = 0;
    
    public static int display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Choose a PDF page");
        window.setMinWidth(250);
        
        TextField textfield = new TextField();
        Label label = new Label();
        label.setText("Choose a page in your PDF to open");
        Button closeButton = new Button("Cancel");
        closeButton.setOnAction(e -> window.close());
        Button okButton = new Button("OK");
        okButton.setOnAction(e -> {
            getPageNumber(textfield);
            window.close();
                });
        Label pgeNumber = new Label();
        pgeNumber.setText("Page: ");
        
        VBox layout = new VBox(10);
        HBox buttons = new HBox(15);
        HBox pageNumber = new HBox(15);
        
        buttons.getChildren().addAll(okButton, closeButton);
        buttons.setAlignment(Pos.CENTER);
        pageNumber.getChildren().addAll(pgeNumber, textfield);
        pageNumber.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(label, pageNumber, buttons);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(5, 5, 5, 5));

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        
        if (pagenumber == 0) {
            window.close();
            return pagenumber;
        }
        else {
            return pagenumber;
        }
    }
    
   public static void getPageNumber(TextField textfield) {
       try {
           pagenumber = Integer.parseInt(textfield.getText());
       }
       catch (Exception e) { 
                      
       }
   } 
   
}
