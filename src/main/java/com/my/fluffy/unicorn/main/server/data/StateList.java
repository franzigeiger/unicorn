package com.my.fluffy.unicorn.main.server.data;

public class StateList {

    public int yearOfCandidature;
    public Party party;
    public State state;

    public StateList(int yearOfCandidature, Party party, State state){
        this.yearOfCandidature = yearOfCandidature;
        this.party = party;
        this.state = state;
    }

}
