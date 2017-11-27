package com.my.fluffy.unicorn.main.client.data;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class DirectCandidature implements Serializable{
    private  Integer id;
    private  District district;
    private  Candidate candidate;
    private  Party party;
    private  Integer votes;

    public DirectCandidature() {
    }


    public static DirectCandidature fullCreate(Integer id,District district, Candidate candidate, @NotNull Party party, Integer votes) {
        return new DirectCandidature(id, district, candidate, party, votes);
    }


    public static DirectCandidature create(District district,  Candidate candidate, Party party) {
        return fullCreate(null, district, candidate, party, null);
    }

  public DirectCandidature(District district, Candidate candidate, Party party) {
        this(null, district, candidate, party, null);
    }

    private DirectCandidature(Integer id, District district, Candidate candidate, Party party, Integer votes) {
        this.id = id;
        this.district = district;
        this.candidate = candidate;
        this.party = party;
        this.votes = votes;
    }

    public Integer getId() {
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

    public Integer getVotes() {
        return votes;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }
}
