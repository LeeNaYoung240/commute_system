package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MonthlyCalendar extends AppCompatActivity {
    private CalendarView calendarView;
    private EditText editText;
    private String stringDateSelected;
    private DatabaseReference databaseReference;
    static int onButtonEvent = 0;

    Date currentTime = java.util.Calendar.getInstance().getTime();
    public static String format_Current_Time = "hh:mm:ss";
    public static String format_yyyyMMdd = "yyyyMMdd";
    static  String currentH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_calendar);

        calendarView = findViewById(R.id.calendarView);
        editText = findViewById(R.id.editText);

        SimpleDateFormat formatT = new SimpleDateFormat(format_Current_Time, Locale.getDefault());
        String currentT = formatT.format(currentTime);

        SimpleDateFormat formatH = new SimpleDateFormat(format_yyyyMMdd,Locale.getDefault());
        currentH = formatH.format(currentTime);


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                stringDateSelected = Integer.toString(year) + Integer.toString(month+1)+
                        Integer.toString(dayOfMonth);
                calenderClicked();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar");
    }

    private void calenderClicked(){
        databaseReference.child(stringDateSelected).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    editText.setText(snapshot.getValue().toString());
                }
                else{
                    editText.setText("null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void buttonSaveEvent(View view){
        onButtonEvent = onButtonEvent + 1;
        //databaseReference.child("Employee").child("ChoiSeEun")
                //.child(stringDateSelected).child("PersonalEvents").child("event"+onButtonEvent).setValue(editText.getText().toString());
        //databaseReference.child("Employee").child("LeeNaYoung").child(currentH).child("PersonalInfo").setValue(editText.getText().toString());
        //databaseReference.child("Employee").child("ChoiSeEun").child("EmployeeInfo").child("JobInfo").setValue(editText.getText().toString());
        //databaseReference.child("Employee").child("LeeNaYoung").child(stringDateSelected).setValue(editText.getText().toString());
        databaseReference.child("Admin").child(stringDateSelected).child("event"+onButtonEvent).setValue(editText.getText().toString());
        //databaseReference.child(stringDateSelected).setValue(editText.getText().toString());
    }
}