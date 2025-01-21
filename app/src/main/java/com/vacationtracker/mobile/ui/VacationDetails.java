package com.vacationtracker.mobile.ui;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.vacationtracker.mobile.R;
import com.vacationtracker.mobile.database.Repository;
import com.vacationtracker.mobile.entities.Excursion;
import com.vacationtracker.mobile.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VacationDetails extends AppCompatActivity {

    String name;
    double price;
    String hotel;
    int vacationId;
    EditText editName;
    EditText editPrice;
    EditText editHotel;
    TextView editStartDate;
    TextView editEndDate;
    int numexcursion;
    Repository repository;
    DatePickerDialog.OnDateSetListener startDate;
    DatePickerDialog.OnDateSetListener endDate;
    final Calendar myCalendarStart = Calendar.getInstance();
    final Calendar myCalendarEnd = Calendar.getInstance();
    TextView textView2, textView3, textView4, textView5, textView6;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

        // Create notification channel
        createNotificationChannel();

        // Retrieve vacationID from the intent
        int vacationID = getIntent().getIntExtra("id", -1);
        Log.d("VacationDetails", "vacationID: " + vacationID);

        // Use vacationID as needed
        // For vacationtracker, set it to a member variable if needed
        this.vacationId = vacationID;

        EdgeToEdge.enable(this);
        editName = findViewById(R.id.vacationname);
        editPrice = findViewById(R.id.vacationprice);
        editHotel = findViewById(R.id.hotel);
        editStartDate = findViewById(R.id.startDate);
        editEndDate = findViewById(R.id.endDate);
        name = getIntent().getStringExtra("name");
        price = getIntent().getDoubleExtra("price", 0.0);
        hotel = getIntent().getStringExtra("hotel");
        String startDateStr = getIntent().getStringExtra("startDate");
        String endDateStr = getIntent().getStringExtra("endDate");

        editName.setText(name);
        editPrice.setText(Double.toString(price));
        editHotel.setText(hotel);
        editStartDate.setText(startDateStr);
        editEndDate.setText(endDateStr);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the excursion card
        View excursionCard = findViewById(R.id.excursionCard);

        RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);
        repository = new Repository(getApplication());
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Hide TextViews initially
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);

        // Observe excursions for this vacation
        repository.getAllExcursion().observe(this, excursions -> {
            List<Excursion> filteredExcursions = new ArrayList<>();
            for (Excursion e : excursions) {
                if (e.getVacationID() == vacationId) filteredExcursions.add(e);
            }
            excursionAdapter.setExcursions(filteredExcursions);

            // Update TextViews visibility
            boolean hasExcursions = !filteredExcursions.isEmpty();
            textView2.setVisibility(hasExcursions ? View.VISIBLE : View.GONE);
            textView3.setVisibility(hasExcursions ? View.VISIBLE : View.GONE);
            textView4.setVisibility(hasExcursions ? View.VISIBLE : View.GONE);
            textView5.setVisibility(hasExcursions ? View.VISIBLE : View.GONE);
            textView6.setVisibility(hasExcursions ? View.VISIBLE : View.GONE);
        });

        startDate = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendarStart.set(Calendar.YEAR, year);
            myCalendarStart.set(Calendar.MONTH, monthOfYear);
            myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabelStart();
        };

        endDate = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendarEnd.set(Calendar.YEAR, year);
            myCalendarEnd.set(Calendar.MONTH, monthOfYear);
            myCalendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabelEnd();
        };

        editStartDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(VacationDetails.this, startDate, myCalendarStart
                    .get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH),
                    myCalendarStart.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // Set minimum date to today
            datePickerDialog.show();
        });

        editEndDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(VacationDetails.this, endDate, myCalendarEnd
                    .get(Calendar.YEAR), myCalendarEnd.get(Calendar.MONTH),
                    myCalendarEnd.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // Set minimum date to today
            datePickerDialog.show();
        });

        findViewById(R.id.menuButton).setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            View bottomSheetView = getLayoutInflater().inflate(R.layout.vacation_actions_bottom_sheet, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            // Hide buttons if creating new vacation
            if (vacationId == -1) {
                bottomSheetView.findViewById(R.id.deleteButton).setVisibility(View.GONE);
                bottomSheetView.findViewById(R.id.notifyButton).setVisibility(View.GONE);
                bottomSheetView.findViewById(R.id.shareButton).setVisibility(View.GONE);
            }

            bottomSheetView.findViewById(R.id.saveButton).setOnClickListener(view -> {
                String vacationName = editName.getText().toString();
                String startDate = editStartDate.getText().toString();
                String endDate = editEndDate.getText().toString();
                double price = Double.parseDouble(editPrice.getText().toString());
                String hotel = editHotel.getText().toString();
                
                if (vacationName.trim().isEmpty()) {
                    Toast.makeText(this, "Please enter a vacation name", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
                    Date parsedStartDate = sdf.parse(startDate);
                    Date parsedEndDate = sdf.parse(endDate);
                    
                    if (parsedStartDate != null && parsedEndDate != null) {
                        if (parsedStartDate.after(parsedEndDate)) {
                            Toast.makeText(this, "Start date must be before end date", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        if (vacationId == -1) {
                            Vacation vacation = new Vacation(0, vacationName, price, hotel, startDate, endDate);
                            repository.insert(vacation);
                        } else {
                            Vacation vacation = new Vacation(vacationId, vacationName, price, hotel, startDate, endDate);
                            repository.update(vacation);
                        }
                        
                        Intent intent = new Intent(VacationDetails.this, VacationList.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (ParseException e) {
                    Toast.makeText(this, "Error parsing dates", Toast.LENGTH_SHORT).show();
                }
                bottomSheetDialog.dismiss();
            });

            bottomSheetView.findViewById(R.id.shareButton).setOnClickListener(view -> {
                repository.getAllExcursion().observe(this, excursions -> {
                    StringBuilder shareContent = new StringBuilder();
                    shareContent.append("Vacation Details\n\n");
                    shareContent.append("Name: ").append(editName.getText().toString()).append("\n");
                    shareContent.append("Hotel: ").append(editHotel.getText().toString()).append("\n");
                    shareContent.append("Price: $").append(editPrice.getText().toString()).append("\n");
                    shareContent.append("From: ").append(editStartDate.getText().toString()).append("\n");
                    shareContent.append("To: ").append(editEndDate.getText().toString()).append("\n\n");

                    // Add excursions if any exist for this vacation
                    List<Excursion> vacationExcursions = new ArrayList<>();
                    for (Excursion excursion : excursions) {
                        if (excursion.getVacationID() == vacationId) {
                            vacationExcursions.add(excursion);
                        }
                    }

                    if (!vacationExcursions.isEmpty()) {
                        shareContent.append("Excursions:\n");
                        for (Excursion excursion : vacationExcursions) {
                            shareContent.append("\n- ").append(excursion.getExcursionName()).append("\n");
                            shareContent.append("  Date: ").append(excursion.getDate()).append("\n");
                            shareContent.append("  Price: $").append(excursion.getExcursionPrice()).append("\n");
                            if (excursion.getNote() != null && !excursion.getNote().isEmpty()) {
                                shareContent.append("  Note: ").append(excursion.getNote()).append("\n");
                            }
                        }
                    }

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, shareContent.toString());
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, "Share Vacation Details"));
                    bottomSheetDialog.dismiss();
                });
            });

            bottomSheetView.findViewById(R.id.notifyButton).setOnClickListener(view -> {
                String myFormat = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                try {
                    Date startDate = sdf.parse(editStartDate.getText().toString());
                    if (startDate != null) {
                        Date notificationTime = scheduleNotification(startDate, 
                            "The Vacation " + editName.getText().toString() + " starts today!");
                        String toastMessage = "Notification scheduled for: " + notificationTime.toString();
                        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                bottomSheetDialog.dismiss();
            });

            bottomSheetView.findViewById(R.id.deleteButton).setOnClickListener(view -> {
                if (vacationId != -1) {
                    repository.getAllExcursion().observe(this, excursions -> {
                        // Check if there are any excursions associated with this vacation
                        boolean hasAssociatedExcursions = false;
                        for (Excursion excursion : excursions) {
                            if (excursion.getVacationID() == vacationId) {
                                hasAssociatedExcursions = true;
                                break;
                            }
                        }

                        if (hasAssociatedExcursions) {
                            Toast.makeText(this, 
                                "Cannot delete vacation with associated excursions.", 
                                Toast.LENGTH_LONG).show();
                        } else {
                            repository.getAllVacation().observe(this, vacations -> {
                                for (Vacation vacation : vacations) {
                                    if (vacation.getVacationID() == vacationId) {
                                        repository.delete(vacation);
                                        Toast.makeText(this, "Vacation deleted", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(this, VacationList.class);
                                        startActivity(intent);
                                        finish();
                                        break;
                                    }
                                }
                            });
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
        editStartDate.setText(sdf.format(myCalendarStart.getTime()));
    }

    private void updateLabelEnd() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editEndDate.setText(sdf.format(myCalendarEnd.getTime()));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("vacation_channel", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public Date scheduleNotification(Date date, String message) {
        Data data = new Data.Builder()
                .putString("message", message)
                .build();
        long delay = date.getTime() - System.currentTimeMillis();
        Log.d("VacationDetails", "Scheduling notification: " + message + " with delay: " + delay); // Add log for debugging

        // Calculate the exact target time
        Date targetTime = new Date(System.currentTimeMillis() + delay);
        Log.d("VacationDetails", "Notification for \"" + message + "\" will trigger at: " + targetTime.toString());

        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build();
        WorkManager.getInstance(this).enqueue(notificationWork);
        Log.d("VacationDetails", "Notification work enqueued");

        // Check the status of the work
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(notificationWork.getId())
                .observe(this, workInfo -> {
                    if (workInfo != null) {
                        Log.d("VacationDetails", "Work status: " + workInfo.getState().name());
                    }
                });

        return targetTime;
    }

    private void shareVacationDetails() {
        repository.getAllVacation().observe(this, vacations -> {
            Vacation currentVacation = null;
            for (Vacation vacation : vacations) {
                if (vacation.getVacationID() == vacationId) {
                    currentVacation = vacation;
                    break;
                }
            }

            if (currentVacation != null) {
                StringBuilder shareContent = new StringBuilder();
                shareContent.append("Vacation Details\n");
                shareContent.append("Name: ").append(currentVacation.getVacationName()).append("\n");
                shareContent.append("Price: ").append(currentVacation.getVacationPrice()).append("\n");
                shareContent.append("Hotel: ").append(currentVacation.getHotel()).append("\n");
                shareContent.append("Start Date: ").append(currentVacation.getStartDate()).append("\n");
                shareContent.append("End Date: ").append(currentVacation.getEndDate()).append("\n");

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent.toString());
                startActivity(Intent.createChooser(shareIntent, "Share Vacation Details"));
            }
        });
    }

    private void generateNewVacationId() {
        repository.getAllVacation().observe(this, vacations -> {
            if (vacations == null || vacations.isEmpty()) {
                vacationId = 1;
            } else {
                vacationId = vacations.get(vacations.size() - 1).getVacationID() + 1;
            }
        });
    }
}