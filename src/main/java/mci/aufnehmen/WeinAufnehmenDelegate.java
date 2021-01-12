package mci.aufnehmen;

import mci.controller.UnterfensterDelegate;
import mci.wein.Wein;

public interface WeinAufnehmenDelegate extends UnterfensterDelegate {
    void addWein(Wein wein);
}
