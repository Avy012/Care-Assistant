package com.example.lighthouseofmemory;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Create a notification
        String channelId = "alarm_channel";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Alarm Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("약 알람")
                .setContentText("약 드실 시간이에요!")
                .setSmallIcon(R.drawable.baseline_error_24) // Ensure you have an icon in your drawable
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

//        NotificationCompat.Action wearAction = new NotificationCompat.Action.Builder(
//                R.drawable.baseline_error_24, // Replace with a valid drawable icon for the action
//                "약 알람", // Action title for Wear OS
//                null // Action intent if needed
//        ).build();

        //notificationBuilder.extend(new NotificationCompat.WearableExtender().addAction(wearAction));
        //NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManager.notify(1001, notificationBuilder.build());
    }
}

