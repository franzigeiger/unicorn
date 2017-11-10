package com.my.fluffy.unicorn.main.server.db;

public class Candidate {

    public int id;
    public String name;
    public String firstName;
    public String title;
    public String gender;
    public String hometown;
    public String profession;
    public boolean repeatedCandidature;
    public int birthYear;
    public String birthPlace;
    public DirectCandidature directCandidature;
    public ListPlacement listPlacement;

    public Candidate(int id, String name, String firstName, String title,
                     String gender, String hometown, String profession,
                     boolean repeatedCandidature, int birthYear, String birthPlace,
                     DirectCandidature directCandidature, ListPlacement listPlacement){
        this.id = id;
        this.name = name;
        this.firstName = firstName;
        this.title = title;
        this.gender = gender;
        this.hometown = hometown;
        this.profession = profession;
        this.repeatedCandidature = repeatedCandidature;
        this.birthYear = birthYear;
        this.birthPlace = birthPlace;
        this.directCandidature = directCandidature;
        this.listPlacement = listPlacement;
    }
}
