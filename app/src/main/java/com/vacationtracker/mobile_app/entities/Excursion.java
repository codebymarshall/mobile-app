package com.vacationtracker.mobile_app.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "excursion")
public class Excursion {
    @PrimaryKey(autoGenerate = true)
    private int excursionID;
    private String excursionName;
    private double excursionPrice;
    private int vacationID;
    private String date;
    private String note;

    public Excursion(int excursionID, String excursionName, double excursionPrice, int vacationID, String date, String note) {
        this.excursionID = excursionID;
        this.excursionName = excursionName;
        this.excursionPrice = excursionPrice;
        this.vacationID = vacationID;
        this.date = date;
        this.note = note;
    }

    public int getExcursionID() {
        return excursionID;
    }

    public void setExcursionID(int excursionID) {
        this.excursionID = excursionID;
    }

    public String getExcursionName() {
        return excursionName;
    }

    public void setExcursionName(String excursionName) {
        this.excursionName = excursionName;
    }

    public double getExcursionPrice() {
        return excursionPrice;
    }

    public void setExcursionPrice(double excursionPrice) {
        this.excursionPrice = excursionPrice;
    }

    public int getVacationID() {
        return vacationID;
    }

    public void setVacationID(int vacationID) {
        this.vacationID = vacationID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
