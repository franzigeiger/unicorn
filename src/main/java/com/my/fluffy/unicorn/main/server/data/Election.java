package com.my.fluffy.unicorn.main.server.data;

import java.time.LocalDate;

public class Election {
    private final int year;
    private final LocalDate date;

    public Election(int year, LocalDate date) {
        this.year = year;
        this.date = date;
    }

    public int getYear() {
        return year;
    }

    public LocalDate getDate() {
        return date;
    }
}
