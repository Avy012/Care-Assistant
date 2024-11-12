package com.example.lighthouseofmemory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Show a toast notification when the alarm goes off
        Toast.makeText(context, "Alarm Triggered!", Toast.LENGTH_SHORT).show();

        // You can add additional actions here, such as displaying a notification
    }
}

