package com.my.fluffy.unicorn.main.client.data;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Date;


public class Election implements Serializable{
    private  int year;
     private Date date;

    public Election() {
    }

    public static Election minCreate(int year) {
        return new Election(year, null);
    }

    public static Election create( Date date) {
        return new Election(date.getYear(), date);
    }

     public Election(int year, Date date) {
        this.year = year;
        this.date = date;
    }

    public int getYear() {
        return year;
    }

    public Date getDate() {
        return date;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
