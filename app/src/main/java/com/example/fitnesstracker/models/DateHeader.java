package com.example.fitnesstracker.models;

import com.example.fitnesstracker.WorkoutItem;


public class DateHeader implements WorkoutItem {
    private String date;

    public DateHeader(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    @Override
    public int getType() {
        return WorkoutItem.TYPE_DATE;
    }
}


