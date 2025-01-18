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
import com.vacationtracker.mobile_app.entities.Excursion;

import java.util.List;

public class ExcursionNameAdapter extends RecyclerView.Adapter<ExcursionNameAdapter.ExcursionNameViewHolder> {
    private final LayoutInflater mInflater;
    private List<Excursion> mExcursions; // Cached copy of excursions
    private final Context context;

    public ExcursionNameAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public ExcursionNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.excursion_name_list_item, parent, false);
        return new ExcursionNameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionNameViewHolder holder, int position) {
        if (mExcursions != null) {
            Excursion current = mExcursions.get(position);
            holder.excursionName.setText(current.getExcursionName());
        } else {
            // Covers the case of data not being ready yet.
            holder.excursionName.setText("No Name");
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

    class ExcursionNameViewHolder extends RecyclerView.ViewHolder {
        private final TextView excursionName;

        private ExcursionNameViewHolder(View itemView) {
            super(itemView);
            excursionName = itemView.findViewById(R.id.excursionName);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                final Excursion current = mExcursions.get(position);
                Intent intent = new Intent(context, ExcursionDetails.class);
                intent.putExtra("id", current.getExcursionID());
                intent.putExtra("name", current.getExcursionName());
                context.startActivity(intent);
            });
        }
    }
}
