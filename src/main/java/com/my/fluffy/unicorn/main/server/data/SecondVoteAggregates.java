package com.my.fluffy.unicorn.main.server.data;

public class SecondVoteAggregates {
    private final District district;
    private final Party party;
    private final int secondVotes;

    public SecondVoteAggregates(District district, Party party, int secondVotes) {
        this.district = district;
        this.party = party;
        this.secondVotes = secondVotes;
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
}
