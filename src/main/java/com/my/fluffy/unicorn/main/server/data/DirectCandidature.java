package com.my.fluffy.unicorn.main.server.data;

public class DirectCandidature {
    private final District district;
    private final Candidate candidate;
    private final Party party;
    // DB: aggregate votes

    public DirectCandidature(District district, Candidate candidate, Party party) {
        this.district = district;
        this.candidate = candidate;
        this.party = party;
    }

    public District getDistrict() {
        return district;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public Party getParty() {
        return party;
    }
}
