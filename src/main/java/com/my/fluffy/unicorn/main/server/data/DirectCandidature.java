package com.my.fluffy.unicorn.main.server.data;

public class DirectCandidature {

    public ElectionDistrict electionDistrict;
    public Party party;

    public DirectCandidature(ElectionDistrict electionDistrict, Party party){
        this.electionDistrict = electionDistrict;
        this.party = party;
    }
}
