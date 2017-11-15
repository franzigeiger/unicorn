package com.my.fluffy.unicorn.main.server.data;

import org.jetbrains.annotations.NotNull;

public class State {
    private final Integer id;
    @NotNull private final String name;

    @NotNull
    public static State fullCreate(Integer id, @NotNull String name) {
        return new State(id, name);
    }

    @NotNull
    public static State create(@NotNull String name) {
        return fullCreate(null, name);
    }

    /**
     * @deprecated
     */
    @Deprecated public State(String name) {
        this(null, name);
    }

    private State(Integer id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    @NotNull public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }
}
