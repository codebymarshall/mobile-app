package com.vacationtracker.mobile_app.ui;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.vacationtracker.mobile_app.R;
import com.vacationtracker.mobile_app.database.Repository;
import com.vacationtracker.mobile_app.entities.Excursion;
import com.vacationtracker.mobile_app.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        if (vacationID == -1) {
            fab.setVisibility(View.GONE); // Hide the button if creating a new vacation
        } else {
            fab.setVisibility(View.VISIBLE); // Show the button if editing an existing vacation
        }
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
            startActivity(intent);
        });

        RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);
        repository = new Repository(getApplication());
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion e : repository.getAllExcursion()) {
            if (e.getVacationID() == vacationId) filteredExcursions.add(e);
        }
        excursionAdapter.setExcursions(filteredExcursions);

        // Hide TextViews if there are no excursions
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);
        if (filteredExcursions.isEmpty()) {
            textView2.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
            textView4.setVisibility(View.GONE);
            textView5.setVisibility(View.GONE);
            textView6.setVisibility(View.GONE);
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacationdetails, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem saveItem = menu.findItem(R.id.vacationsave);
        MenuItem deleteItem = menu.findItem(R.id.vacationdelete);
        MenuItem notifyItem = menu.findItem(R.id.vacationnotify);
        MenuItem shareItem = menu.findItem(R.id.vacationshare);

        if (vacationId == -1) {
            // Creating a new vacation
            saveItem.setVisible(true);
            deleteItem.setVisible(false);
            notifyItem.setVisible(false);
            shareItem.setVisible(false);
        } else {
            // Editing an existing vacation
            saveItem.setVisible(true);
            deleteItem.setVisible(true);
            notifyItem.setVisible(true);
            shareItem.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.vacationsave) {
            Vacation vacation;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);

                String startDateStr = editStartDate.getText().toString();
                String endDateStr = editEndDate.getText().toString();
                Date startDate = sdf.parse(startDateStr);
                Date endDate = sdf.parse(endDateStr);

                Log.d("VacationDetails", "Start date: " + startDate + ", End date: " + endDate); // Add log for debugging

                if (endDate.before(startDate)) {
                    Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (startDate.before(new Date())) {
                    Toast.makeText(this, "Start date cannot be in the past", Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (endDate.before(new Date())) {
                    Toast.makeText(this, "End date cannot be in the past", Toast.LENGTH_SHORT).show();
                    return false;
                }

                // Validate excursions' start dates
                List<Excursion> excursions = repository.getAllExcursion();
                for (Excursion excursion : excursions) {
                    if (excursion.getVacationID() == vacationId) {
                        Date excursionDate = sdf.parse(excursion.getDate());
                        if (excursionDate.before(startDate)) {
                            Toast.makeText(this, "Excursion " + excursion.getExcursionName() + " starts before the vacation start date", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if (excursionDate.after(endDate)) {
                            Toast.makeText(this, "Excursion " + excursion.getExcursionName() + " starts after the vacation end date", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                }

                if (vacationId == -1) {
                    // Create new vacation
                    if (repository.getAllVacation().isEmpty()) vacationId = 1;
                    else vacationId = repository.getAllVacation().get(repository.getAllVacation().size() - 1).getVacationID() + 1;
                    vacation = new Vacation(vacationId, editName.getText().toString(), Double.parseDouble(editPrice.getText().toString()), editHotel.getText().toString(), startDateStr, endDateStr);
                    repository.insert(vacation);
                    Log.d("VacationDetails", "Inserted new vacation with ID: " + vacationId);
                } else {
                    // Update existing vacation
                    vacation = new Vacation(vacationId, editName.getText().toString(), Double.parseDouble(editPrice.getText().toString()), editHotel.getText().toString(), startDateStr, endDateStr);
                    repository.update(vacation);
                    Log.d("VacationDetails", "Updated vacation with ID: " + vacationId);
                }

                // Return to VacationList page
                Intent intent = new Intent(VacationDetails.this, VacationList.class);
                startActivity(intent);
                finish();
            } catch (ParseException e) {
                Log.e("VacationDetails", "Error parsing dates", e);
                Toast.makeText(this, "Error parsing dates", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("VacationDetails", "Error saving vacation", e);
                Toast.makeText(this, "Error saving vacation", Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getItemId()==android.R.id.home){
            this.finish();
            return true;
        }
        else if (item.getItemId() == R.id.vacationdelete) {
            Vacation currentVacation = null;
            for (Vacation prod : repository.getAllVacation()) {
                if (prod.getVacationID() == vacationId) currentVacation = prod;
            }
            numexcursion = 0;
            for (Excursion excursion : repository.getAllExcursion()) {
                if (excursion.getVacationID() == vacationId) ++numexcursion;
            }
            if (numexcursion == 0) {
                repository.delete(currentVacation);
                Toast.makeText(VacationDetails.this, "Vacation deleted", Toast.LENGTH_LONG).show();
                this.finish();
            } else {
                Toast.makeText(VacationDetails.this, "Vacation cannot be deleted with excursions", Toast.LENGTH_LONG).show();
            }
        } else if (item.getItemId() == R.id.vacationnotify) {
            // Schedule notifications for start and end dates
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
                Date startDate = sdf.parse(editStartDate.getText().toString());
                Date endDate = sdf.parse(editEndDate.getText().toString());
                Date startNotificationTime = scheduleNotification(startDate, editName.getText().toString() + " starts today!");
                Date endNotificationTime = scheduleNotification(endDate, editName.getText().toString() + " ends today!");

                // Display toast with notification times
                String toastMessage = "Notifications scheduled:\n" +
                        "Start: " + startNotificationTime.toString() + "\n" +
                        "End: " + endNotificationTime.toString();
                Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
            } catch (ParseException e) {
                Log.e("VacationDetails", "Error parsing dates", e);
                Toast.makeText(this, "Error parsing dates", Toast.LENGTH_SHORT).show();
            }
        } else if (item.getItemId() == R.id.vacationshare) {
            shareVacationDetails();
            return true;
        }
        return true;
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

    private void shareVacationDetails() {
        Vacation currentVacation = null;
        for (Vacation vacation : repository.getAllVacation()) {
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

            List<Excursion> excursions = repository.getAllExcursion();
            if (!excursions.isEmpty()) {
                shareContent.append("\nExcursions:\n");
                for (Excursion excursion : excursions) {
                    if (excursion.getVacationID() == vacationId) {
                        shareContent.append("Excursion Name: ").append(excursion.getExcursionName()).append("\n");
                        shareContent.append("Price: ").append(excursion.getExcursionPrice()).append("\n");
                        shareContent.append("Date: ").append(excursion.getDate()).append("\n");
                        shareContent.append("Note: ").append(excursion.getNote()).append("\n\n");
                    }
                }
            }

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareContent.toString());
            sendIntent.putExtra(Intent.EXTRA_TITLE, "Vacation Details");
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        } else {
            Toast.makeText(this, "No vacation details available to share", Toast.LENGTH_SHORT).show();
        }
    }
}