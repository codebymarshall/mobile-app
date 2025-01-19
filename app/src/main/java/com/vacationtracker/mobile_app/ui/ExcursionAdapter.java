package com.vacationtracker.mobile_app.ui;



import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vacationtracker.mobile_app.entities.Excursion;
import com.vacationtracker.mobile_app.R;

import java.util.List;

/**
 * Project: Bike Arctic Fox
 * Package: android.carolynbicycleshop.arcticfox.UI
 * <p>
 * User: carolyn.sher
 * Date: 12/18/2021
 * Time: 9:15 AM
 * <p>
 * Created with Android Studio
 * To change this template use File | Settings | File Templates.
 */

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {
    private final LayoutInflater mInflater;
    private List<Excursion> mExcursions; // Cached copy of excursions
    private final Context context;

    public ExcursionAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.excursion_detailed_list_item, parent, false);
        return new ExcursionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        if (mExcursions != null) {
            Excursion current = mExcursions.get(position);
            holder.excursionName.setText(current.getExcursionName());
            holder.excursionPrice.setText(String.valueOf(current.getExcursionPrice()));
            holder.excursionNote.setText(current.getNote());
            holder.excursionStartDate.setText(current.getDate());
        } else {
            // Covers the case of data not being ready yet.
            holder.excursionName.setText("No Name");
            holder.excursionPrice.setText("No Price");
            holder.excursionNote.setText("No Note");
            holder.excursionStartDate.setText("No Date");
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
        private final TextView excursionName;
        private final TextView excursionPrice;
        private final TextView excursionNote;
        private final TextView excursionStartDate;

        private ExcursionViewHolder(View itemView) {
            super(itemView);
            excursionName = itemView.findViewById(R.id.excursionName);
            excursionPrice = itemView.findViewById(R.id.excursionPrice);
            excursionNote = itemView.findViewById(R.id.excursionNote);
            excursionStartDate = itemView.findViewById(R.id.excursionDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final Excursion current = mExcursions.get(position);
                    Intent intent = new Intent(context, ExcursionDetails.class);
                    intent.putExtra("id", current.getExcursionID());
                    intent.putExtra("name", current.getExcursionName());
                    intent.putExtra("price", current.getExcursionPrice());
                    intent.putExtra("prodID", current.getVacationID());
                    context.startActivity(intent);
                }
            });
        }
    }
}
