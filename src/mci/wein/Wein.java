package mci.wein;

import mci.aufnehmen.RegexHelper;
import mci.controller.InvalidFormatException;

import java.util.Base64;
import java.util.regex.Pattern;

public class Wein {
    public static Pattern bestellnummerExact = Pattern.compile("^[0-9]{4}-[A-Z]{3}-[0-9]{4}$");
    public String bestellnummer;
    public String bezeichnung;
    public WeinFarbe farbe;
    public Alkoholgehalt alkoholgehalt;
    public String beschreibung;

    public WeinAlter weinAlter;
    public WeinPreis weinPreis;
    public Anbaugebiet anbaugebiet;

    private static final int SPALTENZAHL = 12;

    public Wein(WeinAlter alter, WeinPreis preis, String bestellnummer, String bezeichnung, String beschreibung, WeinFarbe farbe, Alkoholgehalt alkoholgehalt,
                Anbaugebiet anbaugebiet) {
        this.weinAlter = alter;
        this.weinPreis = preis;
        this.bestellnummer = bestellnummer;
        this.bezeichnung = bezeichnung;
        this.beschreibung = beschreibung;
        this.farbe = farbe;
        this.alkoholgehalt = alkoholgehalt;
        this.anbaugebiet = anbaugebiet;
    }

    public Wein(String csvZeile) throws InvalidFormatException {
        var values = csvZeile.split(";");
        if(values.length != SPALTENZAHL) {
            throw new InvalidFormatException("Zeile hat das falsche Format. Skipping. " + csvZeile);
        }
        try {
            bestellnummer = values[0];
            weinAlter = new WeinAlter(values[1], values[9]);
            bezeichnung = values[2];
            farbe = new WeinFarbe(Integer.parseInt(values[3]));
            anbaugebiet = new Anbaugebiet(Integer.parseInt(values[4]), Integer.parseInt(values[5]));
            beschreibung = new String(Base64.getDecoder().decode(values[6]));
            alkoholgehalt = new Alkoholgehalt(Boolean.parseBoolean(values[7]), Double.parseDouble(values[8]));
            weinPreis = new WeinPreis(Integer.parseInt(values[10]), Double.parseDouble(values[11]));
        } catch(IllegalArgumentException e) {
            throw new InvalidFormatException("Zeile hat das falsche Format. Skipping, " + e.toString());
        }
    }
    public String toCSV() {
        String[] values = new String[SPALTENZAHL];
        values[0] = bestellnummer;
        values[1] = String.valueOf(weinAlter.jahrgang);
        values[2] = bezeichnung;
        values[3] = String.valueOf(farbe.wert);
        values[4] = "" + anbaugebiet.getLand();
        values[5] = "" + anbaugebiet.getRegion();
        values[6] = Base64.getEncoder().encodeToString(beschreibung.getBytes());
        values[7] = String.valueOf(alkoholgehalt.alkoholfrei);
        values[8] = String.valueOf(alkoholgehalt.wert);
        values[9] = "" + weinAlter.lagerung;
        values[10] = Integer.toString(weinPreis.flaschengrößeIndex);
        values[11] = Double.toString(weinPreis.flaschenpreisBrutto);
        System.out.println(Double.toString(weinPreis.flaschengrößeIndex));
        System.out.println(Double.toString(weinPreis.flaschenpreisBrutto));
        return String.join(";", values);
    }

    @Override
    public String toString() {
        return "Bestellnummer: " + bestellnummer +
                "\nBezeichnung: " + bezeichnung +
                "\nFarbe: " + farbe.toString() +
                "\nAnbaugebiet: " + anbaugebiet +
                "\nAlkoholgehalt: " + alkoholgehalt.toString() +
                "\nBeschreibung: " + beschreibung +
                "\nWeinAlter: " + weinAlter.toString() +
                "\nWeinPreis: " + weinPreis.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) { return false; }
        if(obj instanceof Wein == false) { return false; }
        Wein a = this;
        Wein b = (Wein)obj;

        System.out.println("a.bestellnummer = " + a.bestellnummer);
        System.out.println("b.bestellnummer = " + b.bestellnummer);

        if(!a.bestellnummer.equals(b.bestellnummer)) { return false; }
        if(!a.bezeichnung.equals(b.bezeichnung)) { return false; }
        if(!a.farbe.equals(b.farbe)) { return false; }
        if(!a.beschreibung.equals(b.beschreibung)) { return false; }
        if(!a.alkoholgehalt.equals(b.alkoholgehalt)) { return false; }
        if(!a.weinAlter.equals(b.weinAlter)) { return false; }
        if(!a.anbaugebiet.equals(b.anbaugebiet)) { return false; }
        if(!a.weinPreis.equals(b.weinPreis)) { return false; }

        return true;
    }

    public String getBestellnummer() {
        return bestellnummer;
    }
    public String getBezeichnung() {
        return bezeichnung;
    }
    public String getJahrgang() {
        return "" + weinAlter.jahrgang;
    }
    public String getFarbe() {
        return farbe.toString();
    }
    public String getBruttoFlaschenpreis() {
        return "" + weinPreis.flaschenpreisBrutto;
    }

    public boolean isValid() {
        return RegexHelper.matchesPattern(bestellnummerExact, bestellnummer) &&
                farbe.isValid() &&
                weinPreis.isValid() &&
                weinAlter.isValid() &&
                anbaugebiet.isValid() &&
                alkoholgehalt.isValid();
    }
}
