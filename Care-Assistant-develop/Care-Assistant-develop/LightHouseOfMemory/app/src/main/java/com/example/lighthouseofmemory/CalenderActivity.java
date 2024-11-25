package com.example.lighthouseofmemory;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class CalenderActivity extends AppCompatActivity {


    private Button medicineB;
    private Button waterB;
    private ImageButton backButton;
    BottomNavigationView bottomNavigationView;
    private AlarmManager alarmManager;
    private PendingIntent alarmPendingIntent;

    View bottomSheetView;

    // 약 알람 목록
    ArrayList<String> items = new ArrayList<>();
    //ArrayList<String> subitems = new ArrayList<>();
    ArrayAdapter<String> adapter;

    // 물 양
    int[] waterAmount = {1000,250};


    // 알람 설정 형태로 변환
    private int convertDayToCalendar(String day) {
        switch (day) {
            case "월": return Calendar.MONDAY;
            case "화": return Calendar.TUESDAY;
            case "수": return Calendar.WEDNESDAY;
            case "목": return Calendar.THURSDAY;
            case "금": return Calendar.FRIDAY;
            case "토": return Calendar.SATURDAY;
            case "일": return Calendar.SUNDAY;
            default: return Calendar.SUNDAY;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
                // Permission granted, you can now set exact alarms
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }



    //알람 저장
    private void setAlarm(int hour, int minute, boolean isAM, String day) {
        // Convert to 24-hour format
        int alarmHour = isAM ? hour : hour + 12;

        // Logic to schedule alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class); // Create intent for your receiver
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                day.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );


        // Set the alarm
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmHour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Set for the next day if time is in the past
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12 (API level 31) or above
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE); // Get AlarmManager instance
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivityForResult(intent, 1); // Request permission
            }
        }
        long triggerTime = calendar.getTimeInMillis(); // Get the trigger time in milliseconds

        // Check if the time is in the past, adjust if necessary
        if (triggerTime < System.currentTimeMillis()) {
            // If the alarm time is in the past, set it for the next day
            calendar.add(Calendar.DATE, 1);
            triggerTime = calendar.getTimeInMillis();
        }
        long windowLength = 60 * 1000L; // 1 minute (60 seconds)
        long triggerTimeWindowEnd = triggerTime + windowLength; // End of the window
        //alarmManager.setWindow(AlarmManager.RTC_WAKEUP, triggerTime, windowLength, pendingIntent);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

