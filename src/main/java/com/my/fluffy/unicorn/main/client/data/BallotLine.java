package com.my.fluffy.unicorn.main.client.data;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

public class BallotLine implements Serializable {
    private Party party;
    private Candidate directCandidate;
    private DirectCandidature directCandidature;
    private List<ListCandidature> listCandidates;
    private int placement;

    public BallotLine() {
    }

    public DirectCandidature getDirectCandidature() {
        return directCandidature;
    }

    public void setParty(Party p) { this.party = p; }

    public void setDirectCandidate(Candidate c) {
        this.directCandidate = c;
    }

    public void setListCandidates(List<ListCandidature> l) {
        this.listCandidates = l;
    }

    public void setPlacement(Integer p) {
        this.placement = p;
    }

    public static BallotLine fullCreate(Party party, Candidate directCandidate, List<ListCandidature> listCandidate, int placement) {
        return new BallotLine(party, directCandidate, listCandidate, placement);
    }

    public static BallotLine create(Party party, Candidate directCandidate, List<ListCandidature> listCandidate, int placement) {
        return fullCreate(party, directCandidate, listCandidate, placement);
    }


    public static BallotLine minCreate(Party party, Candidate directCandidate, List<ListCandidature> listCandidate, int placement) {
        return fullCreate(party, directCandidate, listCandidate, placement);
    }

    private BallotLine(Party party, Candidate directCandidate, List<ListCandidature> listCandidate, int placement) {
        this.party = party;
        this.directCandidate = directCandidate;
        this.listCandidates = listCandidate;
        this.placement = placement;
    }

    public Party getParty() {
        return party;
    }

    public Candidate getDirectCandidate() {
        return directCandidate;
    }

    public List<ListCandidature> getListCandidates() {
        return listCandidates;
    }

    public Integer getPlacement() {
        return placement;
    }
}
