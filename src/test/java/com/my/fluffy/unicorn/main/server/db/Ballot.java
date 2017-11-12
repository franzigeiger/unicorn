package com.my.fluffy.unicorn.main.server.db;

public class Ballot {

    //id of candidate, -1 if vote is invalid
    public int firstVote;
    //id of party, -1 if vote is invalid
    public int secondVote;

    public Ballot(int first, int second){
        this.firstVote = first;
        this.secondVote = second;
    }

}
