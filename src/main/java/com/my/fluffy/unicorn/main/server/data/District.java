package com.my.fluffy.unicorn.main.server.data;

public class District {
    private final int id;
    private final Election election;
    private final int eligibleVoters;
    private final int invalidFirstVotes;
    private final int invalidSecondVotes;
    private final State state;

    public District(int id, Election election, int eligibleVoters, int invalidFirstVotes, int invalidSecondVotes, State state) {
        this.id = id;
        this.election = election;
        this.eligibleVoters = eligibleVoters;
        this.invalidFirstVotes = invalidFirstVotes;
        this.invalidSecondVotes = invalidSecondVotes;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public Election getElection() {
        return election;
    }

    public int getEligibleVoters() {
        return eligibleVoters;
    }

    public int getInvalidFirstVotes() {
        return invalidFirstVotes;
    }

    public int getInvalidSecondVotes() {
        return invalidSecondVotes;
    }

    public State getState() {
        return state;
    }
}
