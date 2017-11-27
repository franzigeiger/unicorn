package com.my.fluffy.unicorn.main.client.data;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class Candidate implements Serializable{
    private  Integer id;

    public Candidate() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public void setBirthtown(String birthtown) {
        this.birthtown = birthtown;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    private  String title;
    private  String firstName;
    private  String lastName;

    private  String profession;
    private  String sex;
    private  String hometown;

    private  String birthtown;
    private  int yearOfBirth;

    /**
     * Only for DB use intended. You should probably use {@link #create(String, String, String, String, String, String, String, int)} instead.
     */

    public static Candidate fullCreate(Integer id, String title, String firstName, String lastName, String profession, String sex, String hometown, String birthtown, int yearOfBirth) {
        return new Candidate(id, title, firstName, lastName, profession, sex, hometown, birthtown, yearOfBirth);
    }


    public static Candidate create(String title,  String firstName, String lastName, String profession, String sex, String hometown, String birthtown, int yearOfBirth) {
        return fullCreate(null, title, firstName, lastName, profession, sex, hometown, birthtown, yearOfBirth);
    }


    public static Candidate minCreate(String firstName, String lastName, int yearOfBirth) {
        return fullCreate(null, null, firstName, lastName, null, null, null, null, yearOfBirth);
    }


    public Candidate(String title, String firstName, String lastName, String profession, String sex, String hometown, String birthtown, int yearOfBirth) {
        this(null, title, firstName, lastName, profession, sex, hometown, birthtown, yearOfBirth);
    }

    private Candidate(Integer id, String title, String firstName,  String lastName, String profession, String sex, String hometown, String birthtown, int yearOfBirth) {
        this.id = id;
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

    public Integer getId() {
        return id;
    }
}
