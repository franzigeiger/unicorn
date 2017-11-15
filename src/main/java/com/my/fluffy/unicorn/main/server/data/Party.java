package com.my.fluffy.unicorn.main.server.data;

import org.jetbrains.annotations.NotNull;

public class Party {
    private final Integer id;
    @NotNull private final String name;

    @NotNull
    public static Party fullCreate(Integer id, @NotNull String name) {
        return new Party(id, name);
    }

    @NotNull
    public static Party create(@NotNull String name) {
        return fullCreate(null, name);
    }

    /**
     * @deprecated
     */
    @Deprecated public Party(String name) {
        this(null, name);
    }

    private Party(Integer id, @NotNull String name) {
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
