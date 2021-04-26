package com.unipi.avyzentini.speedometer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements LocationListener, TextToSpeech.OnInitListener {

    SharedPreferences preferences;
    LocationManager locationManager;
    DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(Locale.getDefault(Locale.Category.FORMAT));
    Button button1,button2,button3;
    public static SQLiteDatabase db;
    public static int selected_button;
    TextView textView1;
    int threshold=1,count,choice;
    MyTts myTts;
    RadioGroup radioGroup;
    public  RadioButton all,sorted,select;
    private TextToSpeech textToSpeech;
    ConstraintLayout constraintLayout;
    FloatingActionButton floatingActionButton, edit_speedbutton,speechrec;
    Boolean visible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        locationManager = (LocationManager)(getSystemService(LOCATION_SERVICE));
        //register everything with their IDs
        button1 = findViewById(R.id.button1);
        button2 =findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        textView1 = findViewById(R.id.textView);
        myTts= new MyTts(this);
        floatingActionButton= findViewById(R.id.floatingActionButton);
        edit_speedbutton = findViewById(R.id.edit_speed_fab);
        speechrec= findViewById(R.id.voicerec);
        constraintLayout = findViewById(R.id.constrainedLayout);
        //for radio button
        radioGroup=findViewById(R.id.radiogroup);
        all=findViewById(R.id.all);
        sorted=findViewById(R.id.sorted);
        textToSpeech = new TextToSpeech(this,this);
        //save default speed
        SharedPreferences.Editor editor= preferences.edit();
        editor.putString("speed","3");
        editor.apply();
        button3.setEnabled(false);
        button3.setBackgroundColor(Color.LTGRAY);
        //open or create the database and table
        db = openOrCreateDatabase("gpsDB", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS gps(x TEXT,y TEXT, date TEXT,speed TEXT)");
        //create intent
        Intent intent = new Intent(getApplicationContext(),MainActivity2.class);
        //set on click listener for button
        button2.setOnClickListener(view -> {
            //get selected radio button
            selected_button = radioGroup.getCheckedRadioButtonId();
            select= findViewById(selected_button);

            Cursor cursor= db.rawQuery("SELECT * FROM gps",null);
            //check if database is empty or a radiobutton is selected
            if(cursor.getCount()==0||select==null){
                Toast.makeText(getApplicationContext(),"No Entries or Pick a Radio Button",Toast.LENGTH_LONG).show();
            }
            else {
                SharedPreferences.Editor editor1=preferences.edit();
                if(all.isChecked()){
                    choice=0;

                    editor1.putInt("choice",choice);
                    editor1.apply();
                }else if(sorted.isChecked()){
                    choice=1;
                    editor1.putInt("choice",choice);
                    editor1.apply();
                }
                startActivity(intent);}
        });
        //set onclick listener to stop the gps
        button3.setOnClickListener(view -> {
            locationManager.removeUpdates(MainActivity.this);
            count=0;
            button3.setEnabled(false);
            button1.setEnabled(true);
            button3.setBackgroundColor(Color.LTGRAY);
            button1.setBackgroundColor(Color.parseColor("#FF018786"));
            constraintLayout.setBackgroundColor(Color.parseColor("#395c7a"));

        });
        //make sure floating button and text are not visible
        edit_speedbutton.setVisibility(View.GONE);
        speechrec.setVisibility(View.GONE);
        visible= false;
        //set onclick listener for the main floating button
        floatingActionButton.setOnClickListener(view -> {
            if(!visible){
                edit_speedbutton.show();
                speechrec.show();
                visible=true;
            }
            else{
                edit_speedbutton.hide();
                speechrec.hide();
                visible=false;
            }
        });

        // set onclick listener for the edit speed button
        edit_speedbutton.setOnClickListener(view -> {
            final EditText editText = new EditText(MainActivity.this);
            //editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Edit Speed Threshold")
                    .setView(editText)
                    .setPositiveButton("OK", (dialog, which) -> {
                        SharedPreferences.Editor editor12 = preferences.edit();
                        editor12.putString("speed",editText.getText().toString());
                        editor12.apply();

                    }).setNegativeButton("Cancel", null)
                    .create()
                    .show();

        });

        //set onclick listener for speech recognition
        speechrec.setOnClickListener(view -> {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 2000);
            } else {
                Intent s =new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                s.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                s.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.ENGLISH);
                s.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
                s.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,MainActivity.this.getPackageName());
                startActivityForResult(s,10);
            }


        });


    }

    public void gps(View view) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},234);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, 0, 0, this);
            button1.setEnabled(false);
            button3.setEnabled(true);
            button1.setBackgroundColor(Color.LTGRAY);
            button3.setBackgroundColor(Color.parseColor("#FF018786"));

        }


    }

    @Override
    public void onLocationChanged(Location location) {
        if (location!=null) {
            String x_r = String.format("%.2f", location.getLatitude());
            String y_r = String.format("%.2f", location.getLongitude());
            ZonedDateTime dateTime = ZonedDateTime.now(ZoneId.systemDefault());
            String formattedDate = dateTime.format(formatter);
            //convert m/s to km/s
            float getspeed= location.getSpeed()*3600/1000;
            String speed = String.format("%.2f",getspeed);

            threshold= Integer.parseInt(preferences.getString("speed","Nothing set yet"));

            //minimize the gps points taken
            if(count==3){
                textView1.setText("Current Speed: "+speed);
                if (getspeed>threshold){
                    constraintLayout.setBackgroundColor(Color.parseColor("red"));
                    db.execSQL("INSERT INTO gps VALUES('"+x_r+"','"+y_r+"','"+formattedDate+"','"+speed+"')");
                    count=0;
                   myTts.speak("Slow down");

                }
                else{
                    constraintLayout.setBackgroundColor(Color.parseColor("#395c7a"));
                    count=0;
                }
            }
            count++;

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onInit(int status) {
        if (status==TextToSpeech.SUCCESS){
            int result = textToSpeech.setLanguage(Locale.ENGLISH);
            if (result==TextToSpeech.LANG_MISSING_DATA|| result==TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("DEBUG","Language Not Supported");
            }
        }
        else{
            Log.i("DEBUG","MISSION FAILED");
        }

    }

    public static class MyTts{
        private final TextToSpeech tts;

        public MyTts(Context context){
            TextToSpeech.OnInitListener initListener = new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    tts.setLanguage(Locale.forLanguageTag("EN"));
                }
            };
            tts = new TextToSpeech(context, initListener);}
        public void speak(String message){tts.speak(message,TextToSpeech.QUEUE_ADD,null,null);}
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null && resultCode==RESULT_OK){
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


            if(results.get(0).toUpperCase().contains("START")){
                button1.performClick();
            }
            else if(results.get(0).toUpperCase().contains("STOP")){
                button3.performClick();
            }
        }
    }
}