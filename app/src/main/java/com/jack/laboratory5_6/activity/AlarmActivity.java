package com.jack.laboratory5_6.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.jack.laboratory5_6.R;
import com.jack.laboratory5_6.service.NotificationService;

public class AlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        TextView setAlarm = (TextView) findViewById(R.id.setAlarm);
        TextView yourProfile = (TextView) findViewById(R.id.yourProfile);
        TextView scheduleList = (TextView) findViewById(R.id.scheduleList);
        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
        intent.setAction(NotificationService.UPDATE);
        startService(intent);
        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SetAlarmActivity.class));
            }
        });
        yourProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
            }
        });
        scheduleList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ScheduleActivity.class));
            }
        });
    }
}
