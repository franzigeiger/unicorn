package com.my.fluffy.unicorn.main.client.data;



import java.io.Serializable;

public class Party implements Serializable {
    private  Integer id;
    private  String name;

    public Party() {
    }


    public static Party fullCreate(Integer id, String name) {
        return new Party(id, name);
    }


    public static Party create(String name) {
        return fullCreate(null, name);
    }

   public Party(String name) {
        this(null, name);
    }

    private Party(Integer id, String name) {
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
