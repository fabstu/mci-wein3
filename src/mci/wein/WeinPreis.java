package mci.wein;

public class WeinPreis {
    int flaschengrößeIndex;
    double flaschenpreisBrutto;

    @Override
    public String toString() {
        return "[Flaschenpreis Brutto: " + flaschenpreisBrutto +
                ", FlaschengrößeIndex: " + flaschengrößeIndex + "]";
    }

    public WeinPreis(int flaschengrößeIndex, double flaschenpreisBrutto) {
        this.flaschengrößeIndex = flaschengrößeIndex;
        this.flaschenpreisBrutto = flaschenpreisBrutto;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) { return false; }
        if(obj instanceof WeinPreis == false) { return false; }
        WeinPreis a = this;
        WeinPreis b = (WeinPreis)obj;

        return a.flaschengrößeIndex == b.flaschengrößeIndex && a.flaschenpreisBrutto == b.flaschenpreisBrutto;
    }

    public double getBruttoFlaschenpreis() {
        return flaschenpreisBrutto;
    }

    public boolean isValid() {
        return 0 <= flaschengrößeIndex && flaschengrößeIndex < Weinflasche.alleGrößen.length &&
                0 <= flaschenpreisBrutto;
    }
}
