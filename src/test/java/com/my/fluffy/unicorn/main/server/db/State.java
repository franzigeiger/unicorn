package com.my.fluffy.unicorn.main.server.db;

import java.util.ArrayList;

public class State {

    public int id;
    public String name;

    public int eligibleVoters_17;
    public int eligebleVoters_13;
    public int voters_17;
    public int voters_13;

    public int valid_17_first;
    public int valid_13_first;
    public int invalid_17_first;
    public int invalid_13_first;

    public int valid_17_second;
    public int valid_13_second;
    public int invalid_17_second;
    public int invalid_13_second;

    public ArrayList<PartyResults> partyResults;

    public State(int id, String name,
                 int eligibleVoters_13, int eligibleVoters_17,
                 int voters_13, int voters_17,
                 int valid_13_first, int valid_17_first,
                 int invalid_13_first, int invalid_17_first,
                 int valid_13_second, int valid_17_second,
                 int invalid_13_second, int invalid_17_second,
                 ArrayList<PartyResults> partyResults){

        this.id = id;
        this.name = name;

        this.eligebleVoters_13 = eligibleVoters_13;
        this.eligibleVoters_17 = eligibleVoters_17;

        this.voters_13 = voters_13;
        this.voters_17 = voters_17;

        this.valid_13_first = valid_13_first;
        this.valid_17_first = valid_17_first;
        this.invalid_13_first = invalid_13_first;
        this.invalid_17_first = invalid_17_first;

        this.valid_13_second = valid_13_second;
        this.valid_17_second = valid_17_second;
        this.invalid_13_second = invalid_13_second;
        this.invalid_17_second = invalid_17_second;

        this.partyResults = partyResults;
    }
}
