package com.vacationtracker.mobile_app;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.vacationtracker.mobile_app.ui.VacationDetails;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@RunWith(AndroidJUnit4.class)
public class NotificationManagementTest {
    private ActivityScenario<VacationDetails> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(VacationDetails.class);
    }

    @Test
    public void testScheduleVacationNotification() {
        scenario.onActivity(activity -> {
            try {
                Date startDate = new SimpleDateFormat("MM/dd/yy", Locale.US).parse("01/01/2024");
                Date notificationTime = activity.scheduleNotification(startDate, "Vacation starts today!");
                assertNotNull(notificationTime);
            } catch (ParseException e) {
                fail("ParseException was thrown: " + e.getMessage());
            }
        });
    }

    @Test
    public void testScheduleExcursionNotification() {
        scenario.onActivity(activity -> {
            try {
                Date excursionDate = new SimpleDateFormat("MM/dd/yy", Locale.US).parse("01/02/2024");
                Date notificationTime = activity.scheduleNotification(excursionDate, "Excursion today!");
                assertNotNull(notificationTime);
            } catch (ParseException e) {
                fail("ParseException was thrown: " + e.getMessage());
            }
        });
    }
}
