package com.thecreators.android.opdplus;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class HomePageActivity extends AppCompatActivity {

    private Button mHistoryPanel;
    private Button mDoctorsPanel;
    private String mPhoneNumber;
    private Button mAppointmentsPanel;
    private Context mContext;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mPhoneNumber=getIntent().getStringExtra("phone number");


        mHistoryPanel=(Button) findViewById(R.id.history_panel);
        mContext=this;
        mHistoryPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,PatientHistoryActivity.class);
                intent.putExtra("phone number",mPhoneNumber);
                mContext.startActivity(intent);
            }
        });

        mDoctorsPanel =(Button) findViewById(R.id.doctors_panel);
        mDoctorsPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,DoctorInfoActivity.class);
                intent.putExtra("phone number",mPhoneNumber);
                mContext.startActivity(intent);

            }
        });

        mAppointmentsPanel = (Button) findViewById(R.id.appointment_panel);
        mAppointmentsPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,MyAppointmentsActivity.class);
                intent.putExtra("phone number",mPhoneNumber);
                mContext.startActivity(intent);
            }
        });

    }


}
