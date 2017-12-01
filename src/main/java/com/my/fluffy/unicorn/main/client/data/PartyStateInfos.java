package com.my.fluffy.unicorn.main.client.data;

import java.io.Serializable;

public class PartyStateInfos implements Serializable{
    Party party;
    State state;
    int additionalMandats;
    int baseseats;
    int landliststeas;
    int endseats;

    public PartyStateInfos(){

    }

    public PartyStateInfos(Party party, State state , int additional){
        this.party=party;
        this.state=state;
        this.additionalMandats=additional;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getAdditionalMandats() {
        return additionalMandats;
    }

    public void setAdditionalMandats(int additionalMandats) {
        this.additionalMandats = additionalMandats;
    }

    public int getBaseseats() {
        return baseseats;
    }

    public void setBaseseats(int baseseats) {
        this.baseseats = baseseats;
    }

    public int getLandliststeas() {
        return landliststeas;
    }

    public void setLandliststeas(int landliststeas) {
        this.landliststeas = landliststeas;
    }

    public int getEndseats() {
        return endseats;
    }

    public void setEndseats(int endseats) {
        this.endseats = endseats;
    }


}
