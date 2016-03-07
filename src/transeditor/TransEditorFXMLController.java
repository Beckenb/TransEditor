/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transeditor;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

/**
 *
 * @author phillip
 */
public class TransEditorFXMLController implements Initializable {

    @FXML
    private ImageView imageview;

    @FXML
    private TextArea textarea;

    @FXML
    private Label zoom;

    private Double width;

    @FXML
    private TextField textField;

    @FXML
    private VBox buttonBox;

    private List<String> charList = new ArrayList<>();

    public boolean modified = false;
    public boolean mod = true;
    private static Stage closeStage;

    private File file;
    private String fileName;
    private boolean saveFile = false;

    private Integer caret;

    @FXML
    private MenuItem menuSave;

    Timeline autoSave = new Timeline(new KeyFrame(Duration.seconds(60), e -> {
        if (saveFile) {
            handleWriteFile();
        } else {
            //Do nothing
        }

    }));
    
    private String textContent;
    
    @FXML
    private TilePane buttonPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Enumeration<String> unicodeKeys = rb.getKeys();
            while (unicodeKeys.hasMoreElements()) {
                String unicodeKey = unicodeKeys.nextElement();
                String value = rb.getString(unicodeKey);
                value = getUnicode(value);
                createInserts(value, unicodeKey.replaceAll("_", " "));
            }
        } catch (Exception e) {
            
        }
    }

    @FXML
    private void handleOpenImage(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        setImageFilters(fileChooser);
        File imageFile;
        imageFile = fileChooser.showOpenDialog(TransEditor.mainStage);
        if (imageFile != null) {
            Image image = new Image(imageFile.toURI().toString());
            imageview.setImage(image);
            width = imageview.getBoundsInParent().getWidth();
            getZoom(width);
        } else {
            //Do nothing
        }
    }

    ;
    
    @FXML
    private void handleOpenFile(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        setFileFilters(fileChooser);
        file = fileChooser.showOpenDialog(TransEditor.mainStage);
        if (file != null) {
            StringBuilder text;
            text = readFile(file);
            fileName = file.getName();
            textarea.setText(text.toString());
            TransEditor.mainStage.setTitle("TransEditor - " + fileName);
        } else {
            //Do nothing
        }
    }

    @FXML
    private void handleSaveFile() {
        FileChooser fileChooser = new FileChooser();
        setFileFilters(fileChooser);
        //File file;
        file = fileChooser.showSaveDialog(TransEditor.mainStage);
        if (file != null) {
            if (!file.getPath().endsWith(".txt")) {
                file = new File(file.getPath() + ".txt");
            }
            handleWriteFile();
        } else {
            //Do nothing
        }

    }

    @FXML
    private void handleWriteFile() {
        String content;
        content = textarea.getText();
        try {
            if (file != null) {

                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"))) {
                    writer.append(content);
                    writer.flush();
                    modified = false;
                    menuSave.setDisable(true);
                    autoSave.stop();
                    saveFile = true;
                    fileName = file.getName();
                    TransEditor.mainStage.setTitle("Trans Editor - " + fileName);
                    textContent = textarea.getText();
                }
            } else {
                handleSaveFile();
            }
        } catch (IOException ex) {
            Logger.getLogger(TransEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setImageFilters(FileChooser fileChooser) {
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG Image", "*.jpg", "*.jpeg")
        );
    }

    private void setFileFilters(FileChooser fileChooser) {
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT File", "*.txt")
        );
    }

    private void setPdfFilters(FileChooser fileChooser) {
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF File", "*.pdf")
        );
    }
    
    private void setUnicodeFilters(FileChooser fileChooser) {
    fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PROPERTIES File", "*.properties")
    );
    }

    @FXML
    private void zoomIn(ActionEvent e) {
        Double fitWidth = imageview.getBoundsInParent().getWidth();
        fitWidth += 100;
        imageview.setFitWidth(fitWidth);
        getZoom(fitWidth);
    }

    @FXML
    private void zoomOut(ActionEvent e) {
        Double fitWidth = imageview.getBoundsInParent().getWidth();
        fitWidth -= 100;
        imageview.setFitWidth(fitWidth);
        getZoom(fitWidth);
    }

    private void getZoom(Double currentWidth) {
        Double zoom_1 = (currentWidth / width) * 100;
        String zoom_2 = "Zoom:" + String.valueOf(zoom_1.intValue()) + "%";
        System.out.println(zoom_1);
        this.zoom.setText(zoom_2);
    }

    private StringBuilder readFile(File file) {
        StringBuilder text = new StringBuilder(1024);
        String curLine;
        try {
            FileReader filereader = new FileReader(file);
            BufferedReader bufferedreader = new BufferedReader(filereader);
            while ((curLine = bufferedreader.readLine()) != null) {
                text.append(curLine).append("\n");
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return text;
    }

    @FXML
    private void handlePDFImport(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        setPdfFilters(fileChooser);
        File file;
        file = fileChooser.showOpenDialog(new Stage());
        Integer page;
        page = pageChooser.display();
        if (file != null) {
            if (page > 0) {
                importPDF(file, page);
            } else {
                //DO nothing
            }
        } else {
            //Do nothing
        }
    }

    private void importPDF(File file, Integer page) {
        PDDocument pdf;
        try {
            pdf = PDDocument.load(file);
            List<PDPage> list = pdf.getDocumentCatalog().getAllPages();
            //ToDO: Nicer selection window
            BufferedImage bfimage = list.get(page).convertToImage(1, 300);
            Image image = SwingFXUtils.toFXImage(bfimage, null);
            imageview.setImage(image);
            width = imageview.getBoundsInParent().getWidth();
            getZoom(width);
        } catch (Exception e) {
        }
    }

    private void appendString(String s) {
        Integer pos = textarea.getCaretPosition();
        textarea.insertText(pos, s);
        setModified();
        textarea.requestFocus();
    }

    private String getUnicode(String s) {
        String c = "u" + s;
        String[] arr = c.split("u");
        String text = "";
        for (int i = 1; i < arr.length; i++) {
            int hexVal = Integer.parseInt(arr[i], 16);
            text += (char) hexVal;
        }
        return text;
    }

    @FXML
    private void insertUnicode() {
        String s = textField.getText();
        if (s.length() == 1) {
            appendString(s);
        }
        if (s.length() == 4) {
            String text = getUnicode(s);
            appendString(text);
            //textField.setText(text);
            createInserts(text, "");
            textField.setText("");

        }
    }

    public static String getUnicodeValue(String line) {

        String hexCodeWithLeadingZeros = "";
        try {
            for (int index = 0; index < line.length(); index++) {
                String hexCode = Integer.toHexString(line.codePointAt(index)).toUpperCase();
                String hexCodeWithAllLeadingZeros = "0000" + hexCode;
                String temp = hexCodeWithAllLeadingZeros.substring(hexCodeWithAllLeadingZeros.length() - 4);
                hexCodeWithLeadingZeros += ("\\u" + temp);
            }
            return hexCodeWithLeadingZeros;
        } catch (NullPointerException nlpException) {
            return hexCodeWithLeadingZeros;
        }
    }

    public void createInserts(String s, String d) {
        Button btn = new Button(s);
        btn.setFont(Font.font("Junicode", 18));
        btn.setPadding(new Insets(2, 10, 2, 10));
        btn.setMinWidth(30);
        btn.setOnAction((ActionEvent e) -> {
            appendString(btn.getText());
        });
        Label lb = new Label(getUnicodeValue(s).substring(2));
        Label description = new Label(d);
        lb.setTextAlignment(TextAlignment.LEFT);
        
        Image icon = new Image(getClass().getResourceAsStream("icon.png"));
        ImageView iconView = new ImageView(icon);
        iconView.setFitHeight(20);
        iconView.setPreserveRatio(true);
        iconView.setSmooth(true);
        Button edit = new Button();
        edit.setGraphic(iconView);
        
        HBox line = new HBox(10);
        line.setAlignment(Pos.CENTER_LEFT);
        line.getChildren().addAll(btn, lb, description);
        buttonBox.getChildren().add(line);
    }

    @FXML
    private void noSave() {
        closeStage.close();
        TransEditor.mainStage.close();
    }

    @FXML
    private void close() {
        closeStage.close();
    }

    @FXML
    public boolean modified() {
        if (modified == true) {
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getResource("CloseFXML.fxml"));
                Stage stage = new Stage();
                closeStage = stage;
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(TransEditor.mainStage);
                stage.setTitle("Do you even close?");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
            }
            return mod;
        } else {
            return false;
        }
    }

    @FXML
    private void setModified() {
        if (textarea.getText().equals(textContent)) {
            
        }
        else {
            modified = true;
            menuSave.setDisable(false);
            TransEditor.mainStage.setTitle("TransEditor - *" + fileName);
            Status status = autoSave.getStatus();
            if (status.equals(Animation.Status.RUNNING)) {
                //Do nothing
            } else {
                autoSave.play();
            }
        }
        
    }

    @FXML
    private void handleAbout() {
        Alert about = new Alert(AlertType.INFORMATION);
        about.setTitle("TransEditor");
        about.setHeaderText("About");
        about.setContentText("Author: Phillip Beckenbauer");
        about.showAndWait();
    }

    @FXML
    private void saveUnicodeList(){
        Map<String, String> unicodeButtons = new HashMap<>();
        String unicodeButton = new String();
        String unicodeDescription = new String();
        for (Node n : buttonBox.getChildren()) {
            final HBox line = (HBox) n;
            for (Node l : line.getChildren()) {
                if (l instanceof Label) {
                    final Label lb = (Label) l;
                    if (lb.getText().length() == 4) {
                        unicodeButton = lb.getText();
                    } else {
                        unicodeDescription = lb.getText().replace(" ", "_");
                    }
                }
            }
            unicodeButtons.put(unicodeDescription, unicodeButton);
        }
        
        FileChooser fileChooser = new FileChooser();
        setUnicodeFilters(fileChooser);
        File propFile = fileChooser.showSaveDialog(TransEditor.mainStage);
        if (propFile != null) {
            if (!propFile.getPath().endsWith(".properties")) {
                propFile = new File(propFile.getPath() + ".properties");
            }
            try {
            if (propFile != null) {
                Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(propFile), "UTF8"));
                for (Map.Entry<String, String> entry : unicodeButtons.entrySet()) {
                    writer.append(entry.getKey() + "=" + entry.getValue());   
                    writer.append(System.lineSeparator());
                }
                writer.flush();
            }
            } catch (IOException ex) {
                Logger.getLogger(TransEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            //Do nothing
        }   
    }
    
    @FXML
    private void loadUnicodeList(){
        FileChooser fileChooser = new FileChooser();
        setUnicodeFilters(fileChooser);
        File propFile = fileChooser.showOpenDialog(TransEditor.mainStage);
        PropertyResourceBundle unicodeRB = null;
        try {
            FileInputStream input = new FileInputStream(propFile);
            unicodeRB = new PropertyResourceBundle(input);
        } catch (Exception e) {
            
        }
        
        if (propFile != null) {
            buttonBox.getChildren().clear();
            try {
            Enumeration<String> unicodeKeys = unicodeRB.getKeys();
            while (unicodeKeys.hasMoreElements()) {
                String unicodeKey = unicodeKeys.nextElement();
                String value = unicodeRB.getString(unicodeKey);
                value = getUnicode(value);
                createInserts(value, unicodeKey.replaceAll("_", " "));
            }
        } catch (Exception e) {
            
        }
        } else {
            //Do nothing
        }
    }
}
