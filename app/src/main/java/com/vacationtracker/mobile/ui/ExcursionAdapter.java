package com.vacationtracker.mobile.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vacationtracker.mobile.entities.Excursion;
import com.vacationtracker.mobile.R;

import java.util.List;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {
    private final LayoutInflater mInflater;
    private List<Excursion> mExcursions;
    private final Context context;

    public ExcursionAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.excursion_list_item, parent, false);
        return new ExcursionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        if (mExcursions != null) {
            Excursion current = mExcursions.get(position);
            holder.excursionNameView.setText(current.getExcursionName());
            
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ExcursionDetails.class);
                intent.putExtra("id", current.getExcursionID());
                intent.putExtra("name", current.getExcursionName());
                intent.putExtra("price", current.getExcursionPrice());
                intent.putExtra("note", current.getNote());
                intent.putExtra("date", current.getDate());
                intent.putExtra("vacationID", current.getVacationID());
                context.startActivity(intent);
            });
        }
    }

    public void setExcursions(List<Excursion> excursions) {
        mExcursions = excursions;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mExcursions != null)
            return mExcursions.size();
        else return 0;
    }

    class ExcursionViewHolder extends RecyclerView.ViewHolder {
        private final TextView excursionNameView;

        private ExcursionViewHolder(View itemView) {
            super(itemView);
            excursionNameView = itemView.findViewById(R.id.excursionName);
        }
    }
}
