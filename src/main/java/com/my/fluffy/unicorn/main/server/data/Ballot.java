package com.my.fluffy.unicorn.main.server.data;

public class Ballot {

    //id of candidate, -1 if vote is invalid
    public int firstVote;
    //id of party, -1 if vote is invalid
    public int secondVote;

    public ElectionDistrict electionDistrict;

    public Ballot(int first, int second, ElectionDistrict electionDistrict){
        this.firstVote = first;
        this.secondVote = second;
        this.electionDistrict = electionDistrict;
    }

}
