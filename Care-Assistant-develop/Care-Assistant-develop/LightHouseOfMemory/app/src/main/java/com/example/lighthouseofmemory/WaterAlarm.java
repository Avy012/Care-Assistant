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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class WaterAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("WaterAlarm", "Water alarm triggered!");
        // Get the water amount from the intent
        int waterAmount = intent.getIntExtra("waterAmount", -1);


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


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "WaterChannel")
                .setSmallIcon(R.drawable.baseline_water_drop_24)
                .setContentTitle("물 알림")
                .setContentText(waterAmount + " ml 의 물을 마실 시간이에요!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);


        NotificationManagerCompat notificationManagercomp = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Intent drinkIntent = new Intent(context, WatchAlarmReceiver.class); // Define the BroadcastReceiver
        drinkIntent.putExtra("action", "drink");
        PendingIntent drinkPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                drinkIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        //워치


        NotificationCompat.Action drinkAction = new NotificationCompat.Action.Builder(
                R.drawable.baseline_water_drop_24,  // Action icon for Wear OS
                "물 마시기 완료",                   // Action label
                drinkPendingIntent             // PendingIntent triggered by this action
        ).build();

        // 워치 커스텀
        builder.extend(new NotificationCompat.WearableExtender()
                .addAction(drinkAction)
                .setContentIntentAvailableOffline(true));


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }



        int notificationId = (int) (System.currentTimeMillis() / 1000); // Example: Seconds since epoch
        notificationManagercomp.notify(notificationId, builder.build());

        Alarm newAlarm = new Alarm("물 알람", System.currentTimeMillis(), waterAmount);

        // 데이터 저장
        SharedPreferences sharedPreferences = context.getSharedPreferences("TriggeredAlarms", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("alarm_list", "[]");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Alarm>>() {}.getType();
        ArrayList<Alarm> alarmLogs = gson.fromJson(json, listType);

        // 알람 추가
        alarmLogs.add(newAlarm);

        // 저장
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String updatedJson = gson.toJson(alarmLogs);
        editor.putString("alarm_list", updatedJson);
        editor.apply();

        Log.d("WaterAlarm", "Alarm saved: " + newAlarm);


    }
}
