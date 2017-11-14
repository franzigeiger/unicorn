package com.my.fluffy.unicorn.main.server.data;

public class StateList {
    private final Party party;
    private final Election election;
    private final State state;

    public StateList(Party party, Election election, State state) {
        this.party = party;
        this.election = election;
        this.state = state;
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
