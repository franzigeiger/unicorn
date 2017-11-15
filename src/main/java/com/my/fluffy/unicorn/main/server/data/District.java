package com.my.fluffy.unicorn.main.server.data;

import org.jetbrains.annotations.NotNull;

public class District {
    private final Integer id;
    private final int number;
    @NotNull private final Election election;
    @NotNull private final State state;
    private final String name;
    private final int eligibleVoters;
    private final Integer invalidFirstVotes;
    private final Integer invalidSecondVotes;

    @NotNull
    public static District fullCreate(Integer id, int number, @NotNull Election election, @NotNull State state, String name, int eligibleVoters, Integer invalidFirst, Integer invalidSecond) {
        return new District(id, number, election, state, name, eligibleVoters, invalidFirst, invalidSecond);
    }

    @NotNull
    public static District create(int number, @NotNull Election election, @NotNull State state, String name, int eligibleVoters) {
        return District.fullCreate(null, number, election, state, name, eligibleVoters, null, null);
    }

    @NotNull
    public static District minCreate(int number, @NotNull Election election, @NotNull State state, int eligibleVoters) {
        return District.fullCreate(null, number, election, state, null, eligibleVoters, null, null);
    }

    /**
     * @param number Election district ID, between 1 and 299. Not unique across multiple elections.
     * @param election The election. (number, election) are unique.
     * @deprecated
     */
    @Deprecated public District(int number, Election election, int eligibleVoters, State state) {
        this(null, number, election, state, null, eligibleVoters, null, null);
    }

    private District(Integer id, int number, @NotNull Election election, @NotNull State state, String name, int eligibleVoters, Integer invalidFirst, Integer invalidSecond) {
        this.id = id;
        this.number = number;
        this.election = election;
        this.state = state;
        this.name = name;
        this.eligibleVoters = eligibleVoters;
        this.invalidFirstVotes = invalidFirst;
        this.invalidSecondVotes = invalidSecond;
    }

    public int getNumber() {
        return number;
    }

    @NotNull public Election getElection() {
        return election;
    }

    public int getEligibleVoters() {
        return eligibleVoters;
    }

    @NotNull public State getState() {
        return state;
    }

    public Integer getId() {
        return id;
    }

    public Integer getInvalidFirstVotes() {
        return invalidFirstVotes;
    }

    public Integer getInvalidSecondVotes() {
        return invalidSecondVotes;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "District{" +
                "id=" + id +
                ", number=" + number +
                ", election=" + election +
                ", state=" + state +
                ", name='" + name + '\'' +
                ", eligibleVoters=" + eligibleVoters +
                ", invalidFirstVotes=" + invalidFirstVotes +
                ", invalidSecondVotes=" + invalidSecondVotes +
                '}';
    }
}
