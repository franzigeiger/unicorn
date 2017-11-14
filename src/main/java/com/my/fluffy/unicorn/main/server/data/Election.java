package com.my.fluffy.unicorn.main.server.data;

import java.time.LocalDate;

public class Election {
    private final int year;
    private final LocalDate date;

    public Election(LocalDate date) {
        this.year = date.getYear();
        this.date = date;
    }

    public int getYear() {
        return year;
    }

    public LocalDate getDate() {
        return date;
    }
}
