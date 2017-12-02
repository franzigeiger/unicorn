package com.my.fluffy.unicorn.main.client.data;

import java.io.Serializable;

public class DistrictResults implements Serializable{
    private String partyName;
    private int first13;
    private int second13;
    private int first17;
    private int second17;

    public static DistrictResults fullCreate(String party, int first13, int second13, int first17, int second17) {
        return new DistrictResults(party, first13, second13, first17, second17);
    }


    public static DistrictResults create(String party, int first13, int second13, int first17, int second17) {
        return DistrictResults.fullCreate(party, first13, second13, first17, second17);
    }


    public static DistrictResults minCreate(String party, int first13, int second13, int first17, int second17) {
        return DistrictResults.fullCreate(party, first13, second13, first17, second17);
    }

    /*public DistrictResults(int partyId, int first, int second) {
        this(partyId, first, second);
    }*/

    private DistrictResults(String party, int first13, int second13, int first17, int second17) {
        this.partyName = party;
        this.first13 = first13;
        this.second13 = second13;
        this.first17 = first17;
        this.second17 = second17;
    }

    public DistrictResults(){

    }

    public String getPartyName() {
        return partyName;
    }

    public int getFirst13() {
        return first13;
    }

    public int getSecond13() {
        return second13;
    }

    public int getFirst17() {
        return first17;
    }

    public int getSecond17() {
        return second17;
    }

    @Override
    public String toString() {
        return "District{" +
                "partyId=" + partyName +
                ", first13=" + first13 +
                ", second13=" + second13 +
                ", first17=" + first17 +
                ", second17=" + second17 +
                '}';
    }

    public void setPartyId(String party) {
        this.partyName = party;
    }

    public void setFirst13(int number) {
        this.first13 = number;
    }
    public void setSecond13(int number) {
        this.second13 = number;
    }
    public void setFirst17(int number) {
        this.first17 = number;
    }
    public void setSecond17(int number) {
        this.second17 = number;
    }
}
