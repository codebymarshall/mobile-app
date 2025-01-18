package com.vacationtracker.mobile_app;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import android.content.Context;

import com.vacationtracker.mobile_app.database.DatabaseBuilder;
import com.vacationtracker.mobile_app.database.Repository;
import com.vacationtracker.mobile_app.entities.Vacation;

@RunWith(AndroidJUnit4.class)
public class VacationManagementTest {
    private DatabaseBuilder db;
    private Repository repository;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, DatabaseBuilder.class)
                .allowMainThreadQueries() // For testing purposes only
                .build();
        repository = new Repository(db);
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void testCreateVacation() {
        Vacation vacation = new Vacation(1, "Beach Holiday", 1000.0, "Seaside Hotel", "01/01/2024", "01/07/2024");
        repository.insert(vacation);
        assertEquals(1, repository.getAllVacation().size());
    }

    @Test
    public void testUpdateVacation() {
        Vacation vacation = new Vacation(1, "Beach Holiday", 1000.0, "Seaside Hotel", "01/01/2024", "01/07/2024");
        repository.insert(vacation);

        vacation.setVacationName("Mountain Retreat");
        repository.update(vacation);
        assertEquals("Mountain Retreat", repository.getAllVacation().get(0).getVacationName());
    }

    @Test
    public void testDeleteVacation() {
        Vacation vacation = new Vacation(1, "Beach Holiday", 1000.0, "Seaside Hotel", "01/01/2024", "01/07/2024");
        repository.insert(vacation);

        repository.delete(vacation);
        assertEquals(0, repository.getAllVacation().size());
    }
}
