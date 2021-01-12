package mci.controller;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mci.aufnehmen.InternalControllerError;
import mci.aufnehmen.WeinAufnehmenDelegate;
import mci.liste.WeinListeDelegate;
import mci.wein.Wein;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class WeinController implements WeinAufnehmenDelegate, Initializable, WeinListeDelegate {
    private List<Wein> weinListe = new ArrayList<Wein>();
    private Stage primaryStage;
    UnterfensterController unterfensterController = new UnterfensterController();
    DateisystemController dateisystemController;

    Wein selectedWein;

    // Only enable saving if something changed.
    BooleanProperty weinListeChanged = new SimpleBooleanProperty();

    @FXML
    public MenuItem löschen;
    @FXML
    public MenuItem speichernMenuItem;
    @FXML
    public MenuItem speichernUnterMenuItem;
    @FXML
    public MenuItem ansicht;
    @FXML
    public MenuItem öffnenMenuItem;

    public WeinController() {

    }

    @Override
    public void addWein(Wein wein) {
        weinListe.add(wein);

        weinListe.forEach(this::printWein);
        weinListeChanged.set(true);
        unterfensterController.updateWeinListeController(weinListe);
    }

    @Override
    public void stopping(Unterfenster controller) {
        unterfensterController.closedUnterfenster(controller);
    }

    private void printWein(Wein wein) {
        System.out.println("\n\nwein = [" + wein + "\n]\n\n");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.setImplicitExit(true);
        löschen.disableProperty().set(true);

        setupWeinListe();
    }

    void setupWeinListe() {
        /**
         * Disabales the save and save at buttons in case the wine list size is zero.
         */
        weinListeChanged.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean weinListeChanged) {
                System.out.println("weinListeChanged = " + weinListeChanged);
                var disableSaving = shouldDisableSaving();
                System.out.println("disableSaving = " + disableSaving);

                speichernMenuItem.setDisable(disableSaving);
                speichernUnterMenuItem.setDisable(disableSaving);
                ansicht.setDisable(weinListe.size() == 0);

                System.out.println("weinListe.size() = " + weinListe.size());

                // Disable öffnen while not saved.
                öffnenMenuItem.setDisable(!disableSaving);
            }
        });
    }
    boolean shouldDisableSaving() {
        return weinListe.size() == 0 || !weinListeChanged.get();
    }

    public void setStage(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setOnCloseRequest(event -> beenden(event));
        dateisystemController = new DateisystemController(primaryStage);

        try {
            weinListe = dateisystemController.loadLastOpenedFile();
        } catch (DecodingError decodingError) {
            decodingError.printStackTrace();
            if(confirmUseDamaged(decodingError)) {
                weinListe = decodingError.damaged;
            }
        } catch (IOException e) {
            AlertHelper.showErrorAlert("Zuletzt geöffnete Datei", "Das Laden der zuletzt geöffneten Datei ist fehlgeschlagen.");
        } catch (KeineWeindateiError keineWeindateiError) {
            AlertHelper.showAlert(keineWeindateiError, true);
        }

        // Updating Ansicht.
        toggleWeinListeChanged(false);

        System.out.println("weinListe.size() = " + weinListe.size());
    }

    private boolean confirmUseDamaged(DecodingError e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.NO, ButtonType.YES);
        alert.setTitle("Weinliste beschädigt");
        alert.setHeaderText("");
        var text = String.format("Beim laden ist ein Fehler aufgetreten.\n" +
                "Beschädigt: %d\nErfolgreich eingelesen: %d\n\n" +
                "Wollen sie die beschädigte Weinliste weiter nutzen?",
                e.countDamaged, e.countHealthy);
        alert.setContentText(text);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.YES;
    }

    @FXML
    void öffnen(ActionEvent event) {
        System.out.println("Öffnen.");

        try {
            weinListe = dateisystemController.öffnen();
        } catch (WrongFileExtension e) {
            AlertHelper.showErrorAlert("Laden von Datei", "Dateiendung ist nicht erlaubt. \nDatei: " + e.getMessage());
        } catch (ChoosingFileFailed choosingFileFailed) {
            choosingFileFailed.printStackTrace();
        } catch (IOException e) {
            handle(e);
            AlertHelper.showErrorAlert("Öffnen fehlgeschlagen.", "Das Laden der Datei ist fehlgeschlagen.");
        } catch (DecodingError decodingError) {
            decodingError.printStackTrace();
            if(confirmUseDamaged(decodingError)) {
                weinListe = decodingError.damaged;
            }
        } catch (KeineWeindateiError keineWeindateiError) {
            AlertHelper.showAlert(keineWeindateiError, false);
        } catch (InternalControllerError internalControllerError) {
            handle(internalControllerError);
        }

        toggleWeinListeChanged(false);
        unterfensterController.updateWeinListeController(weinListe);
    }

    void toggleWeinListeChanged(boolean endValue) {
        var value = weinListeChanged.get();
        if(value == endValue) {
            weinListeChanged.set(!value);
            weinListeChanged.set(value);
        } else {
            weinListeChanged.set(endValue);
        }
    }

    @FXML
    void speichern(ActionEvent event) {
        speichern();
    }
    void speichern() {
        System.out.println("Speichern.");
        try {
            dateisystemController.speichern(weinListe);
            weinListeChanged.set(false);
        } catch (ChoosingFileFailed choosingFileFailed) {
            handle(choosingFileFailed);
        } catch (IOException e) {
            handle(e);
        } catch (InternalControllerError internalControllerError) {
            handle(internalControllerError);
        }
    }
    @FXML
    void speichernUnter(ActionEvent event) {
        System.out.println("Speichern unter.");
        try {
            dateisystemController.speichernUnter(weinListe);
            weinListeChanged.set(false);
        } catch (ChoosingFileFailed choosingFileFailed) {
            handle(choosingFileFailed);
        } catch (IOException e) {
            handle(e);
        } catch (InternalControllerError internalControllerError) {
            handle(internalControllerError);
        }
    }
    void handle(ChoosingFileFailed e) {
        AlertHelper.showErrorAlert("Choosing file failed", "Die Dateiauswahl ist fehlgeschlagen.");
    }
    void handle(IOException e) {
        AlertHelper.showErrorAlert("IOEXception", "Das schreiben ist fehlgeschlagen. " + e.toString());
        e.printStackTrace();
    }
    void handle(InternalControllerError e) {
        AlertHelper.showErrorAlert("InternalControllerError", e.toString());
        e.printStackTrace();
    }

    @FXML
    void beenden(ActionEvent event) {
        beenden();
    }
    void beenden(WindowEvent event) {
        beenden();
        event.consume();
    }
    void beenden() {
        System.out.println("Beenden.");

        if(!unterfensterController.okayMitSchließen()) {
            return;
        }
        if(shouldDisableSaving() == false) {
            Saving saving = null;
            try {
                saving = askSaving();
            } catch (InternalControllerError internalControllerError) {
                AlertHelper.showErrorAlert("Internal Controller Error", "Das speichern ist fehlgeschlagen.");
                internalControllerError.printStackTrace();
                return;
            }
            switch(saving) {
                case SAVE:
                    speichern();
                    break;
                case ABORT:
                    System.out.println("Aborting Beenden.");
                    return;
                case DISCARD:
                    break;
            }
        }
        unterfensterController.closeUnterfenster();
        primaryStage.close();
    }
    private Saving askSaving() throws InternalControllerError {
        var speichernButton = new ButtonType("Speichern");
        var abbrechenButton = new ButtonType("Abbrechen");
        var änderungenVerwerfenButton = new ButtonType("Änderungen verwerfen");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", speichernButton, änderungenVerwerfenButton, abbrechenButton);
        alert.setTitle("Speichern vor Beenden");
        alert.setHeaderText("");
        alert.setContentText("Es gibt ungespeicherte Änderungen. Bitte wählen sie was mit diesen passieren soll.");
        Optional<ButtonType> result = alert.showAndWait();

        Saving saving;
        if(speichernButton == result.get()) {
            saving = Saving.SAVE;
        } else if(änderungenVerwerfenButton == result.get()) {
            saving = Saving.DISCARD;
        } else if(abbrechenButton == result.get()) {
            saving = Saving.ABORT;
        } else {
            throw new InternalControllerError("Dieser Button wurde hier nicht erwartet. " + result.get().getText());
        }
        return saving;
    }

    enum Saving{
        SAVE, ABORT, DISCARD
    }

    @FXML
    void bearbeiten(ActionEvent event) {
        System.out.println("Bearbeiten.");
    }
    @FXML
    void aufnehmen(ActionEvent event) {
        System.out.println("Aufnehmen.");
        try {
            unterfensterController.nehmeWeinAuf(this);
        } catch (IOException e) {
            ExceptionHelper.handle(e);
        }
    }
    @FXML
    void ändern(ActionEvent event) {
        System.out.println("Ändern.");
    }
    @FXML
    void löschen(ActionEvent event) {
        System.out.println("Löschen.");

        if(!confirmDeleting()) {
            System.out.println("Löschen wurde abgebrochen.");
            return;
        }

        weinListe.remove(selectedWein);
        unterfensterController.updateWeinListeController(weinListe);
        System.out.println("weinListe.size() = " + weinListe.size());
        weinListeChanged.set(true);
    }

    private boolean confirmDeleting() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.NO, ButtonType.YES);
        alert.setTitle("Wein Löschen");
        alert.setHeaderText("");
        alert.setContentText("Wollen sie wirklich de aktuell ausgewählten Wein löschen?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.YES;
    }

    @FXML
    void liste(ActionEvent event) {
        System.out.println("Liste.");
        try {
            unterfensterController.zeigeListe(this, weinListe);
        } catch (IOException e) {
            ExceptionHelper.handle(e);
        }
    }
    @FXML
    void details(ActionEvent event) {
        System.out.println("Details.");
    }
    @FXML
    void info(ActionEvent event) {
        System.out.println("Info.");
        AlertHelper.showAlert("Info", "Wein Applikation.\nCopyright Fabian Sturm.\nKontakt: wein-applikation@aduu.de", Alert.AlertType.INFORMATION);
    }

    public List<Wein> getWeinListe() {
        return weinListe;
    }

    @Override
    public void weinSelectionChanged(Wein selectedWein) {
        System.out.println("Wein selection changed.");
        System.out.println("selectedWein = " + (selectedWein == null));
        löschen.setDisable(selectedWein == null);
        this.selectedWein = selectedWein;
    }
}
