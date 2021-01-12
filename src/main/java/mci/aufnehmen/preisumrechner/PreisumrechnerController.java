/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mci.aufnehmen.preisumrechner;

import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import mci.aufnehmen.*;
import mci.controller.AlertHelper;
import mci.wein.WeinPreis;

/**
 *
 * @author fabiansturm
 */
public class PreisumrechnerController implements Initializable {
    // Inputs
    @FXML
    private TextField textFieldFlaschenpreisBrutto;
    @FXML
    private TextField textFieldFlaschenpreisNetto;
    @FXML
    private TextField textFieldPreisProLiter;
    @FXML
    private ChoiceBox flaschengrößeAuswahl;

    /**
     * Initializes the scene.
     *
     * @param url not used.
     * @param rb not used.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setup();
    }

    /// Data and buiseness logic implementation.
    private final Preisumrechner rechner = new Preisumrechner();

    // Number formatter
    private static final NumberFormat numberFormat = NumberFormat.getInstance();
    private static final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
    private static final int MAX_FRAC_DIGITS = 2;
    private static final int MIN_FRAC_DIGITS = 0;

    private static final String groupingSymbol = Pattern.quote(symbols.getGroupingSeparator() + "");
    private static final String decimalSymbol = Pattern.quote(symbols.getDecimalSeparator() + "");

    enum MasterEingabe {
        PREISPROLITER, PREISPROFLASCHE
    }

    PreisumrechnerController.MasterEingabe masterEingabe = MasterEingabe.PREISPROFLASCHE;

    // Beispiele:
    // - 1,00
    // - 0,5
    // - ,5
    // - ,33
    // - 11,3
    // - 11,55
    // - 7Pattern
    Pattern eingabePattern = Pattern.compile("^((([1-9][0-9]*?([" + groupingSymbol + "][0-9]{3})*?([" + groupingSymbol + "][0-9]{0,3})*)|0|)([" + decimalSymbol + "][0-9]{0,2})?)?$");
    Pattern exactPattern = Pattern.compile("^((([1-9][0-9]*?([" + groupingSymbol + "][0-9]{3})*?)|0)([" + decimalSymbol + "][0-9]{1,2})?)$");

    private void showEingabefehler() {
        AlertHelper.showErrorAlert("Eingabefehler", "Bitte geben sie eine Zahl im Format XXX.XXX.XXX,yy ein.");
    }

    /**
     * Initializes the scene.
     */
    public void setup() {
        // Setup ChoiceBox.
        flaschengrößeAuswahl.setItems(FXCollections.observableArrayList(rechner.getFlaschengrößenStrings()));
        flaschengrößeAuswahl.getSelectionModel().select(rechner.getFlaschengrößeIndex());
        flaschengrößeAuswahl.getSelectionModel().selectedIndexProperty()
                .addListener((ov, value, new_value) -> {
                    rechner.setFlaschengrößeIndex(new_value.intValue());
                    switch (masterEingabe) {
                        case PREISPROLITER:
                            // Only calculate incase Preisumrechner has up-to-date values. An invalid text field means it does not so skip calculation.
                            if (preisProLiterValid()) {
                                //rechner.setPreisProLiter(readPreis(textFieldPreisProLiter));
                                rechner.berechneFlaschenpreis();
                                updateBruttoTextField();
                                updateNettoTextField();
                            }
                            break;
                        case PREISPROFLASCHE:
                            // Only calculate incase Preisumrechner has up-to-date values. An invalid text field means it does not so skip calculation.
                            if (preisProFlascheBruttoValid()) {
                                //rechner.setFlaschenpreisBrutto(readPreis(textFieldFlaschenpreisBrutto));
                                rechner.berechnePreisProLiter();
                                updatePreisProLiterTextField();
                            }
                            break;
                    }
                });
        flaschengrößeAuswahl.setTooltip(new Tooltip("Wähle eine Flaschengröße aus."));

        // Setup the number formatter.
        numberFormat.setMinimumFractionDigits(MIN_FRAC_DIGITS);
        numberFormat.setMaximumFractionDigits(MAX_FRAC_DIGITS);
        numberFormat.setRoundingMode(RoundingMode.HALF_UP);
        numberFormat.setMinimumIntegerDigits(1);
//        numberFormat.setMaximumIntegerDigits(3);

        // Setup allowed text format.
        textFieldFlaschenpreisBrutto.setTextFormatter(new TextFormatter(RegexHelper.patternFilter(eingabePattern)));
        textFieldFlaschenpreisNetto.setTextFormatter(new TextFormatter(RegexHelper.patternFilter(eingabePattern)));
        textFieldPreisProLiter.setTextFormatter(new TextFormatter(RegexHelper.patternFilter(eingabePattern)));

        textFieldFlaschenpreisBrutto.promptTextProperty().set("0,00");
        textFieldPreisProLiter.promptTextProperty().set("0,00");

        // Incase Flaschenpreis text fieldgot focus,
        // then make buttonDown the default action.
        textFieldFlaschenpreisBrutto.focusedProperty().addListener((arg0, hatteFokus, newPropertyValue) -> {
            if (hatteFokus) {
                var text = textFieldFlaschenpreisBrutto.getText();
                if (text.length() != 0 && preisProFlascheBruttoValid() == false) {
                    StyleHelper.setRedStyle(textFieldFlaschenpreisBrutto);
                } else {
                    // Clear red. Both since if one is valid the other was updated.
                    StyleHelper.setWhiteStyle(textFieldFlaschenpreisBrutto);
                    StyleHelper.setWhiteStyle(textFieldPreisProLiter);
                }
            }
        });

        // Incase PreisProLiter text fieldgot focus,
        // then make buttonUp the default action.
        textFieldPreisProLiter.focusedProperty().addListener((arg0, hatteFokus, newPropertyValue) -> {
            if (hatteFokus) {
                var text = textFieldPreisProLiter.getText();
                if (text.length() != 0 && preisProLiterValid() == false) {
                    // Make red.
                    StyleHelper.setRedStyle(textFieldPreisProLiter);
                } else {
                    // Clear red. Both since if one is valid the other was updated.
                    StyleHelper.setWhiteStyle(textFieldFlaschenpreisBrutto);
                    StyleHelper.setWhiteStyle(textFieldPreisProLiter);
                }
            }
        });

        // Enable or disable buttons appropriately after change.
        textFieldFlaschenpreisBrutto.textProperty().addListener((arg0, hatteFokus, newPropertyValue) -> {
            // So a change from a user does not cascade. Handle initial changes only in one place.
            if (textFieldFlaschenpreisBrutto.focusedProperty().get()) {
                if (preisProFlascheBruttoValid()) {
                    rechner.setFlaschenpreisBrutto(readPreis(textFieldFlaschenpreisBrutto));
                    rechner.berechnePreisProLiter();
                    updateNettoTextField();
                    updatePreisProLiterTextField();

                    // Clear red color.
                    StyleHelper.setWhiteStyle(textFieldFlaschenpreisBrutto);
                    StyleHelper.setWhiteStyle(textFieldPreisProLiter);
                } else {
                    textFieldFlaschenpreisNetto.setText("");
                    textFieldPreisProLiter.setText("");
                }

                // Change came from user input so make master.
                masterEingabe = MasterEingabe.PREISPROFLASCHE;
                System.out.println("ppF is master.");
            }
        });
        textFieldPreisProLiter.textProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            // So a change from a user does not cascade. Handle initial changes only in one place.
            if (textFieldPreisProLiter.focusedProperty().get()) {
                if (preisProLiterValid()) {
                    rechner.setPreisProLiter(readPreis(textFieldPreisProLiter));
                    rechner.berechneFlaschenpreis();
                    updateNettoTextField();
                    updateBruttoTextField();

                    // Clear red color.
                    StyleHelper.setWhiteStyle(textFieldFlaschenpreisBrutto);
                    StyleHelper.setWhiteStyle(textFieldPreisProLiter);
                } else {
                    textFieldFlaschenpreisNetto.setText("");
                    textFieldFlaschenpreisBrutto.setText("");
                }

                // Only make it master input incase the change came from user input.
                masterEingabe = MasterEingabe.PREISPROLITER;
                System.out.println("ppL is master.");
            }
        });
    }

    public boolean isValid() {
        return preisProLiterValid() && preisProFlascheBruttoValid();
    }
    public void markErrors() throws MissingInputError, InvalidInputError {
        switch(masterEingabe) {
            case PREISPROLITER:
                if(!preisProLiterValid()) {
                    StyleHelper.setRedStyle(textFieldPreisProLiter);

                    if(textFieldPreisProLiter.getText().length() == 0) {
                        throw new MissingInputError("Der Flaschenpreis wurde noch nicht richtig eingegeben.");
                    } else {
                        throw new InvalidInputError("Der Flaschenpreis wurde noch nicht eingegeben.");
                    }
                }
                break;
            case PREISPROFLASCHE:
                // Do only have to mark the primary, since the secondary will be set automatically once the primary input is valid.
                if(!preisProFlascheBruttoValid()) {
                    StyleHelper.setRedStyle(textFieldFlaschenpreisBrutto);

                    if(textFieldFlaschenpreisBrutto.getText().length() == 0) {
                        throw new MissingInputError("Der Flaschenpreis wurde noch nicht richtig eingegeben.");
                    } else {
                        throw new InvalidInputError("Der Flaschenpreis wurde noch nicht eingegeben.");
                    }
                }
                break;
        }
    }
    public WeinPreis getWeinPreis() {
        return rechner.getWeinPreis();
    }
    public void validate() throws MissingInputError, InvalidInputError {
        if(isValid()) {
            return;
        }
        markErrors();
    }

    boolean preisProLiterValid() {
        return RegexHelper.matchesPattern(exactPattern, textFieldPreisProLiter.getText());
    }

    boolean preisProFlascheBruttoValid() {
        return RegexHelper.matchesPattern(exactPattern, textFieldFlaschenpreisBrutto.getText());
    }

    double readPreis(TextField textField) {
        double preis = -1;
        try {
            preis = numberFormat.parse(textField.getText()).doubleValue();
        } catch (ParseException e) {
            System.out.println("Failed to update netto from brutto despite brutto being failed.");
            e.printStackTrace();
            System.exit(1);
        }
        return preis;
    }

    void updateNettoTextField() {
        textFieldFlaschenpreisNetto.setText(numberFormat.format(rechner.getFlaschenpreisNetto()));
    }

    void updatePreisProLiterTextField() {
        textFieldPreisProLiter.setText(numberFormat.format(rechner.getPreisProLiter()));
    }

    void updateBruttoTextField() {
        textFieldFlaschenpreisBrutto.setText(numberFormat.format(rechner.getFlaschenpreisBrutto()));
    }

    public void reset() {
        textFieldFlaschenpreisBrutto.setText("");
        textFieldPreisProLiter.setText("");
        textFieldFlaschenpreisNetto.setText("");
        rechner.reset();
        flaschengrößeAuswahl.getSelectionModel().select(rechner.getFlaschengrößeIndex());
    }
}
