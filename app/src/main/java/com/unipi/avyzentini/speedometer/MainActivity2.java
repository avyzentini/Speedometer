package com.unipi.avyzentini.speedometer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {
    TableLayout tableLayout1;
    SQLiteDatabase db = MainActivity.db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        tableLayout1 = findViewById(R.id.tableLayout1);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Integer choice = preferences.getInt("choice",3);

        //sort by speed
        if (choice==1){
            //query to treat float as numbers and order by column speed
            Cursor cursor1 = db.rawQuery("SELECT * FROM gps ORDER BY CAST(speed AS FLOAT)DESC ", null);
            if (cursor1.getCount() > 0) {
                while (cursor1.moveToNext()) {
                    TableRow row1 = new TableRow(MainActivity2.this);
                    row1.setBackgroundColor(Color.parseColor("#FF018786"));
                    TextView p_x1 = new TextView(MainActivity2.this);
                    p_x1.setText(String.format("%s", cursor1.getString(0)));
                    p_x1.setTextColor(Color.BLACK);
                    p_x1.setGravity(Gravity.CENTER);
                    p_x1.setTextSize(12);
                    row1.addView(p_x1);
                    TextView p_y1 = new TextView(MainActivity2.this);
                    p_y1.setText(String.format("%s", cursor1.getString(1)));
                    p_y1.setTextColor(Color.BLACK);
                    p_y1.setGravity(Gravity.CENTER);
                    row1.addView(p_y1);
                    p_y1.setTextSize(12);
                    TextView timestamp1 = new TextView(MainActivity2.this);
                    timestamp1.setText(String.format("%s", cursor1.getString(2)));
                    timestamp1.setTextColor(Color.BLACK);
                    timestamp1.setGravity(Gravity.CENTER);
                    timestamp1.setTextSize(12);
                    row1.addView(timestamp1);

                    TextView speed = new TextView(MainActivity2.this);
                    speed.setText(String.format("%s", cursor1.getString(3)));
                    speed.setTextColor(Color.BLACK);
                    speed.setGravity(Gravity.CENTER);
                    speed.setTextSize(12);
                    row1.addView(speed);

                    tableLayout1.addView(row1);
                }

                cursor1.close();
            }
        }

        //all records
        if (choice == 0) {
            Cursor cursor = db.rawQuery("SELECT * FROM gps", null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    TableRow row1 = new TableRow(MainActivity2.this);
                    row1.setBackgroundColor(Color.parseColor("#FF018786"));
                    TextView p_x1 = new TextView(MainActivity2.this);
                    p_x1.setText(String.format("%s", cursor.getString(0)));
                    p_x1.setTextColor(Color.BLACK);
                    p_x1.setGravity(Gravity.CENTER);
                    p_x1.setTextSize(12);
                    row1.addView(p_x1);
                    TextView p_y1 = new TextView(MainActivity2.this);
                    p_y1.setText(String.format("%s", cursor.getString(1)));
                    p_y1.setTextColor(Color.BLACK);
                    p_y1.setGravity(Gravity.CENTER);
                    row1.addView(p_y1);
                    p_y1.setTextSize(12);
                    TextView timestamp1 = new TextView(MainActivity2.this);
                    timestamp1.setText(String.format("%s", cursor.getString(2)));
                    timestamp1.setTextColor(Color.BLACK);
                    timestamp1.setGravity(Gravity.CENTER);
                    timestamp1.setTextSize(12);
                    row1.addView(timestamp1);

                    TextView speed = new TextView(MainActivity2.this);
                    speed.setText(String.format("%s", cursor.getString(3)));
                    speed.setTextColor(Color.BLACK);
                    speed.setGravity(Gravity.CENTER);
                    speed.setTextSize(12);
                    row1.addView(speed);

                    tableLayout1.addView(row1);
                }

                cursor.close();

            }

        }
    }

}