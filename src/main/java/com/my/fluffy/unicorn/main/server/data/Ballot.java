package com.my.fluffy.unicorn.main.server.data;

public class Ballot {

    //id of candidate, -1 if vote is invalid
    public int firstVote;
    //id of party, -1 if vote is invalid
    public int secondVote;

    public int districtId;

    public Ballot(int first, int second, int districtId){
        this.firstVote = first;
        this.secondVote = second;
        this.districtId = districtId;
    }

}
