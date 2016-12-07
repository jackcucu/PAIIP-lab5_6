package com.jack.laboratory5_6.receiver;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.jack.laboratory5_6.activity.AlarmRingActivity;

import java.util.Calendar;
public class NotificationBroadcastReceiver extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static long INTERVAL_MINUTE = AlarmManager.INTERVAL_FIFTEEN_MINUTES / 15;
    public static long INTERVAL_HOUR = AlarmManager.INTERVAL_FIFTEEN_MINUTES * 4;
    public static long INTERVAL_DAY = AlarmManager.INTERVAL_DAY;
    public static long INTERVAL_WEEK = INTERVAL_DAY * 7;
    public static long INTERVAL_MONTH = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH) * INTERVAL_DAY;
    public static long INTERVAL_YEAR = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR) * INTERVAL_DAY;

    public void onReceive(Context context, Intent intent) {
        Intent intentAlarm = new Intent(context.getApplicationContext(),AlarmRingActivity.class);
        intentAlarm.putExtra("pattern", intent.getExtras().getString("pattern"));
        intentAlarm.putExtra("time", intent.getExtras().getLong("time"));
        intentAlarm.putExtra("title", intent.getExtras().getString("title"));
        intentAlarm.putExtra("id", intent.getExtras().getInt("id"));
        intentAlarm.putExtra("repetition", intent.getExtras().getLong("repetition"));
        context.startActivity(intentAlarm);
    }
}
