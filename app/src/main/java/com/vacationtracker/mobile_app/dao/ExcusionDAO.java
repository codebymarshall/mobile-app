package com.vacationtracker.mobile_app.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import com.vacationtracker.mobile_app.entities.Excursion;

import java.util.List;

@Dao
public interface ExcusionDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void  insert (Excursion excursion);

    @Update
    void update(Excursion excursion);

    @Delete
    void delete (Excursion excursion);

    @Query("SELECT * FROM EXCURSION ORDER BY excursionID ASC")
    List<Excursion> getAllExcursion();

}
