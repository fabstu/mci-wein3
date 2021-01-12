package mci.liste;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mci.controller.Unterfenster;
import mci.controller.WeinController;
import mci.wein.Wein;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class WeinListeController implements Initializable, Unterfenster {
    private List<Wein> weinListe;
    private WeinListeDelegate delegate;

    @FXML
    TableView<Wein> weinTable;
    @FXML
    TableColumn<Wein, String> bestellnummer;
    @FXML
    TableColumn<Wein, String> jahrgang;
    @FXML
    TableColumn<Wein, String> name;
    @FXML
    TableColumn<Wein, String> farbe;
    @FXML
    TableColumn<Wein, String> bruttoflaschenpreis;

    Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupWeinListe();
    }

    public void setupWeinListe() {
        weinTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Wein>() {
            @Override
            public void changed(ObservableValue<? extends Wein> observableValue, Wein oldWein, Wein selectedWein) {
                delegate.weinSelectionChanged(selectedWein);
            }
        });
    }

    public void updateWeinListe(List<Wein> weinListe) {
        this.weinListe = weinListe;

        bestellnummer.setCellValueFactory(new PropertyValueFactory<Wein, String>("bestellnummer"));
        jahrgang.setCellValueFactory(new PropertyValueFactory<Wein, String>("jahrgang"));
        name.setCellValueFactory(new PropertyValueFactory<Wein, String>("bezeichnung"));
        bruttoflaschenpreis.setCellValueFactory(new PropertyValueFactory<Wein, String>("bruttoFlaschenpreis"));
        farbe.setCellValueFactory(new PropertyValueFactory<Wein, String>("farbe"));

        //var weinModels = FXCollections.observableArrayList(weinListe.stream().map(wein -> new WeinModel(wein)).toArray(WeinModel[]::new));
        //weinTable.setItems(weinModels);

        var weinModels = FXCollections.observableArrayList(weinListe);
        weinTable.setItems(weinModels);

        weinTable.refresh();
    }

    public void setDelegate(WeinController delegate) {
        this.delegate = delegate;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(event -> {
            event.consume();
            forceClose();
        });
    }

    @Override
    public void forceClose() {
        delegate.stopping(this);
        stage.close();
    }

    public int selectedWeinIndex() {
        return weinTable.getSelectionModel().getSelectedIndex();
    }

    public void requestFokus() {
        stage.requestFocus();
    }
}
