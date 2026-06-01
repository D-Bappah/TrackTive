package com.example.fitnesstracker.models;

import com.example.fitnesstracker.WorkoutItem;

public class WorkoutPlan implements WorkoutItem {
    private String name;
    private String details;
    private String date;

    public WorkoutPlan(String name, String details, String date) {
        this.name = name;
        this.details = details;
        this.date = date;
    }

    public String getName() { return name; }
    public String getDetails() { return details; }
    public String getDate() { return date; }

    @Override
    public int getType() {
        return WorkoutItem.TYPE_WORKOUT;
    }
}
