package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesstracker.adapters.WorkoutActionListener;
import com.example.fitnesstracker.adapters.WorkoutPlanAdapter;
import com.example.fitnesstracker.models.DateHeader;
import com.example.fitnesstracker.WorkoutItem;
import com.example.fitnesstracker.models.WorkoutPlan;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<WorkoutItem> groupedItems;
    private static final int REQUEST_CODE_ADD_WORKOUT = 1;
    private ActivityResultLauncher<Intent> addWorkoutLauncher;
    private WorkoutPlanAdapter adapter;




    private List<WorkoutItem> groupWorkoutsByDate(List<WorkoutPlan> plans) {
        List<WorkoutItem> groupedList = new ArrayList<>();
        Map<String, List<WorkoutPlan>> map = new LinkedHashMap<>();

        for (WorkoutPlan plan : plans) {
            if (!map.containsKey(plan.getDate())) {
                map.put(plan.getDate(), new ArrayList<>());
            }
            map.get(plan.getDate()).add(plan);
        }

        for (String date : map.keySet()) {
            groupedList.add(new DateHeader(date));
            groupedList.addAll(map.get(date));
        }

        return groupedList;
    }
    private List<WorkoutItem> insertGroupedWorkout(WorkoutPlan plan) {
        List<WorkoutItem> newList = new ArrayList<>();

        // Check if a header for this date already exists
        boolean headerExists = false;
        for (WorkoutItem item : groupedItems) {
            if (item instanceof DateHeader && ((DateHeader) item).getDate().equals(plan.getDate())) {
                headerExists = true;
                break;
            }
        }

        if (!headerExists) {
            newList.add(new DateHeader(plan.getDate()));
        }

        newList.add(plan);
        return newList;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addWorkoutLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String name = data.getStringExtra("name");
                            String details = data.getStringExtra("details");
                            String date = data.getStringExtra("date");

                            WorkoutPlan newPlan = new WorkoutPlan(name, details, date);

                            int editPosition = data.getIntExtra("editPosition", -1);
                            if (editPosition != -1) {
                                // ✅ Update existing workout
                                groupedItems.set(editPosition, newPlan);
                            } else {
                                // ✅ Add new workout
                                groupedItems.addAll(insertGroupedWorkout(newPlan));
                            }

                            adapter.notifyDataSetChanged();
                        }
                    }
                }
        );




        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        ((TextView) findViewById(R.id.welcome)).setText("Welcome " + username);

        recyclerView = findViewById(R.id.exerciseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<WorkoutPlan> workoutPlans = new ArrayList<>();
        workoutPlans.add(new WorkoutPlan("Push-ups", "Sets: 3 | Reps: 15 | Time: 6:00 PM", "May 20, 2025"));


        groupedItems = groupWorkoutsByDate(workoutPlans);

        adapter = new WorkoutPlanAdapter(groupedItems, new WorkoutActionListener() {
            @Override
            public void onEdit(WorkoutPlan plan, int position) {
                Intent editIntent = new Intent(HomeActivity.this, AddWorkoutActivity.class);
                editIntent.putExtra("isEditMode", true);
                editIntent.putExtra("name", plan.getName());
                editIntent.putExtra("details", plan.getDetails());
                editIntent.putExtra("date", plan.getDate());
                editIntent.putExtra("editPosition", position);
                addWorkoutLauncher.launch(editIntent);
            }

            @Override
            public void onDelete(WorkoutPlan plan, int position) {
                groupedItems.remove(position);

                // Optional: Remove header if no workouts remain for that date
                String date = plan.getDate();
                boolean stillHasSameDate = false;
                for (WorkoutItem item : groupedItems) {
                    if (item instanceof WorkoutPlan && ((WorkoutPlan) item).getDate().equals(date)) {
                        stillHasSameDate = true;
                        break;
                    }
                }
                if (!stillHasSameDate) {
                    // Remove header
                    groupedItems.removeIf(item -> item instanceof DateHeader && ((DateHeader) item).getDate().equals(date));
                }

                adapter.notifyDataSetChanged();
            }
        });

        recyclerView.setAdapter(adapter);


        recyclerView.setAdapter(adapter);

    }
    public void addSchedule(View v) {

        Intent intent = new Intent(HomeActivity.this, AddWorkoutActivity.class);
        addWorkoutLauncher.launch(intent);
    }

}
