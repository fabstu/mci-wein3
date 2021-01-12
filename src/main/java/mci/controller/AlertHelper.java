package mci.controller;

import javafx.scene.control.Alert;
import mci.aufnehmen.InternalControllerError;

public class AlertHelper {
    public static void showErrorAlert(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.setContentText(text);
        alert.showAndWait();
    }
    public static void showAlert(String title, String text, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.setContentText(text);
        alert.showAndWait();
    }
    /*
    public static void confirm(String title, String text, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.setContentText(text);
        alert.showAndWait();
    }
    public static void showAlert(MissingInputError e) {

    }
    public static void showAlert(InvalidInputError e) {

    }
    */

    public static void showErrorAlert(InternalControllerError e) {
        showErrorAlert("Internal Controller Error", e.getMessage());
    }

    public static void showAlert(KeineWeindateiError keineWeindateiError, boolean zuletztGeöffnet) {
        var titel = "Öffnen fehlgeschlagen";
        String text;
        if(zuletztGeöffnet) {
            text = "Das Laden der zuletzt geöffneten Datei ist fehlgeschlagen.";
        } else {
            text = "Das Öffnen ist fehlgeschlagen. Die Datei ist keine Weindatei.";
        }
        AlertHelper.showErrorAlert(titel, text);
    }
}
