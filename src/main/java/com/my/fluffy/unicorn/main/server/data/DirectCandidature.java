package com.my.fluffy.unicorn.main.server.data;

import org.jetbrains.annotations.NotNull;

public class DirectCandidature {
    private final Integer id;
    @NotNull private final District district;
    @NotNull private final Candidate candidate;
    @NotNull private final Party party;
    private final Integer votes;

    @NotNull
    public static DirectCandidature fullCreate(Integer id, @NotNull District district, @NotNull Candidate candidate, @NotNull Party party, Integer votes) {
        return new DirectCandidature(id, district, candidate, party, votes);
    }

    @NotNull
    public static DirectCandidature create(@NotNull District district, @NotNull Candidate candidate, @NotNull Party party) {
        return fullCreate(null, district, candidate, party, null);
    }

    /**
     * @deprecated
     */
    @Deprecated public DirectCandidature(District district, Candidate candidate, Party party) {
        this(null, district, candidate, party, null);
    }

    private DirectCandidature(Integer id, @NotNull District district, @NotNull Candidate candidate, @NotNull Party party, Integer votes) {
        this.id = id;
        this.district = district;
        this.candidate = candidate;
        this.party = party;
        this.votes = votes;
    }

    public Integer getId() {
        return id;
    }

    @NotNull public District getDistrict() {
        return district;
    }

    @NotNull public Candidate getCandidate() {
        return candidate;
    }

    @NotNull public Party getParty() {
        return party;
    }

    public Integer getVotes() {
        return votes;
    }
}
