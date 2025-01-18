package com.vacationtracker.mobile_app.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.vacationtracker.mobile_app.R;

public class NotificationWorker extends Worker {
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String message = getInputData().getString("message");
        Log.d("NotificationWorker", "doWork: " + message); // Add log for debugging

        // Check if the message is null or empty
        if (message == null || message.isEmpty()) {
            Log.e("NotificationWorker", "Message is null or empty");
            return Result.failure();
        }

        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "vacation_channel")
                .setContentTitle("Vacation Reminder")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
        Log.d("NotificationWorker", "Notification sent");
        return Result.success();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Vacation Channel";
            String description = "Channel for vacation reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("vacation_channel", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d("NotificationWorker", "Notification channel created");
        }
    }
}