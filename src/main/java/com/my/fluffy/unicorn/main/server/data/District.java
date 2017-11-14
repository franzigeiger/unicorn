package com.my.fluffy.unicorn.main.server.data;

public class District {
    private final int number;
    private final Election election;
    private final int eligibleVoters;
    // db only: private final int invalidFirstVotes;
    // db only: private final int invalidSecondVotes;
    private final State state;

    /**
     * @param number Election district ID, between 1 and 299. Not unique across multiple elections.
     * @param election The election. (number, election) are unique.
     */
    public District(int number, Election election, int eligibleVoters, State state) {
        this.number = number;
        this.election = election;
        this.eligibleVoters = eligibleVoters;
        this.state = state;
    }

    public int getNumber() {
        return number;
    }

    public Election getElection() {
        return election;
    }

    public int getEligibleVoters() {
        return eligibleVoters;
    }

    public State getState() {
        return state;
    }
}
