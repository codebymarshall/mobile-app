package com.vacationtracker.mobile.ui;

import android.app.DatePickerDialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.vacationtracker.mobile.R;
import com.vacationtracker.mobile.entities.Excursion;
import com.vacationtracker.mobile.entities.Vacation;
import com.vacationtracker.mobile.database.Repository;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.ViewCompat;


public class ExcursionDetails extends AppCompatActivity {
    int excursionID;
    EditText editName;
    EditText editPrice;
    EditText editNote;
    TextView editDate;
    MaterialAutoCompleteTextView vacationSpinner;
    Repository repository;
    DatePickerDialog.OnDateSetListener startDate;
    final Calendar myCalendarStart = Calendar.getInstance();
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);
        EdgeToEdge.enable(this);

        // Initialize views
        repository = new Repository(getApplication());
        editName = findViewById(R.id.excursionName);
        editPrice = findViewById(R.id.excursionPrice);
        editNote = findViewById(R.id.note);
        editDate = findViewById(R.id.date);
        vacationSpinner = findViewById(R.id.vacationSpinner);

        // Set up edge-to-edge content
        View mainContent = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(mainContent, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, insets.top, 0, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        // Observe vacation list changes
        repository.getAllVacation().observe(this, vacationList -> {
            VacationSpinnerAdapter vacationAdapter = new VacationSpinnerAdapter(this, android.R.layout.simple_spinner_item, vacationList);
            vacationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            vacationSpinner.setAdapter(vacationAdapter);

            // Set selected vacation if editing existing excursion
            if (excursionID != -1) {
                repository.getAllExcursion().observe(this, allExcursions -> {
                    for (Excursion excursion : allExcursions) {
                        if (excursion.getExcursionID() == excursionID) {
                            editName.setText(excursion.getExcursionName());
                            editPrice.setText(Double.toString(excursion.getExcursionPrice()));
                            editNote.setText(excursion.getNote());
                            editDate.setText(excursion.getDate());
                            for (int i = 0; i < vacationList.size(); i++) {
                                if (vacationList.get(i).getVacationID() == excursion.getVacationID()) {
                                    vacationSpinner.setText(vacationList.get(i).getVacationName());
                                    break;
                                }
                            }
                            break;
                        }
                    }
                });
            }
        });

        // Retrieve excursionID from the intent
        excursionID = getIntent().getIntExtra("id", -1);
        Log.d("ExcursionDetails", "Excursion ID: " + excursionID); // Debug log

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

        findViewById(R.id.menuButton).setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            View bottomSheetView = getLayoutInflater().inflate(R.layout.excursion_actions_bottom_sheet, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            // Hide buttons if creating new excursion
            if (excursionID == -1) {
                bottomSheetView.findViewById(R.id.deleteButton).setVisibility(View.GONE);
                bottomSheetView.findViewById(R.id.notifyButton).setVisibility(View.GONE);
                bottomSheetView.findViewById(R.id.shareButton).setVisibility(View.GONE);
            }

            bottomSheetView.findViewById(R.id.saveButton).setOnClickListener(view -> {
                if (editName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "Please enter an excursion name", Toast.LENGTH_LONG).show();
                    return;
                }
                saveExcursion();
                bottomSheetDialog.dismiss();
            });

            bottomSheetView.findViewById(R.id.shareButton).setOnClickListener(view -> {
                String note = editNote.getText().toString();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, note);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, null));
                bottomSheetDialog.dismiss();
            });

            bottomSheetView.findViewById(R.id.notifyButton).setOnClickListener(view -> {
                String dateFromScreen = editDate.getText().toString();
                try {
                    Date myDate = sdf.parse(dateFromScreen);
                    if (myDate != null) {
                        Date notificationTime = scheduleNotification(myDate, 
                            "The Excursion " + editName.getText().toString() + " is today!");
                        String toastMessage = "Notification scheduled for: " + notificationTime.toString();
                        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                bottomSheetDialog.dismiss();
            });

            bottomSheetView.findViewById(R.id.deleteButton).setOnClickListener(view -> {
                if (excursionID != -1) {
                    repository.getAllExcursion().observe(this, excursions -> {
                        for (Excursion excursion : excursions) {
                            if (excursion.getExcursionID() == excursionID) {
                                repository.delete(excursion);
                                Toast.makeText(this, "Excursion deleted", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(this, ExcursionList.class);
                                startActivity(intent);
                                finish();
                                break;
                            }
                        }
                    });
                }
                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.show();
        });
    }

    private void updateLabelStart() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editDate.setText(sdf.format(myCalendarStart.getTime()));
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

    private boolean saveExcursion() {
        if (editName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter an excursion name", Toast.LENGTH_LONG).show();
            return false;
        }

        String name = editName.getText().toString();
        double price = Double.parseDouble(editPrice.getText().toString());
        String selectedVacationText = vacationSpinner.getText().toString();
        String excursionDate = editDate.getText().toString();
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            Date excursionDateParsed = sdf.parse(excursionDate);
            
            repository.getAllVacation().observe(this, vacations -> {
                for (Vacation vacation : vacations) {
                    if (vacation.getVacationName().equals(selectedVacationText)) {
                        try {
                            Date vacationStartDate = sdf.parse(vacation.getStartDate());
                            Date vacationEndDate = sdf.parse(vacation.getEndDate());
                            
                            if (excursionDateParsed.before(vacationStartDate) || excursionDateParsed.after(vacationEndDate)) {
                                Toast.makeText(this, 
                                    "Excursion date must be between (" + 
                                    vacation.getStartDate() + " - " + vacation.getEndDate() + ")", 
                                    Toast.LENGTH_LONG).show();
                                return;
                            }
                            
                            if (excursionID == -1) {
                                // Create new excursion
                                Excursion newExcursion = new Excursion(0, name, price, 
                                    vacation.getVacationID(), excursionDate, 
                                    editNote.getText().toString());
                                repository.insert(newExcursion);
                            } else {
                                // Update existing excursion
                                Excursion existingExcursion = new Excursion(excursionID, name, price, 
                                    vacation.getVacationID(), excursionDate, 
                                    editNote.getText().toString());
                                repository.update(existingExcursion);
                            }
                            
                            Intent intent = new Intent(ExcursionDetails.this, ExcursionList.class);
                            startActivity(intent);
                            finish();
                            break;
                        } catch (ParseException e) {
                            Toast.makeText(this, "Error validating dates", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid excursion date format", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
