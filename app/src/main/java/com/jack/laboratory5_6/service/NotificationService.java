package com.jack.laboratory5_6.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.jack.laboratory5_6.data.Data;
import com.jack.laboratory5_6.data.Events;
import com.jack.laboratory5_6.receiver.NotificationBroadcastReceiver;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class NotificationService extends IntentService {
    public static final String CANCEL = "CANCEL";
    public static final String UPDATE = "UPDATE";
    private Events events;

    public NotificationService() {
        super("StudentOrganizerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (UPDATE.equals(intent.getAction())) {
            executeUpdate();
        } else if (CANCEL.equals(intent.getAction())) {
            executeCancel(intent.getExtras().getInt("requestCode"));
        }
    }

    private void executeCancel(int requestCode) {
        Intent notificationIntent = new Intent(this, NotificationBroadcastReceiver.class);
        notificationIntent.setAction(requestCode + "");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void executeUpdate() {
        XStream xstream = new XStream(new DomDriver());
        xstream.processAnnotations(Events.class);
        xstream.processAnnotations(Data.class);
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(new File(this.getFilesDir(), "events.xml"));
            events = (Events) xstream.fromXML(inputStream);
            inputStream.close();
        } catch (IOException ignored) {
        }
        if (events != null) {
            for (Data data : events.data) {
                Intent notificationIntent = new Intent(this, NotificationBroadcastReceiver.class);
                notificationIntent.putExtra(NotificationBroadcastReceiver.NOTIFICATION_ID, data.getId());
                notificationIntent.putExtra("pattern", data.getPattern());
                notificationIntent.putExtra("title", data.getTitle());
                notificationIntent.putExtra("time", data.getCalendarDate());
                notificationIntent.putExtra("id", data.getId());
                notificationIntent.putExtra("repetition", data.getRepeating());
                notificationIntent.setAction(data.getId() + "");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, data.getId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Date g = new Date();
                g.setTime(data.getCalendarDate());
                if (new Date().before(g)) {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, data.getCalendarDate(), pendingIntent);
                }
            }
        }
    }
}
