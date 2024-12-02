package com.example.lighthouseofmemory;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MedicineAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("WaterAlarm", "medicine alarm triggered!");
        // Create a notification
        //String channelId = "alarm_channel";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "MedicineChannel",
                    "Alarm Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "MedicineChannel")
                .setContentTitle("약 알람")
                .setContentText("약 드실 시간이에요!")
                .setSmallIcon(R.drawable.baseline_error_24) // Ensure you have an icon in your drawable
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        
        Intent MedIntent = new Intent(context, WatchAlarmReceiver.class); // Define the BroadcastReceiver
        MedIntent.putExtra("action", "meds");
        PendingIntent MedsPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                MedIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        NotificationCompat.Action MedsAction = new NotificationCompat.Action.Builder(
                R.drawable.baseline_medication_24,  // Action icon for Wear OS
                "약 복용 완료",                   // Action label
                MedsPendingIntent          // PendingIntent triggered by this action
        ).build();

        // Add Wearable Extender for customizations
        notificationBuilder.extend(new NotificationCompat.WearableExtender()
                .addAction(MedsAction) // Add the action
                .setContentIntentAvailableOffline(true)); // Allow the notification to be seen offline on Wear OS

        // Ensure notifications permission is granted
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return; // Permission not granted
        }
        
        notificationManager.notify(1001, notificationBuilder.build());

        SharedPreferences sharedPreferences = context.getSharedPreferences("TriggeredAlarms", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Create a unique key for each notification using timestamp
        long timestamp = System.currentTimeMillis();
        String key = "Notification_" + timestamp;

        // Save the data as a JSON-like string
        String notificationData = "{"
                + "\"title\":\"약 알람\","
                + "\"timestamp\":" + timestamp
                + "}";

        editor.putString(key, notificationData);
        editor.apply();

    }
}

