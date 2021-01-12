/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mci.aufnehmen.preisumrechner;

import mci.wein.WeinPreis;
import mci.wein.Weinflasche;

import java.util.ArrayList;

/**
 *
 * @author fabiansturm
 */
public class Preisumrechner {
    
    private double flaschenpreisBrutto;
    private double preisProLiter;
    private int flaschengrößeIndex;
    
    public Preisumrechner() {
        reset();
    }
    public void reset() {
        flaschenpreisBrutto = 0.00;
        preisProLiter = 0.00;
        flaschengrößeIndex = 6;
    }
    
    // Flaschengrößen
    public double[] getFlaschengrößen() {
        return Weinflasche.alleGrößen;
    }
    public String[] getFlaschengrößenStrings() {
//        String[] strings = new String[];
        ArrayList<String> flaschengrößenStrings = new ArrayList<>();
        for (double größe: Weinflasche.alleGrößen) {
            flaschengrößenStrings.add(Double.toString(größe));
        }
        
        String[] array = new String[flaschengrößenStrings.size()];
        array = flaschengrößenStrings.toArray(array);
        
        return array;
    }
    
    public int getFlaschengrößeIndex() {
        return flaschengrößeIndex;
    }
    public void setFlaschengrößeIndex(int newIndex) {
        if(newIndex == -1) { return; }
        flaschengrößeIndex = newIndex;
    }

    // Berechnungen
    public void berechneFlaschenpreis() {
        System.out.println("volumen: " + getVolumen());
        System.out.println("Preis pro Liter: " + preisProLiter);
        flaschenpreisBrutto = preisProLiter * getVolumen();
    }

    public void berechnePreisProLiter() {
        System.out.println("volumen: " + getVolumen());
        System.out.println("Flaschenpreis: " + flaschenpreisBrutto);
        preisProLiter = flaschenpreisBrutto * 1.0 / getVolumen();
    }
    public WeinPreis getWeinPreis() {
        System.out.println("flaschengrößeIndex = " + flaschengrößeIndex);
        return new WeinPreis(flaschengrößeIndex, flaschenpreisBrutto);
    }
    
    // Attribute
    public double getPreisProLiter() {
        return preisProLiter;
    }
    public void setPreisProLiter(double preisProLiter) {
        this.preisProLiter = preisProLiter;
    }

    public double getFlaschenpreisBrutto() {
        return flaschenpreisBrutto;
    }
    public double getFlaschenpreisNetto() {
        return flaschenpreisBrutto / 119.0 * 100;
    }
    public void setFlaschenpreisBrutto(double flaschenpreisBrutto) {
        this.flaschenpreisBrutto = flaschenpreisBrutto;
    }
    
    /**
     * Gibt das aktuell ausgewählte Volumen zurück.
     */
    private double getVolumen() {
        return Weinflasche.alleGrößen[flaschengrößeIndex];
    }
}
