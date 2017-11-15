package com.my.fluffy.unicorn.main.server.parser.data;

public class CandidateJson {

    public int id;

    public String name;
    public String firstName;
    public String title;
    public String gender;

    public String hometown;
    public String profession;

    public int yearOfCandidature;

    public int birthYear;
    public String birthPlace;

    public DirectCandidatureJson directCandidatureJson;
    public ListPlacementJson listPlacementJson;

    public CandidateJson(int id, String name, String firstName, String title,
                         String gender, String hometown, String profession,
                         int yearOfCandidaturee, int birthYear, String birthPlace,
                         DirectCandidatureJson directCandidatureJson, ListPlacementJson listPlacementJson){

        this.id = id;

        this.name = name;
        this.firstName = firstName;
        this.title = title;

        this.gender = gender;
        this.hometown = hometown;
        this.profession = profession;

        this.yearOfCandidature = yearOfCandidaturee;

        this.birthYear = birthYear;
        this.birthPlace = birthPlace;

        this.directCandidatureJson = directCandidatureJson;
        this.listPlacementJson = listPlacementJson;
    }
}
