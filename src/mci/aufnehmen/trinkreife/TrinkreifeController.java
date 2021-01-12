/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mci.aufnehmen.trinkreife;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import mci.wein.WeinAlter;

/**
 *
 * @author fa97377
 */
public class TrinkreifeController implements Initializable {

    @FXML
    private StackPane stackPane;
    
    @FXML
    private HBox hboxForeground;
    @FXML
    private HBox hboxBackground;

    /// Rectangles
    @FXML
    private Rectangle untrinkbar;
    @FXML
    private Label untrinkbarLabel;
    @FXML
    private Rectangle übergang; // Um Übergang farbig zu machen.
    @FXML
    private Rectangle best;
    @FXML
    private Rectangle ablaufJahr;
    @FXML
    private Label ablaufJahrLabel;

    // Background
    @FXML
    private Rectangle backgroundRect1;
    @FXML
    private Rectangle backgroundRect2;
    @FXML
    private Label currentYearLabel;
    @FXML
    private Rectangle backgroundRect3;
    
    // Label positioning
    @FXML
    private StackPane labelsStackPane;
    
    @FXML
    private Rectangle offsetX1;
    @FXML
    private Rectangle offsetY1;
    
    @FXML
    private Rectangle offsetX2;
    @FXML
    private Rectangle offsetY2;
    
    @FXML
    private Rectangle offsetX3;
    @FXML
    private Rectangle offsetY3;

    public void setup(int jahrgang, int lagerdauer, int aktuellesJahr) throws IllegalArgumentException {
        // Detect illegal arguments.
        if (jahrgang > aktuellesJahr) {
            throw new IllegalArgumentException("Jahrgang muss <= aktuelles Jahr sein.");
        }
        if (lagerdauer <= 0) {
            throw new IllegalArgumentException("Lagerdauer muss 1 oder größer sein.");
        }
        if (jahrgang <= 1900) {
            throw new IllegalArgumentException("Der WeinAlter ist zu alt.");
        }
        if (aktuellesJahr <= 2017) {
            throw new IllegalArgumentException("Das aktuelle Jahr muss > 2017 sein.");
        }

        // Reset visibility.
        untrinkbarLabel.setVisible(true);
        currentYearLabel.setVisible(true);
        ablaufJahrLabel.setVisible(true);

        backgroundRect1.setVisible(true);
        backgroundRect2.setVisible(true);
        backgroundRect3.setVisible(true);

        // Lesejahr + Lagerjahre + Ablaufjahr
        int gesamtJahre = 1 + lagerdauer + 1;

        int gesamtTrinkbar = 1 + lagerdauer;

        System.out.println("gesamtJahre: " + gesamtJahre);
        System.out.println("gesamtTrinkbar: " + gesamtTrinkbar);

        // Subtract padding.
        DoubleBinding stackPanePaddedWidth = stackPane.widthProperty().subtract(60.0);
        DoubleBinding stackPanePaddedHeight = stackPane.heightProperty().subtract(60.0);

        System.out.println("stackPaneWidth: " + stackPane.widthProperty().get());
        System.out.println("stackPanePaddedWidth: " + stackPanePaddedWidth.get());

        // Anteil eines Jahres an den Gesamtjahren
        double jahrFraction = 1.0 / gesamtJahre;

        System.out.println("jahrFraction: " + jahrFraction);

        // trinkbarWidth = gesamtWidth * ()
        DoubleBinding trinkbarWidth = stackPanePaddedWidth.multiply(1 - jahrFraction);
        double trinkbarFraction = 1.0 / 8.0;

        System.out.println("trinkbarWidth: " + trinkbarWidth.toString());

        // Im ersten Achtel untrinkbar
        double untrinkbarAnteil = 1;
        double besterAnteil = 4;
        double übergangAnteil = 8 - untrinkbarAnteil - besterAnteil;

        System.out.println("tBar, übergang, best: " + untrinkbarAnteil + ", " + übergangAnteil + ", " + besterAnteil);

        // Bind HBox size to StackPane size
        //hboxForeground.prefWidthProperty().bind(stackPane.widthProperty());
        //hboxForeground.prefHeightProperty().bind(stackPane.heightProperty());
        //hboxBackground.prefWidthProperty().bind(stackPane.widthProperty());
        //hboxBackground.prefHeightProperty().bind(stackPane.heightProperty());
        // Set hgrow to make children take up all space in hbox - widthProperty does not do it.
        //HBox.setHgrow(hboxForeground, Priority.ALWAYS);
        //HBox.setHgrow(hboxBackground, Priority.ALWAYS);
        
        hboxForeground.prefWidthProperty().bind(stackPanePaddedWidth.subtract(10));
        hboxBackground.prefWidthProperty().bind(stackPanePaddedWidth.subtract(10));
        
        // Foreground rectangles
        untrinkbar.widthProperty().bind(trinkbarWidth.multiply(
                untrinkbarAnteil * trinkbarFraction)
        );
        übergang.widthProperty().bind(trinkbarWidth.multiply(
                übergangAnteil * trinkbarFraction)
        );
        best.widthProperty().bind(trinkbarWidth.multiply(
                besterAnteil * trinkbarFraction)
        );
        ablaufJahr.widthProperty().bind(stackPanePaddedWidth.multiply(
                jahrFraction)
        );
//        ablaufJahr.widthProperty().bind(stackPanePaddedWidth.subtract(trinkbarWidth));
//        ablaufJahr.widthProperty().bind(stackPanePaddedWidth.subtract(
//                untrinkbar.widthProperty().add(übergang.widthProperty()).add(best.widthProperty())
//        ));
        
        System.out.println("untrinkbar.widthProperty().get(): " + untrinkbar.widthProperty().get() );
        
        double gesamtVordergrundBreite = untrinkbar.widthProperty().get() +
                übergang.widthProperty().get() +
                best.widthProperty().get() +
                ablaufJahr.widthProperty().get();
        
        System.out.println("gesamtVordergrundBreite: " + gesamtVordergrundBreite);

        untrinkbar.heightProperty().bind(stackPanePaddedHeight);
        übergang.heightProperty().bind(stackPanePaddedHeight);
        best.heightProperty().bind(stackPanePaddedHeight);
        ablaufJahr.heightProperty().bind(stackPanePaddedHeight);

        // Von 0 bis lagerdauer möglich.
        int relativesJahr = aktuellesJahr - jahrgang;

        System.out.println("relativesJahr: " + relativesJahr);

        // Background rectangles
        if (aktuellesJahr > jahrgang + lagerdauer + 1) {
            // Zeige einzig einen Kasten um die gesamte HBox.
            backgroundRect1.widthProperty().bind(stackPanePaddedWidth);
            backgroundRect2.setVisible(false);
            backgroundRect3.setVisible(false);
            
            currentYearLabel.setVisible(false);
        } else {
            backgroundRect1.widthProperty().bind(stackPanePaddedWidth.multiply(relativesJahr * jahrFraction));
            backgroundRect2.widthProperty().bind(stackPanePaddedWidth.multiply(jahrFraction));
            backgroundRect3.widthProperty().bind(stackPanePaddedWidth.multiply((gesamtJahre - (relativesJahr + 1)) * jahrFraction));
        }

        backgroundRect1.heightProperty().bind(stackPanePaddedHeight);
        backgroundRect2.heightProperty().bind(stackPanePaddedHeight);
        backgroundRect3.heightProperty().bind(stackPanePaddedHeight);

        // Populate labels:
        untrinkbarLabel.setText("" + jahrgang);
        currentYearLabel.setText("" + aktuellesJahr);
        ablaufJahrLabel.setText("" + (jahrgang + lagerdauer + 1));

        if (relativesJahr == 0) {
            untrinkbarLabel.setVisible(false);
        }
        if (aktuellesJahr == jahrgang + lagerdauer + 1) {
            ablaufJahrLabel.setVisible(false);
        }
        
        
        DoubleBinding offsetYHeight = stackPanePaddedHeight.add(10);
        
        // Position labels
//        labelsStackPane.prefHeightProperty().bind(stackPanePaddedHeight);
        
        offsetX1.widthProperty().setValue(0);
        offsetY1.heightProperty().bind(offsetYHeight);
        
        offsetX2.widthProperty().bind(stackPanePaddedWidth.multiply(relativesJahr * jahrFraction));
        offsetY2.heightProperty().bind(offsetYHeight);
        
        offsetX3.widthProperty().bind(stackPanePaddedWidth.multiply(1 - jahrFraction));
        offsetY3.heightProperty().bind(offsetYHeight);
        
        System.out.println("hboxForegroundWidth: " + hboxForeground.widthProperty().get());
        System.out.println("hboxbackgroundWidth: " + hboxBackground.widthProperty().get());
        System.out.println();
    }

