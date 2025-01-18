package com.vacationtracker.mobile_app.ui;

import android.app.DatePickerDialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.vacationtracker.mobile_app.R;
import com.vacationtracker.mobile_app.entities.Excursion;
import com.vacationtracker.mobile_app.entities.Vacation;
import com.vacationtracker.mobile_app.database.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ExcursionDetails extends AppCompatActivity {
    int excursionID;
    EditText editName;
    EditText editPrice;
    EditText editNote;
    TextView editDate;
    Spinner spinner;
    Repository repository;
    DatePickerDialog.OnDateSetListener startDate;
    final Calendar myCalendarStart = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        repository = new Repository(getApplication());
        editName = findViewById(R.id.excursionName);
        editPrice = findViewById(R.id.excursionPrice);
        editNote = findViewById(R.id.note);
        editDate = findViewById(R.id.date);
        spinner = findViewById(R.id.spinner);

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        List<Vacation> vacationList = repository.getAllVacation();
        VacationSpinnerAdapter vacationAdapter = new VacationSpinnerAdapter(this, android.R.layout.simple_spinner_item, vacationList);
        vacationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(vacationAdapter);

        // Retrieve excursionID from the intent
        excursionID = getIntent().getIntExtra("id", -1);
        Log.d("ExcursionDetails", "Excursion ID: " + excursionID); // Debug log

        if (excursionID != -1) {
            List<Excursion> allExcursions = repository.getAllExcursion();
            for (Excursion excursion : allExcursions) {
                if (excursion.getExcursionID() == excursionID) {
                    editName.setText(excursion.getExcursionName());
                    editPrice.setText(Double.toString(excursion.getExcursionPrice()));
                    editNote.setText(excursion.getNote());
                    editDate.setText(excursion.getDate());
                    for (int i = 0; i < vacationList.size(); i++) {
                        if (vacationList.get(i).getVacationID() == excursion.getVacationID()) {
                            spinner.setSelection(i);
                            break;
                        }
                    }
                    break;
                }
            }
        }

        startDate = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendarStart.set(Calendar.YEAR, year);
            myCalendarStart.set(Calendar.MONTH, monthOfYear);
            myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabelStart();
        };

        editDate.setOnClickListener(v -> {
            String info = editDate.getText().toString();
            if (info.isEmpty()) info = "09/01/24";
            try {
                myCalendarStart.setTime(Objects.requireNonNull(sdf.parse(info)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            DatePickerDialog datePickerDialog = new DatePickerDialog(ExcursionDetails.this, startDate, myCalendarStart
                    .get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH),
                    myCalendarStart.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // Set minimum date to today
            datePickerDialog.show();
        });
    }

    private void updateLabelStart() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editDate.setText(sdf.format(myCalendarStart.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursiondetails, menu);

        // Hide "Notify" and "Share" buttons if creating a new excursion
        if (excursionID == -1) {
            MenuItem notifyItem = menu.findItem(R.id.notify);
            MenuItem shareItem = menu.findItem(R.id.share);
            MenuItem deleteItem = menu.findItem(R.id.excursiondelete);
            if (notifyItem != null) notifyItem.setVisible(false);
            if (shareItem != null) shareItem.setVisible(false);
            if (deleteItem != null) deleteItem.setVisible(false);
        } else {
            Log.d("ExcursionDetails", "Excursion ID is valid, showing delete option"); // Debug log
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        if (item.getItemId() == R.id.excursionsave) {
            Excursion excursion;
            String name = editName.getText().toString();
            double price;
            try {
                price = Double.parseDouble(editPrice.getText().toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Retrieve the selected vacationID from the spinner
            Vacation selectedVacation = (Vacation) spinner.getSelectedItem();
            int selectedVacationID = selectedVacation.getVacationID();

            String date = editDate.getText().toString();
            String note = editNote.getText().toString();

            // Validate that the date is not in the past
            try {
                Date selectedDate = new SimpleDateFormat("MM/dd/yy", Locale.US).parse(date);
                if (selectedDate.before(new Date())) {
                    Toast.makeText(this, "Date cannot be in the past", Toast.LENGTH_SHORT).show();
                    return false;
                }

                // Validate that the excursion date is within the vacation date range
                Date vacationStartDate = new SimpleDateFormat("MM/dd/yy", Locale.US).parse(selectedVacation.getStartDate());
                Date vacationEndDate = new SimpleDateFormat("MM/dd/yy", Locale.US).parse(selectedVacation.getEndDate());
                if (selectedDate.before(vacationStartDate) || selectedDate.after(vacationEndDate)) {
                    Toast.makeText(this, "Excursion date must be within the selected vacation dates", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (excursionID == -1) {
                if (repository.getAllExcursion().isEmpty())
                    excursionID = 1;
                else
                    excursionID = repository.getAllExcursion().get(repository.getAllExcursion().size() - 1).getExcursionID() + 1;
                excursion = new Excursion(excursionID, name, price, selectedVacationID, date, note);
                repository.insert(excursion);
            } else {
                excursion = new Excursion(excursionID, name, price, selectedVacationID, date, note);
                repository.update(excursion);
            }

            // Return to VacationList page
            Intent intent = new Intent(ExcursionDetails.this, VacationList.class);
            startActivity(intent);
            finish();
            return true;
        }
        if (item.getItemId() == R.id.share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, editNote.getText().toString());
            sendIntent.putExtra(Intent.EXTRA_TITLE, "Message Title");
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
            return true;
        }
        if (item.getItemId() == R.id.notify) {
            String dateFromScreen = editDate.getText().toString();
            String myFormat = "MM/dd/yy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            Date myDate = null;
            try {
                myDate = sdf.parse(dateFromScreen);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (myDate != null) {
                Date notificationTime = scheduleNotification(myDate, "The Excursion " + editName.getText().toString() + " is today!");

                // Display toast with notification time
                String toastMessage = "Notification scheduled for: " + notificationTime.toString();
                Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
            }
            return true;
        }
        if (item.getItemId() == R.id.excursiondelete) {
            if (excursionID != -1) {
                Excursion currentExcursion = null;
                for (Excursion excursion : repository.getAllExcursion()) {
                    if (excursion.getExcursionID() == excursionID) {
                        currentExcursion = excursion;
                        break;
                    }
                }
                if (currentExcursion != null) {
                    repository.delete(currentExcursion);
                    Toast.makeText(ExcursionDetails.this, "Excursion deleted", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ExcursionDetails.this, VacationList.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ExcursionDetails.this, "Excursion not found", Toast.LENGTH_LONG).show();
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Date scheduleNotification(Date date, String message) {
        Data data = new Data.Builder()
                .putString("message", message)
                .build();
        long delay = date.getTime() - System.currentTimeMillis();
        Log.d("ExcursionDetails", "Scheduling notification: " + message + " with delay: " + delay); // Add log for debugging

        // Calculate the exact target time
        Date targetTime = new Date(System.currentTimeMillis() + delay);
        Log.d("ExcursionDetails", "Notification for \"" + message + "\" will trigger at: " + targetTime.toString());

        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build();
        WorkManager.getInstance(this).enqueue(notificationWork);
        Log.d("ExcursionDetails", "Notification work enqueued");

        // Check the status of the work
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(notificationWork.getId())
                .observe(this, workInfo -> {
                    if (workInfo != null) {
                        Log.d("ExcursionDetails", "Work status: " + workInfo.getState().name());
                    }
                });

        return targetTime;
    }
}
