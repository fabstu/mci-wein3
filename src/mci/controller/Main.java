package mci.controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import mci.aufnehmen.WeinAufnehmenController;
import mci.controller.WeinController;

import java.io.IOException;

public class Main extends Application {

    WeinController weinController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Wein.fxml"));
        Parent root = fxmlLoader.load();

        primaryStage.setTitle("Wein");
        primaryStage.setScene(new Scene(root, 300, 275));

        // Make window bigger.
        //primaryStage.setFullScreen(true);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(801);

        primaryStage.show();

        weinController = fxmlLoader.getController();
        weinController.setStage(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
