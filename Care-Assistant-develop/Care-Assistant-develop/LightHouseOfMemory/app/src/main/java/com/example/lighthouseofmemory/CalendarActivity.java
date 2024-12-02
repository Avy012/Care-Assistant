package com.example.lighthouseofmemory;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;


public class CalendarActivity extends AppCompatActivity {

    private HashMap<String, Integer> dateColorMap = new HashMap<>();
    TextView detailTextView;
    TextView dateTextView;
    ImageButton Back_b;
    ImageButton Setting_b;
    ImageButton noti_b;
    Button Edit_b,Delete_b;
    String selectedDate= "";

    private Button medicineB;
    private Button waterB;
    BottomNavigationView bottomNavigationView;

    View bottomSheetView;

    // 약 알람 목록
    ArrayList<String> items = new ArrayList<>();
    ArrayAdapter<String> adapter;

    

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
        // 24시간 hr로 수정
        int alarmHour = isAM ? hour : hour + 12;

        // 알람 추가
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MedicineAlarm.class); // Create intent for your receiver
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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }



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
        Intent intent = new Intent(this, MedicineAlarm.class);
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


            Toast.makeText(this, "약 알람이 저장되었어요!" , Toast.LENGTH_SHORT).show();
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

        detailTextView = findViewById(R.id.detailTextView);
        dateTextView = findViewById(R.id.dateTextView);

        Back_b = findViewById(R.id.Back_b); // 뒤로 되돌아가는 버튼
        Back_b.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
            startActivity(intent);
        });

        Setting_b = findViewById(R.id.Setting_b); // 설정화면으로 이동하는 버튼
        Setting_b.setOnClickListener(view -> {
            Intent intent = new Intent(CalendarActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        noti_b = findViewById(R.id.Bell_b);
        noti_b.setOnClickListener(view->{
            Intent intent = new Intent(this, AlarmListActivity.class);
            startActivity(intent);
        });



        Edit_b = findViewById(R.id.edit_b); // Edit 버튼을 누르면 ScheduleActivity로 이동
        Edit_b.setOnClickListener(v -> {
            if (selectedDate != null) {
                Intent intent = new Intent(CalendarActivity.this, ScheduleActivity.class);
                intent.putExtra("SELECTED_DATE", selectedDate); // 선택된 날짜 전달
                startActivity(intent);
            }
        });


        Delete_b = findViewById(R.id.delete_b); // Delete 버튼을 눌러 데이터 삭제
        Delete_b.setOnClickListener(v -> {
            deleteScheduleForSelectedDate(selectedDate);
        });

        MaterialCalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setSelectionColor(Color.parseColor("#65558F"));

        // 현재 날짜 가져오기
        CalendarDay today = CalendarDay.today();
        selectedDate = today.getYear() + "-" + (today.getMonth() + 1) + "-" + today.getDay();

        loadSavedColors();

        // 데코레이터를 업데이트
        updateCalendarDecorators();


        // 현재 날짜를 선택된 상태로 설정
        calendarView.setDateSelected(today, true);

        // 현재 날짜 표시
        dateTextView.setText(selectedDate);
        loadScheduleForSelectedDate(selectedDate);

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            selectedDate = date.getYear() + "-" + (date.getMonth() + 1) + "-" + date.getDay();
            dateTextView.setText(selectedDate); // 선택된 날짜 표시
            loadScheduleForSelectedDate(selectedDate); // selectedDate를 사용
        });

        //알림 채널 설정
        createNotificationChannel_medicine();
        createNotificationChannel_water();



        medicineB = findViewById(R.id.medicine_button);
        waterB = findViewById(R.id.water_button);

        items = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);

        //네비게이션 바
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_gps) {
                    startActivity(new Intent(CalendarActivity.this, GpsActivity.class));
                    return true;
                }
                return false;
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
            //알람 리스트 새로고침
            adapter.notifyDataSetChanged();
        }



        // 약 알람 리스트
        bottomSheetView = getLayoutInflater().inflate(R.layout.medicine_bottom_sheet, null);
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

        waterB.setOnClickListener(view -> {
            WaterSheet waterBottomSheetDialog = new WaterSheet(this, sharedPreferences);
            waterBottomSheetDialog.showDialog();
        });


    }//oncreate 끝

    // 저장된 색상 데이터를 불러오는 메서드
    private void loadSavedColors() {
        SharedPreferences sharedPreferences = getSharedPreferences("SchedulePreferences", MODE_PRIVATE);

        // 저장된 날짜-색상 데이터를 HashMap으로 불러오기
        for (String key : sharedPreferences.getAll().keySet()) {
            if (key.endsWith("_color")) { // 색상 키 확인
                String date = key.replace("_color", ""); // 날짜 추출
                int color = sharedPreferences.getInt(key, Color.TRANSPARENT); // 색상 값 불러오기
                dateColorMap.put(date, color);
            }
        }
    }

    private void loadScheduleForSelectedDate(String selectedDate) {
        SharedPreferences sharedPreferences = getSharedPreferences("SchedulePreferences", MODE_PRIVATE);

        // 저장된 일정 제목과 상세내용 가져오기
        String scheduleTitle = sharedPreferences.getString(selectedDate + "_title", "No title");
        String scheduleDetail = sharedPreferences.getString(selectedDate + "_detail", "No details");

        // TextView에 설정
        detailTextView.setText(scheduleDetail);
        dateTextView.setText(scheduleTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String updatedDate = intent.getStringExtra("selectedDate");
        int updatedColor = intent.getIntExtra("selectedColor", Color.TRANSPARENT);

        if (updatedDate != null) {
            dateColorMap.put(updatedDate, updatedColor);
            updateCalendarDecorators();
        }
    }
    private void updateCalendarDecorators() {
        MaterialCalendarView calendarView = findViewById(R.id.calendarView);

        calendarView.removeDecorators(); // 이전 데코레이터 제거

        // 데코레이터 추가
        for (String date : dateColorMap.keySet()) {
            int color = dateColorMap.get(date);

            calendarView.addDecorator(new DayViewDecorator() {
                @Override
                public boolean shouldDecorate(CalendarDay day) {
                    // 날짜 문자열 형식으로 변환
                    String dayString = day.getYear() + "-" + (day.getMonth() + 1) + "-" + day.getDay();
                    return date.equals(dayString);
                }

                @Override
                public void decorate(DayViewFacade view) {
                    // 동그란 배경 추가
                    view.setSelectionDrawable(createCircleDrawable(color));
                }
            });
        }
    }

    // 동그란 배경 drawable 생성
    private Drawable createCircleDrawable(int color) {
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(color);
        drawable.setIntrinsicWidth(50);  // 크기 조정 (필요시 변경)
        drawable.setIntrinsicHeight(50);
        return drawable;
    }

    private void deleteScheduleForSelectedDate(String selectedDate) {
        if (selectedDate == null || selectedDate.isEmpty()) return;

        SharedPreferences sharedPreferences = getSharedPreferences("SchedulePreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 선택된 날짜와 관련된 일정 데이터 삭제
        editor.remove(selectedDate + "_title");
        editor.remove(selectedDate + "_detail");
        editor.remove(selectedDate + "_color");

        // 선택된 날짜와 관련된 증상 기록 데이터 삭제
        editor.remove(selectedDate + "_symptom"); // 증상 기록 키 (예시)

        editor.apply();

        // HashMap에서도 삭제
        dateColorMap.remove(selectedDate);

        // UI 업데이트
        detailTextView.setText("No details");
        dateTextView.setText("No title");

        updateCalendarDecorators();
    }

    private void createNotificationChannel_medicine() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String name = "Medicine Reminder Channel";
            String description = "Channel for medicine reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("MedicineChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d("CreateAlarm", "Medicine alarm channel created!");
            }
        }
    }


    private void createNotificationChannel_water() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Water Reminder Channel";
            String description = "Channel for water drinking reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("WaterChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d("CreateAlarm", "Water alarm channel created!");
            }
        }
    }


}