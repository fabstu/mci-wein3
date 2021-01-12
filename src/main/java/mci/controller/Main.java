package mci.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    WeinController weinController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/Wein.fxml"));
        Parent root = fxmlLoader.load();

        //Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("/Wein.fxml"));


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
