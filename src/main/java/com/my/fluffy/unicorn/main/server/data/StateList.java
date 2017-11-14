package com.my.fluffy.unicorn.main.server.data;

public class StateList {
    private final int id;
    private final Party party;
    private final Election election;
    private final State state;

    public StateList(int id, Party party, Election election, State state) {
        this.id = id;
        this.party = party;
        this.election = election;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public Party getParty() {
        return party;
    }

    public Election getElection() {
        return election;
    }

    public State getState() {
        return state;
    }
}
