package mci.liste;

import mci.controller.Unterfenster;
import mci.controller.UnterfensterDelegate;
import mci.wein.Wein;

public interface WeinListeDelegate extends UnterfensterDelegate {
    public void weinSelectionChanged(Wein selectedWein);
}
