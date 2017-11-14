package com.my.fluffy.unicorn.main.server.data;

public class Party {
    private final int id;
    private final String name;

    public Party(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
