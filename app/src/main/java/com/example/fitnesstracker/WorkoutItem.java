package com.example.fitnesstracker;


public interface WorkoutItem {
    int TYPE_DATE = 0;
    int TYPE_WORKOUT = 1;

    int getType();
}
