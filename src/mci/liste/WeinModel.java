package mci.liste;

import javafx.beans.property.SimpleStringProperty;
import mci.wein.Wein;

public class WeinModel {
    public SimpleStringProperty bestellnummer;
    public SimpleStringProperty jahrgang;
    public SimpleStringProperty name;
    public SimpleStringProperty farbe;
    public SimpleStringProperty bruttoflaschenpreis;

    public WeinModel(Wein wein) {
        this.bestellnummer = new SimpleStringProperty(wein.bestellnummer);
        jahrgang = new SimpleStringProperty("" + wein.weinAlter.jahrgang);
        name = new SimpleStringProperty(wein.bezeichnung);
        farbe = new SimpleStringProperty(wein.farbe.toString());
        bruttoflaschenpreis = new SimpleStringProperty("" + wein.weinPreis.getBruttoFlaschenpreis());
    }
}
