package com.my.fluffy.unicorn.main.server.data;

public class ListCandidature {
    private final Candidate candidate;
    private final StateList stateList;
    private final int placement;

    public ListCandidature(Candidate candidate, StateList stateList, int placement) {
        this.candidate = candidate;
        this.stateList = stateList;
        this.placement = placement;
    }

    public Candidate getCandidate() {

        return candidate;
    }

    public StateList getStateList() {
        return stateList;
    }

    public int getPlacement() {
        return placement;
    }
}
