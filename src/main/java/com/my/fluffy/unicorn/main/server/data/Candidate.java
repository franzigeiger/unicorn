package com.my.fluffy.unicorn.main.server.data;

public class Candidate {

    private final String title;
    private final String firstName;
    private final String lastName;

    private final String profession;
    private final String sex;
    private final String hometown;

    private final String birthtown;
    private final int yearOfBirth;

    public Candidate(String title, String firstName, String lastName, String profession, String sex, String hometown, String birthtown, int yearOfBirth) {
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profession = profession;
        this.sex = sex;
        this.hometown = hometown;
        this.birthtown = birthtown;
        this.yearOfBirth = yearOfBirth;
    }

    public String getTitle() {
        return title;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProfession() {
        return profession;
    }

    public String getSex() {
        return sex;
    }

    public String getHometown() {
        return hometown;
    }

    public String getBirthtown() {
        return birthtown;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }
}
