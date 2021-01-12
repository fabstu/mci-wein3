package mci.aufnehmen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mci.controller.WeinController;

public class Main extends Application {

    WeinController weinController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/WeinAufnehmen.fxml"));
        Parent root = fxmlLoader.load();

        //Parent root = FXMLLoader.load(getClass().getResource("/WeinAufnehmenController.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        // Make window bigger.
        //primaryStage.setFullScreen(true);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(800);

        weinController = new WeinController();

        WeinAufnehmenController weinAufnehmenController = fxmlLoader.getController();
        weinAufnehmenController.setDelegate(weinController);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
