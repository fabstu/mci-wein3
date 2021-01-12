package mci.aufnehmen;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mci.aufnehmen.preisumrechner.PreisumrechnerController;
import mci.aufnehmen.trinkreife.TrinkreifeController;
import mci.controller.AlertHelper;
import mci.controller.Unterfenster;
import mci.wein.*;

import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class WeinAufnehmenController implements Initializable, Unterfenster {
    WeinAufnehmenDelegate delegate;
    private WeinAufnehmenPresenter presenter;

    static Pattern bestellNummerEingabe1 = Pattern.compile("^[0-9]{0,4}\\-?$");
    static Pattern bestellNummerEingabe2 = Pattern.compile("^[0-9]{4}-[A-ZÄÖÜ]{0,3}-?$");
    static Pattern bestellNummerEingabe3 = Pattern.compile("^[0-9]{4}-[A-ZÄÖÜ]{3}-[0-9]{0,4}");

    /*
    Pattern a = Pattern.compile("^((([1-9][0-9]*?([" + groupingSymbol  + "][0-9]{3})*?([" + groupingSymbol  + "][0-9]{0,3})*)|0|)([" + decimalSymbol + "][0-9" +
            "]{0,2})?)?$");
    Pattern b = Pattern.compile("^((([1-9][0-9]*?([" + groupingSymbol + "][0-9]{3})*?)|0)([" + decimalSymbol + "][0-9]{1,2})?)$");
    */

    @FXML
    private TextField bestellnummer;
    @FXML
    private TextField bezeichnung;
    @FXML
    private TextField jahrgang;
    @FXML
    private RadioButton weiss;
    @FXML
    private RadioButton rot;
    @FXML
    private RadioButton rose;
    @FXML
    private ChoiceBox land;
    @FXML
    private ChoiceBox region;
    @FXML
    private Spinner alkoholgehalt;
    @FXML
    private CheckBox alkoholfrei;
    @FXML
    private TextArea beschreibung;
    @FXML
    private Spinner lagerdauer;

    Wein getWein() throws InternalControllerError {
        var alter = getWeinAlter();
        return new Wein(
                alter,
                preisumrechnerController.getWeinPreis(),
                bestellnummer.getText(),bezeichnung.getText(), beschreibung.getText(),
                getFarbe(),
                getAlkoholgehalt(),
                getAnbaugebiet()
        );
    }

    @FXML
    private TrinkreifeController trinkreifeController;

    @FXML
    private PreisumrechnerController preisumrechnerController;

    // ChoiceBox Daten.
    private static final String[] farben = new String[]{"weiß", "rot", "rosé"};
    private static String[] countryNames;
    private static List<String> alkoholgehaltWerte;

    private static Pattern jahrgangEingabe= Pattern.compile("^[0-9]{0,4}$");
    private static Pattern jahrgangExact = Pattern.compile("^(19[4-9][0-9]|20[0-3][0-9])$");

    private static Pattern lagerdauerEingabe = Pattern.compile("^[0-9]{0,2}$");
    private static Pattern lagerdauerExact = Pattern.compile("^[1-3][0-9]?$");

    // Remember the pristine wine. Can compare with it to see whether the user made an input.
    Wein pristineWine;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        presenter = new WeinAufnehmenPresenter();

        try {
            setup();
        } catch (InternalControllerError internalControllerError) {
            internalControllerError.printStackTrace();
        }
    }
    public void setStage(Stage stage) {
        stage.setOnCloseRequest(event -> {
            try {
                abort();
            } catch (InternalControllerError internalControllerError) {
                internalControllerError.printStackTrace();
            }
        });
    }

    void setup() throws InternalControllerError {
        setupBestellnummer();
        setupBezeichnung();
        setupFarben();
        setupAnbaugebiet();
        setupAlkoholgehalt();
        setupPreisumrechner();
        setupTrinkreifediagramm();
        setupJahrgang();
        setupBeschreibung();
        setupLagerdauer();

        updateTrinkreifeDiagramm();

        // Only once.
        if(pristineWine == null) {
            pristineWine = getWein();
        }
    }

    void setupBestellnummer() {
        bestellnummer.setText("");
        StyleHelper.setWhiteStyle(bestellnummer);
        bestellnummer.promptTextProperty().set("2019-BBB-ZZZZ");

        bestellnummer.setTextFormatter(new TextFormatter(RegexHelper.sanityFilterWithHandler(change -> {
            var text = change.getControlNewText();
            var length = text.length();

            // Match depending on length.
            if(length <= 5) {
                return RegexHelper.matchesPattern(bestellNummerEingabe1, text) ? change : null;
            } else if(length <= 9) {
                return RegexHelper.matchesPattern(bestellNummerEingabe2, text) ? change : null;
            } else if(length <= 13) {
                return RegexHelper.matchesPattern(bestellNummerEingabe3, text) ? change : null;
            }
            System.out.println("No match für neue Bestellnummer-Eingabe.");
            return null;
        })));


        bestellnummer.focusedProperty().addListener((observableValue, hatteFokus, hatFokus) -> {
            // If it was marked red remove the mark.
            if (hatteFokus && isBestellnummerValid()) {
                StyleHelper.setWhiteStyle(bestellnummer);
            }
        });
    }
    private boolean isBestellnummerValid() {
        return RegexHelper.matchesPattern(Wein.bestellnummerExact, bestellnummer.getText());
    }
    private void validateBestellnummer() throws MissingInputError, InvalidInputError {
        if(!isBestellnummerValid()) {
            StyleHelper.setRedStyle(bestellnummer);
            bestellnummer.requestFocus();

            if(bestellnummer.getText().length() == 0) {
                throw new MissingInputError("Bitte geben sie eine Bestellnummer ein.");
            } else {
                throw new InvalidInputError("Die eingegebene Bestellnummer muss das Format 2017-BBB-ZZZZ aufweisen. \nB steht für Großbuchstabe. \nZ für Zahl von 0 bis 9. " +
                        "\n\nBitte überprüfen sie ihre Eingabe.");
            }
        }
    }
    private boolean bestellnummerMatchesPattern(Pattern p) {
        return RegexHelper.matchesPattern(p, bestellnummer.getText());
    }
    private void setupBezeichnung() {
        bezeichnung.setText("");
        StyleHelper.setWhiteStyle(bezeichnung);

        bezeichnung.focusedProperty().addListener((observableValue, hatteFokus, hatFokus) -> {
            if(hatteFokus && isBezeichnungValid()) {
                StyleHelper.setWhiteStyle(bezeichnung);
            }
        });
    }
    private boolean isBezeichnungValid() {
        return bezeichnung.getText().length() > 0;
    }
    private void validateBezeichnung() throws MissingInputError, InvalidInputError {
        if(!isBezeichnungValid()) {
            StyleHelper.setRedStyle(bezeichnung);
            bezeichnung.requestFocus();

            if(bezeichnung.getText().length() == 0) {
                throw new MissingInputError("Bitte gib einen Bezeichner für deinen Wein ein.");
            } else {
                throw new InvalidInputError("Diese Bezeichnung ist nicht gültig.");
            }
        }
    }

    private ToggleGroup farbenGroup = new ToggleGroup();

    private void setupFarben() {
        weiss.setToggleGroup(farbenGroup);
        rot.setToggleGroup(farbenGroup);
        rose.setToggleGroup(farbenGroup);
        rot.selectedProperty().setValue(true);
        // farbe.setTooltip(new Tooltip("Wähle einen Weintyp aus."));
    }
    private WeinFarbe getFarbe() {
        if(weiss.selectedProperty().get()) {
            return new WeinFarbe(0);
        } else if(rot.selectedProperty().get()) {
            return new WeinFarbe(1);
        } else {
            return new WeinFarbe(2);
        }
    }
    private void setupAnbaugebiet() {
        countryNames = Anbaugebiet.getCountryNamesSorted();
        land.setItems(FXCollections.observableArrayList(countryNames));

        land.getSelectionModel().select(-1);
        land.getSelectionModel().selectedIndexProperty()
                .addListener((ov, vorigeSelektion, aktuellAusgewählt) -> {
                    // Do nothing incase selection did not change.
                    if(vorigeSelektion == aktuellAusgewählt) { return; }

                    if(vorigeSelektion.intValue() == -1) {
                        StyleHelper.setWhiteStyle(land);
                    }

                    selectedCountryIndex(aktuellAusgewählt.intValue());
                });

        land.setTooltip(new Tooltip("Wähle das Anbauland aus."));

        // Initialize regions.
        selectedCountryIndex(-1);
    }
    private void selectedCountryIndex(int countryIndex) {
        // Can be -1 on reset where the listener gets triggered and nothing is selected.
        if(countryIndex == -1) {
            region.setItems(FXCollections.observableArrayList(new String[]{}));
            return;
        }

        // var regions = Anbaugebiet.getCountries().get(countryNames[countryIndex]);

        var regions = Anbaugebiet.getRegionsOfCountry(countryIndex);
        region.setItems(FXCollections.observableArrayList(regions));

        region.getSelectionModel().select(-1);
        land.setTooltip(new Tooltip("Wähle die Anbauregion aus."));
    }
    public boolean isAnbaugebietValid() {
        return land.getSelectionModel().getSelectedIndex() != -1;
    }
    public void validateAnbaugebiet() throws MissingInputError {
        if(!isAnbaugebietValid()) {
            StyleHelper.setRedStyle(land);
            land.requestFocus();

            throw new MissingInputError("Bitte geben sie ein Anbauland ein.");
        }
    }
    Anbaugebiet getAnbaugebiet() {
        return new Anbaugebiet(land.getSelectionModel().getSelectedIndex(), region.getSelectionModel().getSelectedIndex());
    }

    void setupAlkoholgehalt() {
        alkoholfrei.selectedProperty().addListener((observableValue, warWahr, istWahr) -> {
            if (istWahr) {
                alkoholgehalt.setDisable(true);
            } else {
                alkoholgehalt.setDisable(false);
            }
        });
        var min = 7.5;
        var max = 25;
        var initialValue = 7.5;
        var stepBy = 0.5;

        var factory = new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, initialValue, stepBy);
        alkoholgehalt.setValueFactory(factory);
        alkoholfrei.setSelected(false);
    }
    void setupPreisumrechner() {
        preisumrechnerController.setup();
    }
    void setupTrinkreifediagramm() {
        updateTrinkreifeDiagramm();
    }
    void setupJahrgang() {
        // Startwert.
        jahrgang.setText("" + (aktuellesJahr() - 1));
        jahrgang.promptTextProperty().set("2019");
        StyleHelper.setWhiteStyle(jahrgang);


        jahrgang.setTextFormatter(new TextFormatter<String>(
                change -> {
                    String text = change.getText();

                    if (!change.isContentChange()) {
                        return change;
                    }

                    if (text.matches("[a-z]*") || text.isEmpty()) {
                        return change;
                    }

                    return null;
                }
        ));

        jahrgang.setTextFormatter(new TextFormatter(RegexHelper.patternFilterWithHandler(jahrgangEingabe, change -> change)));
        jahrgang.focusedProperty().addListener((observableValue, warImFokus, istImFokus) -> {
            // Check whether the input does match exactly. If colored then remove color.
            if(warImFokus && isJahrgangValid()) {
                StyleHelper.setWhiteStyle(jahrgang);
                updateTrinkreifeDiagramm();
            }
        });
    }
    boolean isJahrgangValid() {
        return RegexHelper.matchesPattern(jahrgangExact, jahrgang.getText()) && Integer.parseInt(jahrgang.getText()) <= aktuellesJahr();
    }
    void validateJahrgang() throws MissingInputError, InvalidInputError {
        if(!isJahrgangValid()) {
            StyleHelper.setRedStyle(jahrgang);
            jahrgang.requestFocus();

            if(jahrgang.getText().length() == 0) {
                throw new MissingInputError("Bitte gib den Jahrgang des Weines an.");
            } else {
                throw new InvalidInputError("Der Jahrgang soll als Jahreszahl eingegeben werden.");
            }
        }
    }
    void setupBeschreibung() {
        beschreibung.setText("");
        StyleHelper.setWhiteStyle(beschreibung);

        beschreibung.focusedProperty().addListener((observableValue, hatteFokus, hatFokus) -> {
            if(hatteFokus && isBeschreibungValid()) {
                StyleHelper.setWhiteStyle(beschreibung);
            }
        });
    }
    boolean isBeschreibungValid() {
        return beschreibung.getText().length() > 0;
    }
    private void validateBeschreibung() throws MissingInputError, InvalidInputError {
        if(!isBeschreibungValid()) {
            StyleHelper.setRedStyle(beschreibung);
            beschreibung.requestFocus();

            if(beschreibung.getText().length() == 0) {
                throw new MissingInputError("Bitte gib eine Beschreibung für deinen Wein ein.");
            } else {
                throw new InvalidInputError("Die Wein-Beschreibung ist so nicht richtig. <Platzhalter>.");
            }
        }
    }
    void setupLagerdauer() {
        var min = 1;
        var max = 30;
        var initialValue = 1;
        var stepBy = 1;

        var factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initialValue, stepBy);
        lagerdauer.setValueFactory(factory);

        /*
        lagerdauer.setText("1");
        StyleHelper.setWhiteStyle(lagerdauer);
        lagerdauer.promptTextProperty().set("1");
        lagerdauer.setTextFormatter(new TextFormatter(RegexHelper.patternFilter(lagerdauerEingabe)));
        */

        lagerdauer.focusedProperty().addListener((observableValue, hatteFokus, hatFokus) -> {
            if(hatteFokus && isLagerdauerValid()) {
                updateTrinkreifeDiagramm();
            }
        });
    }
    boolean isLagerdauerValid() {
        return true;
        //return RegexHelper.matchesPattern(lagerdauerExact, lagerdauer.getText());
    }
    void validateLagerdauer() throws InvalidInputError, MissingInputError {
        if(!isLagerdauerValid()) {
            /*
            StyleHelper.setRedStyle(lagerdauer);
            lagerdauer.requestFocus();

            if(lagerdauer.getText().length() == 0) {
                throw new MissingInputError("Bitte geben sie die Lagerdauer ihres Weines ein.");
            } else {
                throw new InvalidInputError("Bitte geben sie eine positive ganze Zahl für die Lagerdauer ein. Überprüfen sie ihre Eingabe.");
            }
            */
        }
    }
    private int getLagerdauer() {
        Spinner<Integer> mySpinner = (Spinner<Integer>) lagerdauer;
        return mySpinner.getValue();
    }

    Alkoholgehalt getAlkoholgehalt() {
        Spinner<Double> mySpinner = (Spinner<Double>) alkoholgehalt;
        return new Alkoholgehalt(alkoholfrei.isSelected(), mySpinner.getValue());
    }

    @FXML
    private void abbrechenPressed(ActionEvent event) {
        try {
            abort();
        } catch (InternalControllerError internalControllerError) {
            internalControllerError.printStackTrace();
            AlertHelper.showErrorAlert("Internal Controller Error", internalControllerError.getMessage());
        }
    }

    @FXML
    private void speichernPressed(ActionEvent event) {
        try {
            save();
        } catch (InternalControllerError internalControllerError) {
            internalControllerError.printStackTrace();
            AlertHelper.showErrorAlert(internalControllerError);
        }
    }

    public void setDelegate(WeinAufnehmenDelegate delegate) {
        this.delegate = delegate;
    }

    public void save() throws InternalControllerError {
        try {
            validateBestellnummer();
            validateBezeichnung();
            validateJahrgang();
            validateAnbaugebiet();
            validateBeschreibung();
            preisumrechnerController.validate();
            validateLagerdauer();
        } catch(InvalidInputError e) {
            AlertHelper.showAlert("Invalid input", e.getMessage(), Alert.AlertType.ERROR);
            return;
        } catch (MissingInputError e) {
            AlertHelper.showAlert("Missing input", e.getMessage(), Alert.AlertType.ERROR);
            return;
        }/* catch (InternalControllerError e) {
            AlertHelper.showAlert("Internal Controller Error", e.getMessage(), Alert.AlertType.ERROR);
        }*/

        delegate.addWein(getWein());

        reset();

        AlertHelper.showAlert("Notice", "Saving finished.", Alert.AlertType.INFORMATION);
    }

    public void abort() throws InternalControllerError {
        if(didSomethingChange()) {
            if(confirmAbort() == false) { return; }
        }

        forceClose();
    }
    boolean confirmAbort() {
        System.out.println("Confirming abort.");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.NO, ButtonType.YES);
        alert.setTitle("Abbrechen");
        alert.setHeaderText("");
        alert.setContentText("Es wurden Änderungen getätigt. Wollen sie die Änderungen verwerfen?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.YES;
    }
    boolean didSomethingChange() throws InternalControllerError {
        var currentWine = getWein();
        return currentWine.equals(pristineWine) == false;
    }

    WeinAlter getWeinAlter() throws InternalControllerError {
        if(!(isLagerdauerValid() && isJahrgangValid())) {
            throw new InternalControllerError("Die Lagerdauer oder der Jahrgang ist invalid. Das sollte nicht passieren.");
        }
        var lagerdauer = getLagerdauer();
        var jahrgang = Integer.parseInt(this.jahrgang.getText());
        return new WeinAlter(jahrgang, lagerdauer);
    }

    public void updateTrinkreifeDiagramm() {
        if(isLagerdauerValid() && isJahrgangValid()) {
            WeinAlter alter = null;
            try {
                alter = getWeinAlter();
            } catch (InternalControllerError internalControllerError) {
                internalControllerError.printStackTrace();
                AlertHelper.showErrorAlert("Internal Error", internalControllerError.getMessage());
                return;
            }
            System.out.println("Updating Trinkreifediagramm.");
            trinkreifeController.setup(alter.jahrgang, alter.lagerung, aktuellesJahr());
        }
    }

    int aktuellesJahr() { return Calendar.getInstance().get(Calendar.YEAR); }

    void reset() {
        setupBestellnummer();
        setupBezeichnung();
        setupFarben();
        setupAnbaugebiet();
        setupAlkoholgehalt();
        preisumrechnerController.reset();
        setupJahrgang();
        setupBeschreibung();
        setupLagerdauer();

        updateTrinkreifeDiagramm();
    }

    public void forceClose() {
        Stage stage = (Stage) jahrgang.getScene().getWindow();
        delegate.stopping(this);
        stage.close();
    }
}
