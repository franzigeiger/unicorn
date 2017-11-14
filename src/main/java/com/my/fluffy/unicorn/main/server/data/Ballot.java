package com.my.fluffy.unicorn.main.server.data;

public class Ballot {

    //id of candidate, -1 if vote is invalid
    public Candidate firstVote;
    //id of party, -1 if vote is invalid
    public Party secondVote;

    public ElectionDistrict electionDistrict;

    public int year;

    public Ballot(Candidate first, Party second, ElectionDistrict electionDistrict, int year){
        this.firstVote = first;
        this.secondVote = second;
        this.electionDistrict = electionDistrict;
        this.year = year;
    }

}
