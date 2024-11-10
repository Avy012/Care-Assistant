package com.example.lighthouseofmemory;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class CalenderActivity extends AppCompatActivity {

    private Button medicineB;
    private Button waterB;
    private ImageButton backButton;
    BottomNavigationView bottomNavigationView;
    View bottomSheetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        backButton = findViewById(R.id.Back_b);
        medicineB = findViewById(R.id.medicine_button);
        waterB = findViewById(R.id.water_button);

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

        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);

        ListView listView = bottomSheetView.findViewById(R.id.listView);
        Button addButton = bottomSheetView.findViewById(R.id.addButton);

        // Create and set up the adapter for the ListView
        ArrayList<String> items = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);

        // Set up the button to add new items to the list
        addButton.setOnClickListener(v -> {
            // Add a new item and notify the adapter
            items.add("New Item " + (items.size() + 1));
            adapter.notifyDataSetChanged();
        });


        medicineB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.show();
            }
        });



    }
}