    int aktuellerWein = 3;
    ArrayList<WeinAlterMitJahr> weine = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        weine.add(new WeinAlterMitJahr(2018, 1, 2018));
        weine.add(new WeinAlterMitJahr(2017, 1, 2018));

        weine.add(new WeinAlterMitJahr(2016, 1, 2018));
        weine.add(new WeinAlterMitJahr(2015, 1, 2018));

        weine.add(new WeinAlterMitJahr(2015, 4, 2018));
        weine.add(new WeinAlterMitJahr(2015, 5, 2018));

        weine.add(new WeinAlterMitJahr(2016, 4, 2018));
        weine.add(new WeinAlterMitJahr(2011, 7, 2018));

        setupWein();

        StackPane tempStackPane = stackPane;

        /*
        // Timer to add the key events after the scene actually exists.
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Executing timer.");
                Scene scene = tempStackPane.getScene();

                scene.setOnKeyReleased(event -> {
                    String codeString = event.getCode().toString();
                    switch (codeString) {
                        case "RIGHT":
                            setupNächstenWein();
                            break;
                        case "LEFT":
                            setupVorigenWein();
                            break;
                    }
                });
                timer.cancel();
            }
        }, 1 * 60);
        */
    }

    public void setupVorigenWein() {
        aktuellerWein--;
        if (aktuellerWein < 0) {
            aktuellerWein = weine.size() - 1;
        }
        setupWein();
    }

    public void setupNächstenWein() {
        aktuellerWein = aktuellerWein + 1 < weine.size() ? aktuellerWein + 1 : 0;
        setupWein();
    }

    private void setupWein() {
        System.out.println("aktuellerWein: " + aktuellerWein);
        var w = weine.get(aktuellerWein);
        setup(w.jahrgang, w.lagerung, w.aktuellesJahr);
    }
}
