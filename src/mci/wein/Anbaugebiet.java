package mci.wein;

import java.util.Arrays;
import java.util.HashMap;

public class Anbaugebiet {
    private int land;
    private int region;

    public Anbaugebiet(int land, int region) {
        this.land = land;
        this.region = region;
    }

    public int getLand() { return land; }
    public int getRegion() { return region; }

    public String getLandString() { return getCountryString(land); }
    public String getRegionString() {
        return getRegionString(land, region);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) { return false; }
        if(obj instanceof Anbaugebiet == false) { return false; }
        Anbaugebiet a = this;
        Anbaugebiet b = (Anbaugebiet)obj;

        return a.land == b.land && a.region == b.region;
    }

    @Override
    public String toString() {
        return "Land: " + land + " Region: " + region;
    }

    private static HashMap<String, String[]> countries;
    public static HashMap<String, String[]> getCountries() {
        if(countries == null) {
            // Load countries.
            countries = new HashMap<String, String[]>();

            countries.put("Deutschland", new String[]{
                    "Baden-Württemberg",
                    "Nordrhein-Westfalen",
                    "Brandenburg",
                    "Hamburg",
            });
            countries.put("Frankreich", new String[]{
                    "Beaujolais",
                    "Bordeaux",
                    "Burgund",
                    "Champagne",
                    "Elsass",
                    "Jura",
                    "Korsika",
                    "Languedoc",
                    "Roussillon",
            });
        }
        return countries;
    }
    public static String[] getCountryNamesSorted() {
        var countryNames = (String[]) getCountries().keySet().toArray(new String[getCountries().keySet().size()]);
        Arrays.sort(countryNames);
        return countryNames;
    }
    public static String[] getRegionsOfCountry(int country) {
        return getCountries().get(getCountryString(country));
    }
    public static String getCountryString(int country) {
        return getCountryNamesSorted()[country];
    }
    public static String getRegionString(int country, int region) { return getRegionsOfCountry(country)[region]; }

    private boolean isValidLand() {
        return -1 <= land && land < getCountries().size();
    }

    public boolean isValid() {
        if(land == -1) {
            return region == -1;
        }
        if(region == -1) {
            return isValidLand();
        }
        if(!isValidLand()) {
            return false;
        }

        var regions = getRegionsOfCountry(land);
        return -1 <= region && region < regions.length;
    }
}
