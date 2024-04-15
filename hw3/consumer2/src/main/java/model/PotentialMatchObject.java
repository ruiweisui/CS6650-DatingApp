package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PotentialMatchObject {

    private String swiper;
    private List<String> potentialMatches;

    public PotentialMatchObject() {
        this.potentialMatches = new ArrayList<>(100);
    }

    public String getSwiper() {
        return swiper;
    }

    public void setSwiper(String swiper) {
        this.swiper = swiper;
    }

    public List<String> getPotentialMatches() {
        return potentialMatches;
    }

    public void setPotentialMatches(List<String> potentialMatches) {
        this.potentialMatches = potentialMatches;
    }

    public void addToPotentialMatches(String candidate) {
        this.potentialMatches.add(candidate);
    }
}
