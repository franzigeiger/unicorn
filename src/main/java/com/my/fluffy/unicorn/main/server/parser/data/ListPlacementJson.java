package com.my.fluffy.unicorn.main.server.parser.data;

public class ListPlacementJson {

    public int place;
    public StateListJson stateListJson;

    public ListPlacementJson(int place, StateListJson stateListJson){
        this.place = place;
        this.stateListJson = stateListJson;
    }

}
