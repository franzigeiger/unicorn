package com.my.fluffy.unicorn.main.server.data;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class Candidate {
    private final Integer id;

    private final String title;
    @NotNull private final String firstName;
    @NotNull private final String lastName;

    private final String profession;
    private final String sex;
    private final String hometown;

    private final String birthtown;
    private final int yearOfBirth;

    /**
     * Only for DB use intended. You should probably use {@link #create(String, String, String, String, String, String, String, int)} instead.
     */
    @NotNull
    public static Candidate fullCreate(Integer id, String title, @NotNull String firstName, @NotNull String lastName, String profession, String sex, String hometown, String birthtown, int yearOfBirth) {
        return new Candidate(id, title, firstName, lastName, profession, sex, hometown, birthtown, yearOfBirth);
    }

    @NotNull
    public static Candidate create(String title, @NotNull String firstName, @NotNull String lastName, String profession, String sex, String hometown, String birthtown, int yearOfBirth) {
        return fullCreate(null, title, firstName, lastName, profession, sex, hometown, birthtown, yearOfBirth);
    }

    @NotNull
    public static Candidate minCreate(@NotNull String firstName, @NotNull String lastName, int yearOfBirth) {
        return fullCreate(null, null, firstName, lastName, null, null, null, null, yearOfBirth);
    }

    /**
     * @deprecated Use {@link #create(String, String, String, String, String, String, String, int)} instead
     */
    @Deprecated
    public Candidate(String title, String firstName, String lastName, String profession, String sex, String hometown, String birthtown, int yearOfBirth) {
        this(null, title, firstName, lastName, profession, sex, hometown, birthtown, yearOfBirth);
    }

    private Candidate(Integer id, String title, @NotNull String firstName, @NotNull String lastName, String profession, String sex, String hometown, String birthtown, int yearOfBirth) {
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

    @NotNull
    public String getFirstName() {
        return firstName;
    }

    @NotNull
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
