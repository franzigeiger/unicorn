package com.my.fluffy.unicorn.main.client.data;

import java.io.Serializable;

public class State implements Serializable{
    private  Integer id;
    private  String name;
    private Integer eligibleVoters2017;

    public State() {
    }


    public static State fullCreate(Integer id,  String name, Integer eligibleVoters2017) {
        return new State(id, name, eligibleVoters2017);
    }


    public static State create( String name, Integer eligibleVoters2017) {
        return fullCreate(null, name, eligibleVoters2017);
    }

    public static State minCreate(String name) {
        return fullCreate(null, name, null);
    }

    /**
     * @deprecated
     */
   public State(String name) {
        this(null, name, null);
    }

    private State(Integer id,  String name, Integer eligibleVoters2017) {
        this.id = id;
        this.name = name;
        this.eligibleVoters2017 = eligibleVoters2017;
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

    public int getEligibleVoters2017() {
        return eligibleVoters2017;
    }
}
