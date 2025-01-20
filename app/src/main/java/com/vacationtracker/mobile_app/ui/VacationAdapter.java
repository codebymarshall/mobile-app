package com.vacationtracker.mobile_app.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vacationtracker.mobile_app.R;
import com.vacationtracker.mobile_app.entities.Vacation;

import java.util.List;
import java.util.ArrayList;

import android.util.Log;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {
    private List<Vacation> mVacations = new ArrayList<>();  // Initialize with empty list
    private final Context context;

    public VacationAdapter(Context context) {
        this.context = context;
    }

    static class VacationViewHolder extends RecyclerView.ViewHolder {
        private final TextView vacationItemView;

        private VacationViewHolder(View itemView) {
            super(itemView);
            vacationItemView = itemView.findViewById(R.id.viewDataText);
        }
    }

    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vacation_list_item, parent, false);
        return new VacationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        if (mVacations != null && position < mVacations.size()) {
            Vacation current = mVacations.get(position);
            holder.vacationItemView.setText(current.getVacationName());
            
            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, VacationDetails.class);
                intent.putExtra("id", current.getVacationID());
                intent.putExtra("name", current.getVacationName());
                intent.putExtra("price", current.getVacationPrice());
                intent.putExtra("hotel", current.getHotel());
                intent.putExtra("startDate", current.getStartDate());
                intent.putExtra("endDate", current.getEndDate());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mVacations != null ? mVacations.size() : 0;
    }

    public void setVacations(List<Vacation> vacations) {
        this.mVacations = vacations != null ? vacations : new ArrayList<>();
        notifyDataSetChanged();
    }
}

