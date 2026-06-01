package com.example.fitnesstracker.adapters;

import com.example.fitnesstracker.models.WorkoutPlan;

public interface WorkoutActionListener {
    void onEdit(WorkoutPlan plan, int position);
    void onDelete(WorkoutPlan plan, int position);
}




