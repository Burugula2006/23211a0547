package com.affordmed.dto;

public class Depot {

    private int id;
    private int mechanicHours;

    public Depot() {
    }

    public Depot(int id, int mechanicHours) {
        this.id = id;
        this.mechanicHours = mechanicHours;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMechanicHours() {
        return mechanicHours;
    }

    public void setMechanicHours(int mechanicHours) {
        this.mechanicHours = mechanicHours;
    }
}