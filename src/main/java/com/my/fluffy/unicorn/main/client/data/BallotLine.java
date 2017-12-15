package com.my.fluffy.unicorn.main.client.data;

import java.io.Serializable;
import java.util.List;

public class BallotLine implements Serializable {
    private Party party;
    private DirectCandidature directCandidate;
    private List<ListCandidature> listCandidates;
    private int placement;

    public BallotLine() {
    }

    public void setParty(Party p) { this.party = p; }

    public void setDirectCandidate(DirectCandidature c) {
        this.directCandidate = c;
    }

    public void setListCandidates(List<ListCandidature> l) {
        this.listCandidates = l;
    }

    public void setPlacement(Integer p) {
        this.placement = p;
    }

    public static BallotLine fullCreate(Party party, DirectCandidature directCandidate, List<ListCandidature> listCandidate, int placement) {
        return new BallotLine(party, directCandidate, listCandidate, placement);
    }

    public static BallotLine create(Party party, DirectCandidature directCandidate, List<ListCandidature> listCandidate, int placement) {
        return fullCreate(party, directCandidate, listCandidate, placement);
    }


    public static BallotLine minCreate(Party party, DirectCandidature directCandidate, List<ListCandidature> listCandidate, int placement) {
        return fullCreate(party, directCandidate, listCandidate, placement);
    }

    private BallotLine(Party party, DirectCandidature directCandidate, List<ListCandidature> listCandidate, int placement) {
        this.party = party;
        this.directCandidate = directCandidate;
        this.listCandidates = listCandidate;
        this.placement = placement;
    }

    public Party getParty() {
        return party;
    }

    public DirectCandidature getDirectCandidate() {
        return directCandidate;
    }

    public List<ListCandidature> getListCandidates() {
        return listCandidates;
    }

    public Integer getPlacement() {
        return placement;
    }
}
