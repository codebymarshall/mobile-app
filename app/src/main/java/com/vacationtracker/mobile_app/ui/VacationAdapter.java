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

import android.util.Log;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {


    class VacationViewHolder extends RecyclerView.ViewHolder{
        private final TextView vacationItemView;
        private VacationViewHolder(View itemView){
            super(itemView);
            vacationItemView=itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(view -> {
                int position=getAdapterPosition();
                final Vacation current=mVacations.get(position);
                Intent intent=new Intent(context,VacationDetails.class);
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
    private List<Vacation> mVacations;
    private final Context context;
    private final LayoutInflater mInflater;

    public VacationAdapter(Context context){
        mInflater=LayoutInflater.from(context);
        this.context=context;
    }
    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView=mInflater.inflate(R.layout.vacation_list_item,parent,false);
        return new VacationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        if (mVacations != null) {
            Vacation current = mVacations.get(position);
            String name = current.getVacationName();
            holder.vacationItemView.setText(name);
            Log.d("VacationAdapter", "Binding vacation: " + name);
        } else {
            holder.vacationItemView.setText("No vacation name");
            Log.d("VacationAdapter", "No vacation data available");
        }
    }

    public void setVacations(List<Vacation> vacations){
        mVacations=vacations;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mVacations.size();
    }
}
