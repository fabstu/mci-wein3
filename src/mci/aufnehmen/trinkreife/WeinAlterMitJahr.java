package mci.aufnehmen.trinkreife;

import mci.wein.WeinAlter;

public class WeinAlterMitJahr extends WeinAlter {
    public int aktuellesJahr;

    public WeinAlterMitJahr(int jahrgang, int lagerung, int aktuellesJahr) {
        super(jahrgang, lagerung);
        this.aktuellesJahr = aktuellesJahr;
    }
    public WeinAlterMitJahr(WeinAlter weinAlter, int aktuellesJahr) {
        super(weinAlter.jahrgang, weinAlter.lagerung);
        this.aktuellesJahr = aktuellesJahr;
    }

    @Override
    public String toString() {
        return super.toString() + " Aktuelles Jahr: " + aktuellesJahr;
    }
}
