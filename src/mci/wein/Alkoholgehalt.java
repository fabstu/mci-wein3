package mci.wein;

public class Alkoholgehalt {
    public boolean alkoholfrei;
    public double wert;

    public Alkoholgehalt(boolean alkoholfrei, double wert) {
        this.alkoholfrei = alkoholfrei;
        this.wert = wert;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) { return false; }
        if(obj instanceof Alkoholgehalt == false) { return false; }
        Alkoholgehalt a = this;
        Alkoholgehalt b = (Alkoholgehalt)obj;
        return a.alkoholfrei == b.alkoholfrei && a.wert == b.wert;
    }

    @Override
    public String toString() {
        return "Alkoholfrei: " + alkoholfrei + " Alkohol-Wert: " + wert;
    }

    public boolean isValid() {
        return 7.5 <= wert && wert <= 25;
    }
}
