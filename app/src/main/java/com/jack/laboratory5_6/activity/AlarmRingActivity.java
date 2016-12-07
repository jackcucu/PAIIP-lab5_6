package com.jack.laboratory5_6.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;
import com.eftimoff.patternview.PatternView;
import com.jack.laboratory5_6.R;
import com.jack.laboratory5_6.data.Data;
import com.jack.laboratory5_6.data.Events;
import com.jack.laboratory5_6.service.NotificationService;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.*;

public class AlarmRingActivity extends AppCompatActivity {
    private PatternView patternView;
    private String patternString;
    private MediaPlayer mPlayer;
    private Events events;
    private XStream xStream = new XStream(new DomDriver());
    private static SimpleDateFormat dateFormatForTime = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    public void onBackPressed() {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ring);
        patternView = (PatternView) findViewById(R.id.patternView);
        patternString = getIntent().getExtras().getString("pattern");
        TextView hourText = (TextView) findViewById(R.id.hour_text_alarm);
        hourText.setText(dateFormatForTime.format(new Date(getIntent().getExtras().getLong("time")).getTime()));
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(this, alarmSound);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
            mPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mPlayer.setLooping(true);
            try {
                mPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPlayer.start();
        }
        patternView.setPathColor(Color.WHITE);
        patternView.setDotColor(Color.WHITE);
        patternView.setCircleColor(Color.WHITE);
        patternView.setOnPatternDetectedListener(new PatternView.OnPatternDetectedListener() {
            @Override
            public void onPatternDetected() {
                if (patternString.equals(patternView.getPatternString())) {
                    if (mPlayer != null) {
                        mPlayer.stop();
                    }
                    if(getIntent().getExtras().getLong("repetition") != 0) {
                        xStream.processAnnotations(Events.class);
                        xStream.processAnnotations(Data.class);
                        InputStream inputStream;
                        try {
                            inputStream = new FileInputStream(new File(getFilesDir(), "events.xml"));
                            events = (Events) xStream.fromXML(inputStream);
                            inputStream.close();
                        } catch (IOException ignored) {
                        }
                        Data data = new Data();
                        for (Data datum : events.data) {
                            if (datum.getId() == getIntent().getExtras().getInt("id")) data = datum;
                        }
                        events.getData().remove(data);
                        data.setCalendarDate(Calendar.getInstance().getTimeInMillis() + data.getRepeating());
                        events.data.add(data);
                        Collections.sort(events.data);
                        FileOutputStream outputStream;
                        try {
                            outputStream = new FileOutputStream(new File(getApplicationContext().getFilesDir(), "events.xml"));
                            outputStream.write(xStream.toXML(events).getBytes());
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
                        intent.setAction(NotificationService.UPDATE);
                        startService(intent);
                    }
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect wake up!!!", Toast.LENGTH_SHORT).show();
                    patternView.setDisplayMode(PatternView.DisplayMode.Wrong);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }
}
