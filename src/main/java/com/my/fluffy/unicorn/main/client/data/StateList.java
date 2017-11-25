package com.my.fluffy.unicorn.main.client.data;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class StateList implements Serializable{
    private  Integer id;
    @NotNull private  Party party;
    @NotNull private  Election election;
    @NotNull private  State state;

    public StateList() {
    }

    @NotNull
    public static StateList fullCreate(Integer id, @NotNull Party party, @NotNull Election election, @NotNull State state) {
        return new StateList(id, party, election, state);
    }

    @NotNull
    public static StateList create(@NotNull Party party, @NotNull Election election, @NotNull State state) {
        return fullCreate(null, party, election, state);
    }

    /**
     * @deprecated
     */
    @Deprecated public StateList(@NotNull Party party, @NotNull Election election, @NotNull State state) {
        this(null, party, election, state);
    }

    private StateList(Integer id, @NotNull Party party, @NotNull Election election, @NotNull State state) {
        this.id = id;
        this.party = party;
        this.election = election;
        this.state = state;
    }

    @NotNull public Party getParty() {
        return party;
    }

    @NotNull public Election getElection() {
        return election;
    }

    @NotNull public State getState() {
        return state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public void setState(State state) {
        this.state = state;
    }
}
