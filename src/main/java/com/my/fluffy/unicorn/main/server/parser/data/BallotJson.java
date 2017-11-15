package com.my.fluffy.unicorn.main.server.parser.data;

public class BallotJson {

    //id of candidate, -1 if vote is invalid
    public CandidateJson firstVote;
    //id of partyJson, -1 if vote is invalid
    public PartyJson secondVote;

    public ElectionDistrictJson electionDistrictJson;

    public int year;

    public BallotJson(CandidateJson first, PartyJson second, ElectionDistrictJson electionDistrictJson, int year){
        this.firstVote = first;
        this.secondVote = second;
        this.electionDistrictJson = electionDistrictJson;
        this.year = year;
    }

}