//        if (alarmManager != null) {
//            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            alarmManager.setWindow(AlarmManager.RTC_WAKEUP, triggerTime, windowLength, pendingIntent);
//
//        }



    }

    private void saveAlarmsToPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("Alarms", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(items);
        editor.putString("alarm_list", json);
        editor.apply();
    }

    private void cancelAlarm(int position) {
        // Generate a unique ID for the alarm based on its position or other identifier
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                position,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );



        if (pendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
            pendingIntent.cancel();
        }
    }



    private void showTimePickerBottomSheet(int preHour, int preMinute, boolean preIsAM, ArrayList<String> preSelectedDays, Integer editPosition) {

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        //adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_2,subitems);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.time_picker_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        Button amButton = bottomSheetView.findViewById(R.id.buttonAM);
        Button pmButton = bottomSheetView.findViewById(R.id.buttonPM);

        // 요일
        Button mondayButton = bottomSheetView.findViewById(R.id.buttonMonday);
        Button tuesdayButton = bottomSheetView.findViewById(R.id.buttonTuesday);
        Button wedButton = bottomSheetView.findViewById(R.id.buttonWednesday);
        Button thuButton = bottomSheetView.findViewById(R.id.buttonThursday);
        Button friButton = bottomSheetView.findViewById(R.id.buttonFriday);
        Button satButton = bottomSheetView.findViewById(R.id.buttonSaturday);
        Button sunButton = bottomSheetView.findViewById(R.id.buttonSunday);

        // 시
        Button hr1 = bottomSheetView.findViewById(R.id.button1);
        Button hr2 = bottomSheetView.findViewById(R.id.button2);
        Button hr3 = bottomSheetView.findViewById(R.id.button3);
        Button hr4 = bottomSheetView.findViewById(R.id.button4);
        Button hr5 = bottomSheetView.findViewById(R.id.button5);
        Button hr6 = bottomSheetView.findViewById(R.id.button6);
        Button hr7 = bottomSheetView.findViewById(R.id.button7);
        Button hr8 = bottomSheetView.findViewById(R.id.button8);
        Button hr9 = bottomSheetView.findViewById(R.id.button9);
        Button hr10 = bottomSheetView.findViewById(R.id.button10);
        Button hr11 = bottomSheetView.findViewById(R.id.button11);
        Button hr12 = bottomSheetView.findViewById(R.id.button12);

        //분
        Button min00 = bottomSheetView.findViewById(R.id.button00);
        Button min05 = bottomSheetView.findViewById(R.id.button05);
        Button min10 = bottomSheetView.findViewById(R.id.mbutton10);
        Button min15 = bottomSheetView.findViewById(R.id.button15);
        Button min20 = bottomSheetView.findViewById(R.id.button20);
        Button min25 = bottomSheetView.findViewById(R.id.button25);
        Button min30 = bottomSheetView.findViewById(R.id.button30);
        Button min35 = bottomSheetView.findViewById(R.id.button35);
        Button min40 = bottomSheetView.findViewById(R.id.button40);
        Button min45 = bottomSheetView.findViewById(R.id.button45);
        Button min50 = bottomSheetView.findViewById(R.id.button50);
        Button min55 = bottomSheetView.findViewById(R.id.button55);



        // 저장,삭제
        Button saveButton = bottomSheetView.findViewById(R.id.buttonSave);
        Button deleteButton = bottomSheetView.findViewById(R.id.buttonDelete);

        final int[] selectedHour = {1};
        final int[] selectedMinute = {0};
        final boolean[] isAM = {true};
        final ArrayList<String> selectedDays = new ArrayList<>();

        selectedHour[0] = preHour;
        selectedMinute[0] = preMinute;
        isAM[0] = preIsAM;
        selectedDays.clear();
        selectedDays.addAll(preSelectedDays);

        // 오전오후 클릭
        amButton.setOnClickListener(v -> {
            isAM[0] = true;
            amButton.setEnabled(false);
            pmButton.setEnabled(true);
        });

        pmButton.setOnClickListener(v -> {
            isAM[0] = false;
            pmButton.setEnabled(false);
            amButton.setEnabled(true);
        });

        // 요일 클릭 효과
        mondayButton.setOnClickListener(v -> toggleDaySelection("월", mondayButton, selectedDays));
        tuesdayButton.setOnClickListener(v -> toggleDaySelection("화", tuesdayButton, selectedDays));
        wedButton.setOnClickListener(v -> toggleDaySelection("수", wedButton, selectedDays));
        thuButton.setOnClickListener(v -> toggleDaySelection("목", thuButton, selectedDays));
        friButton.setOnClickListener(v -> toggleDaySelection("금", friButton, selectedDays));
        satButton.setOnClickListener(v -> toggleDaySelection("토", satButton, selectedDays));
        sunButton.setOnClickListener(v -> toggleDaySelection("일", sunButton, selectedDays));

        Button[] hourButtons = {
                hr1, hr2, hr3, hr4, hr5, hr6, hr7, hr8, hr9, hr10, hr11, hr12
        };
        Button[] minButtons = {
                min00, min05, min10, min15, min20, min25, min30, min35, min40, min45, min50, min55
        };

        // 시 클릭 효과
        View.OnClickListener hourClickListener = v -> {
            // Reset effects for all buttons
            for (Button button : hourButtons) {
                button.setAlpha(1.0f); // Reset transparency
                button.setBackgroundResource(R.drawable.time_button);
            }

            Button selectedButton = (Button) v;
            selectedButton.setAlpha(0.5f);

            String buttonText = selectedButton.getText().toString();
            selectedHour[0] = Integer.parseInt(buttonText);
        };

        // 분 클릭 효과
        View.OnClickListener minClickListener = v -> {
            // Reset effects for all buttons
            for (Button button : hourButtons) {
                button.setAlpha(1.0f); // Reset transparency
                button.setBackgroundResource(R.drawable.time_button);
            }

            Button selectedButton = (Button) v;
            selectedButton.setAlpha(0.5f);

            String buttonText = selectedButton.getText().toString();
            selectedMinute[0] = Integer.parseInt(buttonText);
        };

        // 시에 효과 적용
        for (Button button: hourButtons){
            button.setOnClickListener(hourClickListener);
        }
        //분에 효과 적용
        for (Button button: minButtons){
            button.setOnClickListener(minClickListener);
        }

        // 저장
        saveButton.setOnClickListener(v -> {
            String time = String.format("%s %02d:%02d",isAM[0] ? "오전" : "오후", selectedHour[0], selectedMinute[0]);
            if (selectedDays.isEmpty()) {
                Toast.makeText(this, "날짜를 선택해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            String alarmDetails = time + ", " + selectedDays;
            //String alarm = time;
            //items.add(alarmDetails);


            Toast.makeText(this, "알람이 저장되었습니다. " , Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
            // After adding a new alarm
            Log.d("CalenderActivity", "Alarm added: " + alarmDetails);
            Log.d("CalenderActivity", "Updated items: " + items);

            //saveAlarmsToPreferences();  // This method already saves alarms to SharedPreferences.


            // Update existing alarm if editing
            if (editPosition != null) {
                items.set(editPosition, alarmDetails);
                cancelAlarm(editPosition); // Cancel previous alarm in the system
            } else {
                items.add(alarmDetails); // Add new alarm
            }
            adapter.notifyDataSetChanged();
            saveAlarmsToPreferences();

            // 알람 저장 부분
            for (String day : selectedDays) {
                setAlarm(selectedHour[0], selectedMinute[0], isAM[0], day);
            }


        });

        // 삭제
        // Delete Button Logic
        deleteButton.setOnClickListener(v -> {
            if (editPosition != null) {
                items.remove((int) editPosition);
                adapter.notifyDataSetChanged();

                // Save updated list to SharedPreferences
                saveAlarmsToPreferences();

                cancelAlarm(editPosition); // Cancel the alarm in the system
                Toast.makeText(this, "알람이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    // 클릭 버튼 효과
    private void toggleDaySelection(String day, Button button, ArrayList<String> selectedDays) {
        if (selectedDays.contains(day)) {
            selectedDays.remove(day);
            button.setAlpha(1.0f);
        } else {
            selectedDays.add(day);
            button.setAlpha(0.5f);
        }
    }
    private void showTimePickerBottomSheetForEdit(int position, String selectedAlarm) {
        // Parse the selected alarm
        String[] parts = selectedAlarm.split(", ");
        String time = parts[0];
        String days = parts[1];

        // Extract time and days
        String[] timeParts = time.split(" ");
        boolean isAM = timeParts[0].equals("오전");
        String[] hourMinute = timeParts[1].split(":");
        int hour = Integer.parseInt(hourMinute[0]);
        int minute = Integer.parseInt(hourMinute[1]);

        ArrayList<String> selectedDays = new ArrayList<>();
        if (days != null) {
            String[] dayArray = days.replace("[", "").replace("]", "").split(", ");
            for (String day : dayArray) {
                selectedDays.add(day.trim());
            }
        }

        // Call the time picker with pre-filled values
        showTimePickerBottomSheet(hour, minute, isAM, selectedDays, position);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        // 물 알림
        createNotificationChannel();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        backButton = findViewById(R.id.Back_b);
        medicineB = findViewById(R.id.medicine_button);
        waterB = findViewById(R.id.water_button);

        items = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_gps) {
                    startActivity(new Intent(CalenderActivity.this, GpsActivity.class));
                    return true;
                }
                return false;
            }

        });

        // 뒤로가기 버튼 클릭 이벤트
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티 종료
            }
        });

        // 설정화면으로 이동하는 버튼
        ImageButton Setting_b = findViewById(R.id.Setting_b);
        Setting_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalenderActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });


        // Load saved alarms
        SharedPreferences sharedPreferences = getSharedPreferences("Alarms", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("alarm_list", null);

        Log.d("CalenderActivity", "Saved JSON: " + json); // Debug log

        if (json != null) {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            ArrayList<String> loadedItems = gson.fromJson(json, type);
            items.addAll(loadedItems); // Add loaded data to the existing list
        }

        //알람 리스트 새로고침

        adapter.notifyDataSetChanged();

        // Set adapter to the ListView
        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);

        Button addButton = bottomSheetView.findViewById(R.id.addButton); //// 알람 추가 버튼
        ListView listView = bottomSheetView.findViewById(R.id.listView); //// 리스트뷰
        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        listView.invalidateViews(); // Optionally force refresh


        //약 알림설정 버튼
        medicineB.setOnClickListener(view -> {
            adapter.notifyDataSetChanged();
            bottomSheetDialog.show();
        });

        // 추가 버튼 눌렀을 때
        addButton.setOnClickListener(v -> {
            showTimePickerBottomSheet(1, 0, true, new ArrayList<>(), null);
            bottomSheetDialog.dismiss();
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedAlarm = items.get(position);
            showTimePickerBottomSheetForEdit(position, selectedAlarm);
            bottomSheetDialog.dismiss();
        });

        //물 알림설정 버튼 -------------------------------------------------------------------------------------------------
        waterB.setOnClickListener(view -> {

            View waterBottomSheetView = getLayoutInflater().inflate(R.layout.water_bottom_sheet, null);

            BottomSheetDialog waterBottomSheetDialog = new BottomSheetDialog(this);
            waterBottomSheetDialog.setContentView(waterBottomSheetView);
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Switch on_off = waterBottomSheetView.findViewById(R.id.onoff);
            TextView waterAmountTextView = waterBottomSheetView.findViewById(R.id.waterAmountTextView);
            TextView once_waterAmountTextView = waterBottomSheetView.findViewById(R.id.once_water);
            TextView wakeTime = waterBottomSheetView.findViewById(R.id.wake_time);
            TextView sleepTime = waterBottomSheetView.findViewById(R.id.sleep_time);
            Button saveB = waterBottomSheetView.findViewById(R.id.saveButton);

            // 저장된 데이터 불러옴
            String savedWaterAmount = sharedPreferences.getString("waterAmount", "1000ml");
            String savedonce = sharedPreferences.getString("once_waterAmount", "250ml");
            String wake = sharedPreferences.getString("wakeTime", "-");
            String sleep = sharedPreferences.getString("sleepTime", "-");
            boolean savedSwitchState = sharedPreferences.getBoolean("isSwitchOn", false);

            // 저장된 데이터로 출력
            waterAmountTextView.setText(savedWaterAmount);
            once_waterAmountTextView.setText(savedonce);
            wakeTime.setText(wake);
            sleepTime.setText(sleep);
            on_off.setChecked(savedSwitchState);


            // 목표 물 양 elements
            Button increaseWaterButton = waterBottomSheetView.findViewById(R.id.button1);
            Button decreaseWaterButton = waterBottomSheetView.findViewById(R.id.button2);

            // 목표 물 증가
            increaseWaterButton.setOnClickListener(v -> {
                waterAmount[0] += 50; // 50씩 증가
                waterAmountTextView.setText(waterAmount[0] + " ml"); // Update the TextView
            });

            decreaseWaterButton.setOnClickListener(v -> {
                if (waterAmount[0] >= 50) { // Ensure water amount doesn't go below 0
                    waterAmount[0] -= 50;
                    waterAmountTextView.setText(waterAmount[0] + " ml"); // Update the TextView
                }
            });

            // 회당 물 양 elements
            Button once_increaseWaterButton = waterBottomSheetView.findViewById(R.id.button3);
            Button once_decreaseWaterButton = waterBottomSheetView.findViewById(R.id.button4);

            // 회당 물 증가
            once_increaseWaterButton.setOnClickListener(v -> {
                waterAmount[1] += 50;
                once_waterAmountTextView.setText(waterAmount[1] + " ml"); // Update the TextView
            });

            once_decreaseWaterButton.setOnClickListener(v -> {
                if (waterAmount[1] >= 50) {
                    waterAmount[1] -= 50;
                    once_waterAmountTextView.setText(waterAmount[1] + " ml");
                }
            });

            // 기상, 취침시간
            Button wakeB = waterBottomSheetView.findViewById(R.id.wake_button);
            Button sleepB = waterBottomSheetView.findViewById(R.id.sleep_button);

            View.OnClickListener timePickerListener = v -> {
                // Determine which button was clicked
                boolean isWakeTime = v.getId() == R.id.wake_button;

                // Show a TimePickerDialog
                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);


                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        waterBottomSheetView.getContext(),
                        (timePicker, hourOfDay, minute) -> {

                            String period = hourOfDay < 12 ? "오전" : "오후";

                            int hourIn12 = hourOfDay % 12;
                            if (hourIn12 == 0) hourIn12 = 12;
                            // Format the selected time
                            String selectedTime = String.format(Locale.getDefault(), "%s %02d:%02d", period, hourOfDay, minute);

                            // Update the corresponding TextView
                            if (isWakeTime) {
                                wakeTime.setText(selectedTime);
                            } else {
                                sleepTime.setText(selectedTime);
                            }
                        },
                        currentHour,
                        currentMinute,
                        true // Use 24-hour format
                );


                timePickerDialog.show();
            };

            // 스위치  -> 알람 온/오프
            on_off.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    waterAmountTextView.setVisibility(View.VISIBLE);
                    once_waterAmountTextView.setVisibility(View.VISIBLE);
                    increaseWaterButton.setVisibility(View.VISIBLE);
                    decreaseWaterButton.setVisibility(View.VISIBLE);
                    once_increaseWaterButton.setVisibility(View.VISIBLE);
                    once_decreaseWaterButton.setVisibility(View.VISIBLE);
                    wakeTime.setVisibility(View.VISIBLE);
                    sleepTime.setVisibility(View.VISIBLE);
                    wakeB.setVisibility(View.VISIBLE);
                    sleepB.setVisibility(View.VISIBLE);

                    startAlarm();
                } else {
                    waterAmountTextView.setVisibility(View.GONE);
                    once_waterAmountTextView.setVisibility(View.GONE);
                    increaseWaterButton.setVisibility(View.GONE);
                    decreaseWaterButton.setVisibility(View.GONE);
                    once_increaseWaterButton.setVisibility(View.GONE);
                    once_decreaseWaterButton.setVisibility(View.GONE);
                    wakeTime.setVisibility(View.GONE);
                    sleepTime.setVisibility(View.GONE);
                    wakeB.setVisibility(View.GONE);
                    sleepB.setVisibility(View.GONE);

                    stopAlarm();
                }
            });

            wakeB.setOnClickListener(timePickerListener);
            sleepB.setOnClickListener(timePickerListener);

            // 저장 버튼 -> 앱 내 데이터 저장
            saveB.setOnClickListener(v -> {
                String waterAmount = waterAmountTextView.getText().toString();
                String once_waterAmount = once_waterAmountTextView.getText().toString();
                String wake_time = wakeTime.getText().toString();
                String sleep_time = sleepTime.getText().toString();
                boolean isSwitchOn = on_off.isChecked();

                // Save the values to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("waterAmount", waterAmount);
                editor.putString("once_waterAmount", once_waterAmount);
                editor.putString("wakeTime", wake_time);
                editor.putString("sleepTime", sleep_time);
                editor.putBoolean("isSwitchOn", isSwitchOn);
                editor.apply();

                // Optionally show a toast message
                Toast.makeText(this, "물 알람이 저장되었어요!", Toast.LENGTH_SHORT).show();
            });

            scheduleWaterNotifications(
                    wakeTime.getText().toString(),
                    sleepTime.getText().toString(),
                    waterAmount[0], // e.g., 2000 ml
                    waterAmount[1] // e.g., 250 ml
            );




            // Show the bottom sheet dialog
            waterBottomSheetDialog.show();
        });


    }

    private void scheduleWaterNotifications(String wakeTime, String sleepTime, int dailyGoal, int perSessionAmount) {
        // Parse wake and sleep times
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        try {
            Date wakeUp = timeFormat.parse(wakeTime);
            Date sleep = timeFormat.parse(sleepTime);

            // Calculate total sessions
            int totalSessions = dailyGoal / perSessionAmount;

            // Calculate active hours
            long activeTimeInMillis = sleep.getTime() - wakeUp.getTime();
            if (activeTimeInMillis < 0) {
                // Handle case where sleep time is past midnight
                activeTimeInMillis += 24 * 60 * 60 * 1000;
            }
            long intervalInMillis = activeTimeInMillis / totalSessions;

            // Schedule notifications
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(wakeUp);
            for (int i = 0; i < totalSessions; i++) {
                scheduleNotification(calendar.getTimeInMillis(), perSessionAmount);
                calendar.setTimeInMillis(calendar.getTimeInMillis() + intervalInMillis);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void scheduleNotification(long notificationTime, int waterAmount) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("waterAmount", waterAmount);
        alarmPendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) notificationTime, // Unique request code
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    notificationTime,
                    pendingIntent
            );
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Water Reminder Channel";
            String description = "Channel for water drinking reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("WaterChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void startAlarm() {
        // Example: set the alarm for a specific time (e.g., 8 AM)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8); // Set hour
        calendar.set(Calendar.MINUTE, 0); // Set minute
        calendar.set(Calendar.SECOND, 0); // Set second

        // Create an intent for the alarm
        Intent intent = new Intent(CalenderActivity.this, NotificationReceiver.class);
        intent.putExtra("waterAmount", 250); // Example value for water amount
        alarmPendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Set the alarm to trigger at the specified time
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);
    }

    private void stopAlarm() {
        if (alarmPendingIntent != null) {
            // Cancel the existing alarm
            alarmManager.cancel(alarmPendingIntent);
        }
    }




}