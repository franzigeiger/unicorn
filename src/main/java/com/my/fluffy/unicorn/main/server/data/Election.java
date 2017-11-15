package com.my.fluffy.unicorn.main.server.data;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public class Election {
    private final int year;
    @NotNull private final LocalDate date;

    @NotNull
    public static Election create(@NotNull LocalDate date) {
        return new Election(date);
    }

    /**
     * @deprecated
     */
    @Deprecated public Election(@NotNull LocalDate date) {
        this.year = date.getYear();
        this.date = date;
    }

    public int getYear() {
        return year;
    }

    @NotNull public LocalDate getDate() {
        return date;
    }
}
