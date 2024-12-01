package com.example.lighthouseofmemory;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class WaterAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("WaterAlarm", "Water alarm triggered!");
        // Get the water amount from the intent
        int waterAmount = intent.getIntExtra("waterAmount", -1);

        // Fallback in case waterAmount is not provided
        if (waterAmount <= 0) {
            waterAmount = 250; // Default to 250 ml
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "WaterChannel",
                    "Alarm Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "WaterChannel")
                .setSmallIcon(R.drawable.baseline_water_drop_24)
                .setContentTitle("물 알림")
                .setContentText(waterAmount + " ml 의 물을 마실 시간이에요!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Ensure notifications permission is granted
        NotificationManagerCompat notificationManagercomp = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return; // Permission not granted, don't show the notification
        }

        // Use a unique ID for notifications

        int notificationId = (int) (System.currentTimeMillis() / 1000); // Example: Seconds since epoch
        notificationManagercomp.notify(notificationId, builder.build());
    }
}
