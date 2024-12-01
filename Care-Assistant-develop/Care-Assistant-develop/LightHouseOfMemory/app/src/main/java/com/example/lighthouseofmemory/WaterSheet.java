package com.example.lighthouseofmemory;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Locale;

public class WaterSheet extends BottomSheetDialog {

    private Context context;
    private AlarmManager alarmManager;
    private SharedPreferences sharedPreferences;
    private int[] waterAmount = {1000, 250};
    int hour, minute;
    private TextView wakeTime, sleepTime;

    public WaterSheet(Context context, SharedPreferences sharedPreferences) {
        super(context);
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    public void showDialog() {
        View waterBottomSheetView = LayoutInflater.from(context).inflate(R.layout.water_bottom_sheet, null);
        setContentView(waterBottomSheetView);

        TextView waterAmountTextView = waterBottomSheetView.findViewById(R.id.waterAmountTextView);
        TextView once_waterAmountTextView = waterBottomSheetView.findViewById(R.id.once_water);
        wakeTime = waterBottomSheetView.findViewById(R.id.wake_time);
        sleepTime = waterBottomSheetView.findViewById(R.id.sleep_time);
        Button saveB = waterBottomSheetView.findViewById(R.id.saveButton);
        Button deleteB = waterBottomSheetView.findViewById(R.id.deleteButton);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // 데이터 불러옴
        loadPreferences(waterAmountTextView, once_waterAmountTextView, wakeTime, sleepTime);


        setupButtons(waterBottomSheetView, waterAmountTextView, once_waterAmountTextView, wakeTime, sleepTime);
        //setupSwitch(on_off, waterAmountTextView, once_waterAmountTextView, wakeTime, sleepTime);

        saveB.setOnClickListener(v -> {
            savePreferences(waterAmountTextView, once_waterAmountTextView, wakeTime, sleepTime);
            startAlarm();
            dismiss();
        });

        deleteB.setOnClickListener(v -> {
            deletePreferences();
            stopAlarm();
            dismiss();
        });



        show();
    }

    private void loadPreferences(TextView waterAmountTextView, TextView once_waterAmountTextView,
                                 TextView wakeTime, TextView sleepTime) {
        String savedWaterAmount = sharedPreferences.getString("waterAmount", "1000ml");
        String savedOnce = sharedPreferences.getString("once_waterAmount", "250ml");
        String wake = sharedPreferences.getString("wakeTime", "-");
        String sleep = sharedPreferences.getString("sleepTime", "-");
        //boolean savedSwitchState = sharedPreferences.getBoolean("isSwitchOn", false);


        waterAmountTextView.setText(savedWaterAmount);
        once_waterAmountTextView.setText(savedOnce);
        wakeTime.setText(wake);
        sleepTime.setText(sleep);
        //on_off.setChecked(savedSwitchState);
        updateUI(waterAmountTextView, once_waterAmountTextView, wakeTime, sleepTime);
    }

    private void setupButtons(View waterBottomSheetView, TextView waterAmountTextView, TextView once_waterAmountTextView,
                              TextView wakeTime, TextView sleepTime) {
        Button increaseWaterButton = waterBottomSheetView.findViewById(R.id.button1);
        Button decreaseWaterButton = waterBottomSheetView.findViewById(R.id.button2);
        Button onceIncreaseButton = waterBottomSheetView.findViewById(R.id.button3);
        Button onceDecreaseButton = waterBottomSheetView.findViewById(R.id.button4);
        Button wakeB = waterBottomSheetView.findViewById(R.id.wake_button);
        Button sleepB = waterBottomSheetView.findViewById(R.id.sleep_button);
        Button deleteB = waterBottomSheetView.findViewById(R.id.deleteButton);

        //물 증가, 감소
        increaseWaterButton.setOnClickListener(v -> {
            waterAmount[0] += 50;
            waterAmountTextView.setText(waterAmount[0] + " ml");
            waterAmountTextView.setVisibility(View.VISIBLE);
        });

        decreaseWaterButton.setOnClickListener(v -> {
            if (waterAmount[0] >= 50) {
                waterAmount[0] -= 50;
                waterAmountTextView.setText(waterAmount[0] + " ml");
            }
            waterAmountTextView.setVisibility(View.VISIBLE);
        });


        onceIncreaseButton.setOnClickListener(v -> {
            waterAmount[1] += 50;
            once_waterAmountTextView.setText(waterAmount[1] + " ml");
            once_waterAmountTextView.setVisibility(View.VISIBLE);
        });

        onceDecreaseButton.setOnClickListener(v -> {
            if (waterAmount[1] >= 50) {
                waterAmount[1] -= 50;
                once_waterAmountTextView.setText(waterAmount[1] + " ml");
            }
            once_waterAmountTextView.setVisibility(View.VISIBLE);
        });

        // 기상,취침시간
        setupTimePicker(wakeB, wakeTime, true);
        setupTimePicker(sleepB, sleepTime, false);

        deleteB.setOnClickListener(v -> {
            deletePreferences();
            updateUI(waterAmountTextView, once_waterAmountTextView, wakeTime, sleepTime);
            stopAlarm();
        });
    }

    private void setupSwitch(Switch on_off, TextView waterAmountTextView, TextView once_waterAmountTextView,
                             TextView wakeTime, TextView sleepTime) {
        on_off.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int visibility = isChecked ? View.VISIBLE : View.GONE;

            waterAmountTextView.setVisibility(visibility);
            once_waterAmountTextView.setVisibility(visibility);
            wakeTime.setVisibility(visibility);
            sleepTime.setVisibility(visibility);

            //체크되면 알람 저장
            if (isChecked) {
                startAlarm();
            } else {
                stopAlarm();
            }
        });
    }

    private void setupTimePicker(Button button, TextView timeTextView, boolean isWakeTime) {
        button.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view, hourOfDay, minute1) -> {
                String period = hourOfDay < 12 ? "오전" : "오후";
                int hour12 = hourOfDay % 12;
                if (hour12 == 0) hour12 = 12;
                String formattedTime = String.format(Locale.getDefault(), "%s %02d:%02d", period, hour12, minute1);
                timeTextView.setText(formattedTime);
            }, hour, minute, true);
            timePickerDialog.show();
            wakeTime.setVisibility(View.VISIBLE);
            sleepTime.setVisibility(View.VISIBLE);
        });
    }


    //알람 정보 로컬에 저장
    private void savePreferences(TextView waterAmountTextView, TextView once_waterAmountTextView,
                                 TextView wakeTime, TextView sleepTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("waterAmount", waterAmountTextView.getText().toString());
        editor.putString("once_waterAmount", once_waterAmountTextView.getText().toString());
        editor.putString("wakeTime", wakeTime.getText().toString());
        editor.putString("sleepTime", sleepTime.getText().toString());
        //editor.putBoolean("isSwitchOn", on_off.isChecked());
        editor.apply();

        Toast.makeText(context, "물 알람이 저장되었어요!", Toast.LENGTH_SHORT).show();

    }

    private void deletePreferences(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("waterAmount");
        editor.remove("once_waterAmount");
        editor.remove("wakeTime");
        editor.remove("sleepTime");
        editor.remove("isSwitchOn"); // Remove switch state if applicable
        editor.apply();

        Toast.makeText(context, "알람이 삭제되었습니다.", Toast.LENGTH_SHORT).show();

    }

    private void updateUI(TextView waterAmountTextView, TextView once_waterAmountTextView,
                          TextView wakeTime, TextView sleepTime) {
        // Check if data exists in SharedPreferences
        boolean hasData = sharedPreferences.contains("waterAmount") ||
                sharedPreferences.contains("once_waterAmount") ||
                sharedPreferences.contains("wakeTime") ||
                sharedPreferences.contains("sleepTime");

        // Set visibility of TextViews based on data presence
        int visibility = hasData ? View.VISIBLE : View.GONE;
        waterAmountTextView.setVisibility(visibility);
        once_waterAmountTextView.setVisibility(visibility);
        wakeTime.setVisibility(visibility);
        sleepTime.setVisibility(visibility);

        // Reset TextViews if no data
        if (!hasData) {
            waterAmountTextView.setText("");
            once_waterAmountTextView.setText("");
            wakeTime.setText("");
            sleepTime.setText("");
        }
    }


    private void startAlarm() {
        // Retrieve saved wake time from SharedPreferences
        String wakeTime = sharedPreferences.getString("wakeTime", null);
        if (wakeTime == null || wakeTime.equals("-")) {
            Toast.makeText(context, "알람 시간이 설정되지 않았습니다.", Toast.LENGTH_SHORT).show();
            return; // Exit if no time is set
        }

        // Parse the saved time (assuming format is "오전 HH:mm" or "오후 HH:mm")
        String[] parts = wakeTime.split(" ");
        String period = parts[0]; // "오전" or "오후"
        String[] timeParts = parts[1].split(":");
        int savedHour = Integer.parseInt(timeParts[0]);
        int savedMinute = Integer.parseInt(timeParts[1]);

        // Convert to 24-hour format
        if (period.equals("오후") && savedHour != 12) {
            savedHour += 12; // Convert PM hours
        } else if (period.equals("오전") && savedHour == 12) {
            savedHour = 0; // Handle midnight (12 AM)
        }

        // Set the calendar with the retrieved time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, savedHour);
        calendar.set(Calendar.MINUTE, savedMinute);
        calendar.set(Calendar.SECOND, 0);

        // Adjust for past time
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Set for the next day
        }

        // Create an intent for the alarm
        Intent intent = new Intent(getContext(), WaterAlarm.class);
        intent.putExtra("waterAmount", waterAmount[1]); // Add water amount as extra

        // Create a pending intent
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                getContext(),
                0, // Use a unique request code if needed
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        // Get the AlarmManager service and set the alarm
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        alarmPendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        alarmPendingIntent
                );
            }
            Log.d("AlarmSetup", "Alarm set for: " + calendar.getTime());
        }

    }


    private void stopAlarm() {
        // Create the same PendingIntent used when setting the alarm
        Intent intent = new Intent(getContext(), WaterAlarm.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                getContext(),
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Get the AlarmManager system service
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            // Cancel the scheduled alarm
            alarmManager.cancel(alarmPendingIntent);
        }
    }
}
