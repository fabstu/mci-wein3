package mci.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mci.aufnehmen.WeinAufnehmenController;
import mci.liste.WeinListeController;
import mci.wein.Wein;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UnterfensterController {
    private List<Unterfenster> unterfenster = new ArrayList<>();

    private WeinListeController listeController;

    public void nehmeWeinAuf(WeinController weinController) throws IOException {
        System.out.println("Creating WeinAufnehmen Fenster..");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/WeinAufnehmen.fxml"));
        Parent root = fxmlLoader.load();

        Stage stage = new Stage();
        stage.setTitle("Wein Aufnahme");
        stage.setScene(new Scene(root, 300, 275));

        // Make window bigger.
        //primaryStage.setFullScreen(true);
        stage.setMinWidth(600);
        stage.setMinHeight(802);

        stage.initModality(Modality.NONE);
        stage.initStyle(StageStyle.UNIFIED);

        WeinAufnehmenController weinAufnehmenController = fxmlLoader.getController();
        weinAufnehmenController.setDelegate(weinController);
        weinAufnehmenController.setStage(stage);
        stage.show();

        unterfenster.add(weinAufnehmenController);
        System.out.println("unterfenster.size() = " + unterfenster.size());
    }

    public void zeigeListe(WeinController weinController, List<Wein> weinListe) throws IOException {
        if(listeController != null) {
            System.out.println("Making Liste Fenster Fokus anstelle ein neues zu erstellen.");
            listeController.requestFokus();
            return;
        }

        System.out.println("Creating Liste Fenster.");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/WeinListe.fxml"));
        Parent root = fxmlLoader.load();

        Stage stage = new Stage();
        stage.setTitle("Wein Liste");
        stage.setScene(new Scene(root, 300, 275));

        // Make window bigger.
        //primaryStage.setFullScreen(true);
        stage.setMinWidth(600);
        stage.setMinHeight(803);

        stage.initModality(Modality.NONE);
        stage.initStyle(StageStyle.UNIFIED);

        WeinListeController weinListeController = fxmlLoader.getController();
        if(weinListeController == null) {
            System.out.println("FUCK!");
            return;
        }
        weinListeController.setDelegate(weinController);
        weinListeController.setStage(stage);
        this.listeController = weinListeController;
        updateWeinListeController(weinListe);
        stage.show();

        unterfenster.add(weinListeController);
        System.out.println("unterfenster.size() = " + unterfenster.size());
    }
    public void updateWeinListeController(List<Wein> weinListe) {
        if(listeController == null) {
            return;
        }
        listeController.updateWeinListe(weinListe);
    }

    public boolean okayMitSchließen() {
        if(unterfenster.size() == 0) {
            return true;
        }

        // Frage Nutzer ob schliessen.
        if(confirmClosing() == false) {
            return false;
        }
        return true;
    }

    private boolean confirmClosing() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.NO, ButtonType.YES);
        alert.setTitle("Schliessen");
        alert.setHeaderText("");
        alert.setContentText("Gerade sind Unterfenster offen. Wollen sie das Programm wirklich beenden?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.YES;
    }
    void closeUnterfenster() {
        // Copy list. Other list will be modified.
        List<Unterfenster> unterfenster = Arrays.asList(this.unterfenster.stream().map(controller -> controller).toArray(Unterfenster[]::new));

        unterfenster.forEach(controller -> controller.forceClose());
        listeController = null;
        System.out.println("unterfenster.size() = " + this.unterfenster.size());
    }

    public void closedUnterfenster(Unterfenster controller) {
        System.out.println("Removed Unterfenster.");
        if(controller instanceof WeinListeController) {
            listeController = null;
        }

        unterfenster.remove(controller);
    }
}
