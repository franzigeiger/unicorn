package com.my.fluffy.unicorn.main.server.data;

public class StateList {

    public int id;
    public int yearOfCandidature;
    public Party party;
    public State state;

    public StateList(int id, int yearOfCandidature, Party party, State state){
        this.id = id;
        this.yearOfCandidature = yearOfCandidature;
        this.party = party;
        this.state = state;
    }

}
