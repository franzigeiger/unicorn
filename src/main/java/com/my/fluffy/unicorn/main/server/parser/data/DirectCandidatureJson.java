package com.my.fluffy.unicorn.main.server.parser.data;

public class DirectCandidatureJson {

    public ElectionDistrictJson electionDistrictJson;
    public PartyJson partyJson;

    public DirectCandidatureJson(ElectionDistrictJson electionDistrictJson, PartyJson partyJson){
        this.electionDistrictJson = electionDistrictJson;
        this.partyJson = partyJson;
    }
}
