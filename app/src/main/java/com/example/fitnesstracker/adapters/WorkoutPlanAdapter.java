package com.example.fitnesstracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesstracker.R;
import com.example.fitnesstracker.WorkoutItem;
import com.example.fitnesstracker.models.DateHeader;
import com.example.fitnesstracker.models.WorkoutPlan;

import java.util.List;

public class WorkoutPlanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<WorkoutItem> items;
    private WorkoutActionListener listener;

    public WorkoutPlanAdapter(List<WorkoutItem> items, WorkoutActionListener listener) {
        this.items = items;
        this.listener = listener;

    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == WorkoutItem.TYPE_DATE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_header, parent, false);
            return new DateViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
            return new WorkoutViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        WorkoutItem item = items.get(position);
        if (holder instanceof DateViewHolder) {
            ((DateViewHolder) holder).dateText.setText(((DateHeader) item).getDate());
        } else {
            WorkoutPlan plan = (WorkoutPlan) item;
            WorkoutViewHolder vh = (WorkoutViewHolder) holder;
            vh.name.setText(plan.getName());
            vh.details.setText(plan.getDetails());

            vh.btnEdit.setOnClickListener(v -> listener.onEdit(plan, holder.getAdapterPosition()));
            vh.btnDelete.setOnClickListener(v -> listener.onDelete(plan, holder.getAdapterPosition()));



        }
    }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;

        public DateViewHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateHeader);
        }
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView name, details;
        Button btnEdit, btnDelete;

        public WorkoutViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.exerciseName);
            details = itemView.findViewById(R.id.exerciseDetails);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public void updateData(List<WorkoutItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

}
