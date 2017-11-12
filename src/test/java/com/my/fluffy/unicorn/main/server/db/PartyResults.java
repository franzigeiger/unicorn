package com.my.fluffy.unicorn.main.server.db;

public class PartyResults {

    public int first_17;
    public int first_13;
    public int second_17;
    public int second_13;

    public boolean electable_first_13;
    public boolean electable_first_17;
    public boolean electable_second_13;
    public boolean electable_second_17;

    public int party;

    public PartyResults(int first_13, int first_17, int second_13, int second_17,
                        boolean electable_first_13, boolean electable_first_17,
                        boolean electable_second_13, boolean electable_second_17, int party){

        this.first_13 = first_13;
        this.first_17 = first_17;
        this.second_13 = second_13;
        this.second_17 = second_17;

        this.electable_first_13 = electable_first_13;
        this.electable_first_17 = electable_first_17;
        this.electable_second_13 = electable_second_13;
        this.electable_second_17 = electable_second_17;

        this.party = party;
    }

}
