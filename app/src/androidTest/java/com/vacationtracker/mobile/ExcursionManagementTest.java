package com.vacationtracker.mobile;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import android.content.Context;

import com.vacationtracker.mobile.database.DatabaseBuilder;
import com.vacationtracker.mobile.database.Repository;
import com.vacationtracker.mobile.entities.Excursion;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;

import java.util.List;

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
        
        // Use TestObserver to get the value synchronously
        TestObserver<List<Excursion>> observer = new TestObserver<>();
        repository.getAllExcursion().observeForever(observer);
        assertEquals(1, observer.getValue().size());
    }

    @Test
    public void testAssociateExcursionWithVacation() {
        Excursion excursion = new Excursion(1, "City Tour", 200.0, 1, "01/02/2024", "Guided city tour");
        repository.insert(excursion);

        TestObserver<List<Excursion>> observer = new TestObserver<>();
        repository.getAllExcursion().observeForever(observer);
        
        List<Excursion> excursions = observer.getValue();
        assertFalse(excursions.isEmpty());
        assertEquals(1, excursions.get(0).getVacationID());
    }

    // Helper class for testing LiveData
    private static class TestObserver<T> implements Observer<T> {
        private T value;

        @Override
        public void onChanged(T t) {
            value = t;
        }

        public T getValue() {
            return value;
        }
    }
}
