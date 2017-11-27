package com.my.fluffy.unicorn.main.client.data;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class State implements Serializable{
    private  Integer id;
    private  String name;

    public State() {
    }


    public static State fullCreate(Integer id,  String name) {
        return new State(id, name);
    }


    public static State create( String name) {
        return fullCreate(null, name);
    }

    /**
     * @deprecated
     */
   public State(String name) {
        this(null, name);
    }

    private State(Integer id,  String name) {
        this.id = id;
        this.name = name;
    }

     public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
