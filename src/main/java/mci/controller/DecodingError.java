package mci.controller;

import mci.wein.Wein;

import java.util.List;

public class DecodingError extends Exception {
    public List<Wein> damaged;
    public int countDamaged;
    public int countHealthy;

    public DecodingError(List<Wein> damanged, int countDamaged, int countHealthy) {
        this.damaged = damanged;
        this.countDamaged = countDamaged;
        this.countHealthy = countHealthy;
    }
}
