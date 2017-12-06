package com.my.fluffy.unicorn.main.client.data;

import java.io.Serializable;

public class Top10Data implements Serializable{
    private Candidate candidate;
    private boolean isWinner;
    private int differenceInAbsoluteVotes;

    public Top10Data() {

    }

    public void setCandidate(Candidate c) {
        this.candidate = c;
    }

    public void setIsWinner(boolean w) {
        this.isWinner = w;
    }

    public void setDifferenceInAbsoluteVotes(int d) {
        this.differenceInAbsoluteVotes = d;
    }

    public static Top10Data fullCreate(Candidate candidate, boolean isWinner, int differenceInAbsoluteVotes) {
        return new Top10Data(candidate, isWinner, differenceInAbsoluteVotes);
    }

    public static Top10Data create(Candidate candidate, boolean isWinner, int differenceInAbsoluteVotes) {
        return fullCreate(candidate, isWinner, differenceInAbsoluteVotes);
    }

    public static Top10Data minCreate(Candidate candidate, boolean isWinner, int differenceInAbsoluteVotes) {
        return fullCreate(candidate, isWinner, differenceInAbsoluteVotes);
    }

    /*public Top10Data(Candidate candidate, boolean isWinner, int differenceInAbsoluteVotes) {
        this(candidate, isWinner, differenceInAbsoluteVotes);
    }*/

    private Top10Data(Candidate candidate, boolean isWinner, int differenceInAbsoluteVotes) {
        this.candidate = candidate;
        this.isWinner = isWinner;
        this.differenceInAbsoluteVotes = differenceInAbsoluteVotes;
    }

    public Candidate getCandidate() { return candidate; }

    public boolean getIsWinner() { return isWinner; }

    public int getDifferenceInAbsoluteVotes() { return differenceInAbsoluteVotes; }
}
