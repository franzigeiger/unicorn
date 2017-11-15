package com.my.fluffy.unicorn.main.server.data;

import org.jetbrains.annotations.NotNull;

public class ListCandidature {
    private final Integer id;
    @NotNull private final Candidate candidate;
    @NotNull private final StateList stateList;
    private final Integer placement;

    @NotNull public static ListCandidature fullCreate(Integer id, @NotNull Candidate candidate, @NotNull StateList stateList, Integer placement) {
        return new ListCandidature(id, candidate, stateList, placement);
    }

    @NotNull public static ListCandidature create(@NotNull Candidate candidate, @NotNull StateList stateList, Integer placement) {
        return new ListCandidature(null, candidate, stateList, placement);
    }

    @NotNull public static ListCandidature minCreate(@NotNull Candidate candidate, @NotNull StateList stateList) {
        return new ListCandidature(null, candidate, stateList, null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public ListCandidature(Candidate candidate, StateList stateList, int placement) {
        this (null, candidate, stateList, placement);
    }

    private ListCandidature(Integer id, @NotNull Candidate candidate, @NotNull StateList stateList, Integer placement) {
        this.id = id;
        this.candidate = candidate;
        this.stateList = stateList;
        this.placement = placement;
    }

    public Integer getId() {
        return id;
    }

    @NotNull public Candidate getCandidate() {
        return candidate;
    }

    @NotNull public StateList getStateList() {
        return stateList;
    }

    public int getPlacement() {
        return placement;
    }
}
