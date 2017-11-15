package com.my.fluffy.unicorn.main.server.parser.data;

public class StateListJson {

    public int yearOfCandidature;
    public PartyJson partyJson;
    public StateJson state;

    public StateListJson(int yearOfCandidature, PartyJson partyJson, StateJson state){
        this.yearOfCandidature = yearOfCandidature;
        this.partyJson = partyJson;
        this.state = state;
    }

}
