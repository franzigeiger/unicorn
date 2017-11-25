package com.my.fluffy.unicorn.main.client.data;

import java.io.Serializable;

public class Ballot implements Serializable{
    private  StateList stateList;
    private  DirectCandidature directCandidature;
    private  District district;

    public Ballot(StateList stateList, DirectCandidature directCandidature, District district) {
        this.stateList = stateList;
        this.directCandidature = directCandidature;
        this.district = district;
    }

    public Ballot() {
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

    public void setStateList(StateList stateList) {
        this.stateList = stateList;
    }

    public void setDirectCandidature(DirectCandidature directCandidature) {
        this.directCandidature = directCandidature;
    }

    public void setDistrict(District district) {
        this.district = district;
    }
}
