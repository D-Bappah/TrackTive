package com.example.fitnesstracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fitnesstracker.models.WorkoutReminderReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddWorkoutActivity extends AppCompatActivity {

    private EditText editWorkoutName, editSets, editReps;
    private Spinner spinnerHour, spinnerMinute, spinnerDay, spinnerMonth, spinnerYear;
    private Button btnSaveWorkout;
    private Spinner spinnerAmPm;
    private int getSpinnerIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);
        int editPosition = getIntent().getIntExtra("editPosition", -1); // -1 means not editing

        editWorkoutName = findViewById(R.id.editWorkoutName);
        editSets = findViewById(R.id.editSets);
        editReps = findViewById(R.id.editReps);
        spinnerHour = findViewById(R.id.spinnerHour);
        spinnerMinute = findViewById(R.id.spinnerMinute);
        spinnerDay = findViewById(R.id.spinnerDay);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);
        btnSaveWorkout = findViewById(R.id.btnSaveWorkout);
        spinnerAmPm = findViewById(R.id.spinnerAmPm);

        setupSpinners();
        boolean isEditMode = getIntent().getBooleanExtra("isEditMode", false);
        if (isEditMode) {
            String name = getIntent().getStringExtra("name");
            String details = getIntent().getStringExtra("details");
            String date = getIntent().getStringExtra("date");

            editWorkoutName.setText(name);
            // FIX: Replace 'detailsEditText' with 'editSets' and 'editReps' appropriately if needed
            // Or, add a new EditText for details if it's a separate field.

            // Parse the date: "May 20, 2025"
            String[] parts = date.split(" ");
            if (parts.length >= 3) {
                String month = parts[0];
                String day = parts[1].replace(",", "");
                String year = parts[2];

                spinnerMonth.setSelection(getSpinnerIndex(spinnerMonth, month));
                spinnerDay.setSelection(Integer.parseInt(day) - 1);
                spinnerYear.setSelection(getSpinnerIndex(spinnerYear, year));
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        1001);  // Use a constant for requestCode
            }
            btnSaveWorkout.setOnClickListener(v -> {
                // ... listener code ...
            });
        }  // Correctly ends onCreate here

        btnSaveWorkout.setOnClickListener(v -> {
            String name = editWorkoutName.getText().toString().trim();
            String sets = editSets.getText().toString().trim();
            String reps = editReps.getText().toString().trim();

            if (name.isEmpty() || sets.isEmpty() || reps.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String hourS = spinnerHour.getSelectedItem().toString();
            String minuteS = spinnerMinute.getSelectedItem().toString();
            String amPmS = spinnerAmPm.getSelectedItem().toString();
            String timeS = hourS + ":" + minuteS + " " + amPmS;


            String dayS = spinnerDay.getSelectedItem().toString();
            String monthS = spinnerMonth.getSelectedItem().toString();
            String yearS = spinnerYear.getSelectedItem().toString();
            String date = monthS + " " + dayS + ", " + yearS;

            String details = "Sets: " + sets + " | Reps: " + reps + " | Time: " + timeS;

            // 1. Get data from spinners
            int hour = Integer.parseInt(spinnerHour.getSelectedItem().toString());
            int minute = Integer.parseInt(spinnerMinute.getSelectedItem().toString());
            String amPm = spinnerAmPm.getSelectedItem().toString();

            if (amPm.equals("PM") && hour < 12) hour += 12;
            if (amPm.equals("AM") && hour == 12) hour = 0;

            int day = Integer.parseInt(spinnerDay.getSelectedItem().toString());
            int month = spinnerMonth.getSelectedItemPosition(); // 0-indexed for Calendar
            int year = Integer.parseInt(spinnerYear.getSelectedItem().toString());

// 2. Create calendar
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute, 0);

// 3. Schedule the alarm
            scheduleWorkoutReminder(name, calendar);

// 4. Return result
            Intent resultIntent = new Intent();
            resultIntent.putExtra("name", name);
            resultIntent.putExtra("details", details);
            resultIntent.putExtra("date", date);

            if (isEditMode) {
                resultIntent.putExtra("editPosition", editPosition);
            }

            setResult(RESULT_OK, resultIntent);
            finish();



        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scheduleWorkoutReminder(String workoutName, Calendar calendar) {
        Intent intent = new Intent(this, WorkoutReminderReceiver.class);
        intent.putExtra("workout_name", workoutName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent2 = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent2);
            }
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void setupSpinners() {
        // Hour: 1–12
        List<String> hours = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            hours.add(String.valueOf(i));
        }
        ArrayAdapter<String> hourAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hours);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHour.setAdapter(hourAdapter);

        // Minute: 0–59
        List<String> minutes = new ArrayList<>();
        for (int i = 0; i <= 59; i++) {
            minutes.add(String.format("%02d", i));
        }
        ArrayAdapter<String> minuteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, minutes);
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMinute.setAdapter(minuteAdapter);

        // Day: 1–31
        List<String> days = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            days.add(String.valueOf(i));
        }
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);

        // Month: Jan–Dec
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        // Year: current year to next 10 years
        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 10; i++) {
            years.add(String.valueOf(currentYear + i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        ArrayAdapter<String> amPmAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"AM", "PM"});
        amPmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAmPm.setAdapter(amPmAdapter);

    }
}
