package com.my.fluffy.unicorn.main.client.data;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class ListCandidature implements Serializable{
    private  Integer id;
    @NotNull private  Candidate candidate;
    @NotNull private  StateList stateList;
    private  Integer placement;

    public ListCandidature() {
    }

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

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public void setStateList(StateList stateList) {
        this.stateList = stateList;
    }

    public void setPlacement(Integer placement) {
        this.placement = placement;
    }
}
