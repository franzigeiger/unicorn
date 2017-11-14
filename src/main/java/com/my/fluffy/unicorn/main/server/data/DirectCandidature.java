package com.my.fluffy.unicorn.main.server.data;

public class DirectCandidature {
    private final int id;
    private final District district;
    private final Candidate candidate;
    private final Party party;
    private final int votes;

    public DirectCandidature(int id, District district, Candidate candidate, Party party, int votes) {
        this.id = id;
        this.district = district;
        this.candidate = candidate;
        this.party = party;
        this.votes = votes;
    }

    public int getId() {
        return id;
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

    public int getVotes() {
        return votes;
    }
}
