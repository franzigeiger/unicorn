package com.my.fluffy.unicorn.main.client.data;

import java.io.Serializable;

public class DifferenceFirstSecondVotes implements Serializable{
    private  int diff;
    private int firstVotes;
    private int secondVotes;
    private String districtName;
    private int year;

    public static DifferenceFirstSecondVotes create(
            int diff, int firstVotes, int secondVotes, String districtName, int year) {
        return new DifferenceFirstSecondVotes(
                diff, firstVotes,secondVotes, districtName, year
        );
    }

    private DifferenceFirstSecondVotes(int diff, int firstVotes, int secondVotes, String districtName, int year) {
        this.diff = diff;
        this.firstVotes = firstVotes;
        this.secondVotes = secondVotes;
        this.districtName = districtName;
        this.year = year;
    }

    public DifferenceFirstSecondVotes(){

    }

    public int getDiff() {
        return diff;
    }

    public int getFirstVotes() {
        return firstVotes;
    }

    public int getSecondVotes() {
        return secondVotes;
    }

    public String getDistrictName() {
        return districtName;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "DiffFirstSecondVotes{" +
                "diff=" + diff +
                ", first votes=" + firstVotes +
                ", second votes=" + secondVotes +
                ", district=" + districtName +
                ", year='" + year +
                '}';
    }
}
