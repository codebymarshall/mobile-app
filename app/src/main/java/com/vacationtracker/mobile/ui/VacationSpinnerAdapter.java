package com.vacationtracker.mobile.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vacationtracker.mobile.entities.Vacation;

import java.util.List;

public class VacationSpinnerAdapter extends ArrayAdapter<Vacation> {
    private final Context context;
    private final List<Vacation> vacations;

    public VacationSpinnerAdapter(Context context, int textViewResourceId, List<Vacation> vacations) {
        super(context, textViewResourceId, vacations);
        this.context = context;
        this.vacations = vacations;
    }

    @Override
    public Vacation getItem(int position) {
        return vacations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return vacations.get(position).getVacationID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false);
        }
        TextView label = (TextView) convertView.findViewById(android.R.id.text1);
        label.setText(vacations.get(position).getVacationName());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }
        TextView label = (TextView) convertView.findViewById(android.R.id.text1);
        label.setText(vacations.get(position).getVacationName());
        return convertView;
    }
}