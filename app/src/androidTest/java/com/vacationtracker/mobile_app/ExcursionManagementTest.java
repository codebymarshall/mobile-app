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
import com.vacationtracker.mobile_app.entities.Excursion;

@RunWith(AndroidJUnit4.class)
public class ExcursionManagementTest {
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
    public void testCreateExcursion() {
        Excursion excursion = new Excursion(1, "City Tour", 200.0, 1, "01/02/2024", "Guided city tour");
        repository.insert(excursion);
        assertEquals(1, repository.getAllExcursion().size());
    }

    @Test
    public void testAssociateExcursionWithVacation() {
        // Ensure the list is populated
    Excursion excursion = new Excursion(1, "City Tour", 200.0, 1, "01/02/2024", "Guided city tour");
    repository.insert(excursion);

    // Check that the list is not empty
    assertFalse(repository.getAllExcursion().isEmpty());

    // Now safely access the first element
    Excursion retrievedExcursion = repository.getAllExcursion().get(0);
    assertEquals(1, retrievedExcursion.getVacationID());
    }
}
