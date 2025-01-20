package com.vacationtracker.mobile.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.vacationtracker.mobile.dao.ExcusionDAO;
import com.vacationtracker.mobile.dao.VacationDAO;
import com.vacationtracker.mobile.entities.Excursion;
import com.vacationtracker.mobile.entities.Vacation;

@Database(entities = {Vacation.class, Excursion.class}, version= 14, exportSchema = false)

public abstract class DatabaseBuilder extends RoomDatabase {

    public abstract VacationDAO vacationDAO();

    public abstract ExcusionDAO excusionDAO();

    private static volatile DatabaseBuilder INSTANCE;

    static DatabaseBuilder getDatabase(final Context context){
        if (INSTANCE==null){
            synchronized (DatabaseBuilder.class){
                if(INSTANCE == null){
                    INSTANCE= Room.databaseBuilder(context.getApplicationContext(),DatabaseBuilder.class, "MyDatabase.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static DatabaseBuilder getInMemoryDatabase(Context context) {
        return Room.inMemoryDatabaseBuilder(context.getApplicationContext(), DatabaseBuilder.class)
                .allowMainThreadQueries() // For testing purposes only
                .build();
    }

}
