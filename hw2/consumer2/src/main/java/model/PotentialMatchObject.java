package model;

import java.util.ArrayList;
import java.util.HashSet;

public class PotentialMatchObject {

    private HashSet<String> potentialMatches;

    public PotentialMatchObject() {
        this.potentialMatches = new HashSet<>(100);
    }

    public HashSet<String> getPotentialMatches() {
        return potentialMatches;
    }

    public void setPotentialMatches(HashSet<String> potentialMatches) {
        this.potentialMatches = potentialMatches;
    }

    public void addToPotentialMatches(String candidate) {
        this.potentialMatches.add(candidate);
    }
}
