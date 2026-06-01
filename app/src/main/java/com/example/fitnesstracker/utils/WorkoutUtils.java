package com.example.fitnesstracker.utils;
import com.example.fitnesstracker.models.WorkoutPlan;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class WorkoutUtils {

    /**
     * Returns the next upcoming workout from the list based on current date and time.
     */
    public static WorkoutPlan getNextUpcomingWorkout(List<WorkoutPlan> workoutPlans) {
        if (workoutPlans == null || workoutPlans.isEmpty()) return null;

        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy hh:mm a", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());

        Date now = new Date();
        WorkoutPlan nextWorkout = null;
        Date nextDate = null;

        for (WorkoutPlan plan : workoutPlans) {
            if (plan == null || plan.getDate() == null || plan.getDetails() == null) continue;

            try {
                String timePart = extractTimeFromDetails(plan.getDetails());
                if (timePart == null) continue;

                String dateTimeString = plan.getDate().trim() + " " + timePart;
                Date workoutDate = sdf.parse(dateTimeString);

                if (workoutDate != null && workoutDate.after(now)) {
                    if (nextDate == null || workoutDate.before(nextDate)) {
                        nextDate = workoutDate;
                        nextWorkout = plan;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return nextWorkout;
    }

    /**
     * Extracts the time string (e.g., "6:00 PM") from the workout details string.
     * Assumes the time is specified in the format "Time: 6:00 PM"
     */
    private static String extractTimeFromDetails(String details) {
        if (details == null) return null;

        int index = details.indexOf("Time:");
        if (index == -1) return null;

        String timePart = details.substring(index + 5).trim();

        // If there's a "|" after time, take only the time part
        if (timePart.contains("|")) {
            timePart = timePart.split("\\|")[0].trim();
        }

        // Validate time format using regex (optional, but helpful for debugging)
        if (!timePart.matches("\\d{1,2}:\\d{2}\\s?(AM|PM|am|pm)")) {
            return null;
        }

        return timePart;
    }
}
