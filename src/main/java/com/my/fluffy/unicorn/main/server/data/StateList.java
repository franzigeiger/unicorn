package com.my.fluffy.unicorn.main.server.data;

import org.jetbrains.annotations.NotNull;

public class StateList {
    private final Integer id;
    @NotNull private final Party party;
    @NotNull private final Election election;
    @NotNull private final State state;

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
}
