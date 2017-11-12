package com.my.fluffy.unicorn.main.server.db;

import java.util.ArrayList;

public class ElectionDistrict extends State {

    public String state;

    public ElectionDistrict(String state, int id, String name,
                            int eligibleVoters_13, int eligibleVoters_17,
                            int voters_13, int voters_17,
                            int valid_13_first, int valid_17_first,
                            int invalid_13_first, int invalid_17_first,
                            int valid_13_second, int valid_17_second,
                            int invalid_13_second, int invalid_17_second,
                            ArrayList<PartyResults> partyResults){
        super(id, name,
                eligibleVoters_13, eligibleVoters_17,
                voters_13, voters_17,
                valid_13_first, valid_17_first,
                invalid_13_first, invalid_17_first,
                valid_13_second, valid_17_second,
                invalid_13_second, invalid_17_second,
                partyResults);
        this.state = state;
    }
}
