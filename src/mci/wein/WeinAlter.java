/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mci.wein;

/**
 *
 * @author fabiansturm
 */
public class WeinAlter {
    public int jahrgang;
    public int lagerung;
    
    public WeinAlter(int jahrgang, int lagerung) {
        this.jahrgang = jahrgang;
        this.lagerung = lagerung;
    }

    public WeinAlter(String jahrgangString, String lagerungString) {
        jahrgang = Integer.parseInt(jahrgangString);
        lagerung = Integer.parseInt(lagerungString);
    }

    @Override
    public String toString() {
        return "Jahrgang: " + jahrgang + " Lagerung: " + lagerung;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) { return false; }
        if(obj instanceof WeinAlter == false) { return false; }
        WeinAlter a = this;
        WeinAlter b = (WeinAlter)obj;

        return a.jahrgang == b.jahrgang && a.lagerung == b.lagerung;
    }

    public boolean isValid() {
        return 1 <= lagerung && 1850 <= jahrgang && jahrgang <= 2020;
    }
}
