package model;

import hibernate.Spec;
import java.util.Comparator;

public class SpecComparator implements Comparator<Spec>{
    
    private static SpecComparator sc;

    @Override
    public int compare(Spec s1, Spec s2) {
//        return s1.getSpecName().compareTo(s2.getSpecName());
        return s1.getId() - s2.getId();
    }
    
    public static Comparator getComparator() {
        if (sc == null) {
            sc = new SpecComparator();
        }
        return sc;
    }
}
