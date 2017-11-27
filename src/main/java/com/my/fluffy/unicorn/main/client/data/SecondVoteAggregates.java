package com.my.fluffy.unicorn.main.client.data;

import java.io.Serializable;

public class SecondVoteAggregates implements Serializable{
    private  District district;
    private  Party party;
    private  int secondVotes;

    public SecondVoteAggregates(District district, Party party, int secondVotes) {
        this.district = district;
        this.party = party;
        this.secondVotes = secondVotes;
    }

    public SecondVoteAggregates() {
    }

    public District getDistrict() {

        return district;
    }

    public Party getParty() {
        return party;
    }

    public int getSecondVotes() {
        return secondVotes;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public void setSecondVotes(int secondVotes) {
        this.secondVotes = secondVotes;
    }
}
