package com.my.fluffy.unicorn.main.server.data;

public class Ballot {
    private final StateList stateList;
    private final DirectCandidature directCandidature;
    private final District district;

    public Ballot(StateList stateList, DirectCandidature directCandidature, District district) {
        this.stateList = stateList;
        this.directCandidature = directCandidature;
        this.district = district;
    }

    public StateList getStateList() {

        return stateList;
    }

    public DirectCandidature getDirectCandidature() {
        return directCandidature;
    }

    public District getDistrict() {
        return district;
    }
}
