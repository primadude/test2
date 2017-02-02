/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloader;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Jamie
 */
public class FXMLController implements Initializable {
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Label fileLabel;
    
    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private Button startButton;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private Button exitButton;
    
    private Downloader downloader;
    
    private Task updateTask;
    
    private Task downloadTask;
    
    @FXML
    public void startButtonAction() {
        cancelButton.setDisable(false);
        startButton.setDisable(true);
        downloader = new Downloader();
        updateTask = progressTask();
        downloadTask = downloader.downloadTask();
        statusLabel.textProperty().bind(updateTask.messageProperty());
        fileLabel.textProperty().bind(downloadTask.messageProperty());
        progressBar.progressProperty().bind(updateTask.progressProperty());
        downloader.setDownloading(true);
        new Thread(updateTask).start();
        new Thread(downloadTask).start();
    }
    
    @FXML
    public void cancelButtonAction() {
        statusLabel.textProperty().unbind();
        fileLabel.textProperty().unbind();
        progressBar.progressProperty().unbind();
        statusLabel.setText("Status: Ready to start.");
        fileLabel.setText("");
        progressBar.setProgress(0);
        downloader.setDownloading(false);
        downloadTask.cancel();
        updateTask.cancel();
        cancelButton.setDisable(true);
        startButton.setDisable(false);
    }
    
    @FXML
    public void exitButtonAction() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you wish to exit?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.close();
        } else {
            // Do nothing.
        }
    }
    
    private Task progressTask() {
        return new Task() {
            @Override
            protected Object call() {
                updateProgress(0, 1);
                updateMessage("Status: Getting download");
                while (downloader.isDownloading()) {
                    try {
                        if (downloader.getFos() != null) {
                            updateMessage("Status: Downloading");
                            System.out.println("Progress: "+ downloader.getFos().getChannel().size());
                            updateProgress(downloader.getFos().getChannel().size(), downloader.getConnection().getContentLength());
                        }
                        Thread.sleep(100);
                    } catch (IOException | InterruptedException e) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
                updateProgress(1, 1);
                cancelButton.setDisable(true);
                startButton.setDisable(false);
                updateMessage("Status: Completed.");
                return true;
            }
        };
    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert statusLabel != null : "text as not injected";
    }    
    
}
