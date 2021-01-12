package mci.wein;

public class WeinFarbe {
    public int wert;

    public WeinFarbe(int wert) {
        this.wert = wert;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) { return false; }
        if(obj instanceof WeinFarbe == false) { return false; }
        WeinFarbe a = this;
        WeinFarbe b = (WeinFarbe)obj;
        return a.wert == b.wert;
    }

    @Override
    public String toString() {
        String farbe;
        switch(wert) {
            case 0:
                farbe = "weiß";
                break;
            case 1:
                farbe = "rot";
                break;
            case 2:
                farbe = "rosé";
                break;
            default:
                farbe = "unknown: " + wert;
        }
        return farbe;
    }

    public boolean isValid() {
        return 0 <= wert && wert <= 2;
    }
}
