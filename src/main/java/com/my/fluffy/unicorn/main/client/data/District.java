package com.my.fluffy.unicorn.main.client.data;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class District implements Serializable{
    private  Integer id;
    private  int number;
    State state;
    private  String name;
    private  Integer eligibleVoters;
    private  Integer invalidFirstVotes;
    private  Integer invalidSecondVotes;
    Election election;

    public static District fullCreate(Integer id, int number,  Election election, State state, String name, Integer eligibleVoters, Integer invalidFirst, Integer invalidSecond) {
        return new District(id, number, election, state, name, eligibleVoters, invalidFirst, invalidSecond);
    }


    public static District create(int number,  Election election,  State state, String name, Integer eligibleVoters) {
        return District.fullCreate(null, number, election, state, name, eligibleVoters, null, null);
    }


    public static District minCreate(int number,  Election election) {
        return District.fullCreate(null, number, election, null, null, null, null, null);
    }

    /**
     * @param number Election district ID, between 1 and 299. Not unique across multiple elections.
     * @param election The election. (number, election) are unique.
     * @deprecated
     */
    public District(int number, Election election, int eligibleVoters, State state) {
        this(null, number, election, state, null, eligibleVoters, null, null);
    }

    private District(Integer id, int number,  Election election, State state, String name, Integer eligibleVoters, Integer invalidFirst, Integer invalidSecond) {
        this.id = id;
        this.number = number;
        this.election = election;
        this.state = state;
        this.name = name;
        this.eligibleVoters = eligibleVoters;
        this.invalidFirstVotes = invalidFirst;
        this.invalidSecondVotes = invalidSecond;
    }

    public District(){

    }

    public int getNumber() {
        return number;
    }

    public Election getElection() {
        return election;
    }

    public Integer getEligibleVoters() {
        return eligibleVoters;
    }

    public State getState() {
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

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEligibleVoters(Integer eligibleVoters) {
        this.eligibleVoters = eligibleVoters;
    }

    public void setInvalidFirstVotes(Integer invalidFirstVotes) {
        this.invalidFirstVotes = invalidFirstVotes;
    }

    public void setInvalidSecondVotes(Integer invalidSecondVotes) {
        this.invalidSecondVotes = invalidSecondVotes;
    }
}
