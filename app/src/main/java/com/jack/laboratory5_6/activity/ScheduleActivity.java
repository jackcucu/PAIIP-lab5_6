package com.jack.laboratory5_6.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import com.jack.laboratory5_6.adapter.ScheduleListAdapter;
import com.jack.laboratory5_6.R;
import com.jack.laboratory5_6.data.Data;
import com.jack.laboratory5_6.data.Default;
import com.jack.laboratory5_6.data.Events;
import com.jack.laboratory5_6.service.NotificationService;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {
    private static SimpleDateFormat dateFormatForDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private static SimpleDateFormat dateFormatForTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private ArrayList<String> description = new ArrayList<>();
    private XStream xstream = new XStream(new DomDriver());
    private XStream xstreamDef = new XStream(new DomDriver());
    private String name;
    private ArrayList<String> hour = new ArrayList<>();
    private ArrayList<String> date = new ArrayList<>();
    private Bitmap image;
    private Events events;
    private Default aDefault;
    private boolean foundNextEvent;
    private Data nextEvent;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        TextView dateText = (TextView) findViewById(R.id.date_text_list);
        TextView timeText = (TextView) findViewById(R.id.time_text_list);
        TextView nameText = (TextView) findViewById(R.id.name_list);
        final TextView descriptionText = (TextView) findViewById(R.id.description_text_list);
        ImageButton addButton = (ImageButton) findViewById(R.id.add_button_list);
        ImageButton mainButton = (ImageButton) findViewById(R.id.main_menu_button_list);
        Button editButton = (Button) findViewById(R.id.button_edit_list);
        editButton.setVisibility(View.GONE);
        initialiseDates();
        if(aDefault != null) nameText.setText(aDefault.getName());
        else nameText.setText("Me");
        if (events != null) {
            if (nextEvent != null) {
                dateText.setText(dateFormatForDate.format(nextEvent.getCalendarDate()));
                timeText.setText(dateFormatForTime.format(nextEvent.getCalendarDate()));
                descriptionText.setText(nextEvent.getTitle());
                editButton.setVisibility(View.VISIBLE);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
                        intent.setAction(NotificationService.CANCEL);
                        intent.putExtra("requestCode", nextEvent.getId());
                        startService(intent);
                        events.data.remove(nextEvent);
                        if (events.data.isEmpty()) {
                            getApplicationContext().deleteFile("events.xml");
                            events = null;
                        } else {
                            saveXML();
                        }
                        Intent modifyIntent = new Intent(getApplicationContext(), SetAlarmActivity.class);
                        modifyIntent.putExtra("title", nextEvent.getTitle());
                        modifyIntent.putExtra("time", nextEvent.getCalendarDate());
                        modifyIntent.putExtra("repeating", nextEvent.getRepeating());
                        modifyIntent.putExtra("pattern", nextEvent.getPattern());
                        modifyIntent.setAction("modify");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(modifyIntent);
                        finish();
                    }
                });
            } else {
                dateText.setText("                    ");
                timeText.setText("                      ");
                descriptionText.setText("No Upcoming Event");
                editButton.setVisibility(View.GONE);
            }
            ScheduleListAdapter adapter = new
                    ScheduleListAdapter(ScheduleActivity.this, description, hour, date, name, image);
            ListView list = (ListView) findViewById(R.id.list);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleActivity.this);
                    builder.setTitle("Choose");
                    builder.setPositiveButton("Modify", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Data data = events.data.get(position);
                            Intent intent = new Intent(getApplicationContext(), NotificationService.class);
                            intent.setAction(NotificationService.CANCEL);
                            intent.putExtra("requestCode", data.getId());
                            startService(intent);
                            events.data.remove(data);
                            if (events.data.isEmpty()) {
                                getApplicationContext().deleteFile("events.xml");
                                events = null;
                            } else {
                                saveXML();
                            }
                            Intent modifyIntent = new Intent(getApplicationContext(), SetAlarmActivity.class);
                            modifyIntent.putExtra("title", data.getTitle());
                            modifyIntent.putExtra("time", data.getCalendarDate());
                            modifyIntent.putExtra("repeating", data.getRepeating());
                            modifyIntent.putExtra("pattern", data.getPattern());
                            modifyIntent.setAction("modify");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(modifyIntent);
                            finish();
                        }
                    });
                    builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Data data = events.data.get(position);
                            Intent intent = new Intent(getApplicationContext(), NotificationService.class);
                            intent.setAction(NotificationService.CANCEL);
                            intent.putExtra("requestCode", data.getId());
                            startService(intent);
                            events.data.remove(data);
                            if (events.data.isEmpty()) {
                                getApplicationContext().deleteFile("events.xml");
                                events = null;
                                recreate();
                            } else {
                                saveXML();
                                dialog.cancel();
                                recreate();
                            }
                        }
                    });
                    builder.show();
                }
            });
        }
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetAlarmActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveXML() {
        Collections.sort(events.data);
        xstream.processAnnotations(Events.class);
        xstream.processAnnotations(Data.class);
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(new File(getApplicationContext().getFilesDir(), "events.xml"));
            outputStream.write(xstream.toXML(events).getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialiseDates() {
        image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile_logo);
        xstream.processAnnotations(Events.class);
        xstream.processAnnotations(Data.class);
        xstreamDef.processAnnotations(Default.class);
        InputStream inputStream2;
        try {
            inputStream2 = new FileInputStream(new File(getApplication().getFilesDir(), "defaults.xml"));
            aDefault = (Default) xstreamDef.fromXML(inputStream2);
            inputStream2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(aDefault != null) {
            name = aDefault.getName();
            image =  BitmapFactory.decodeFile(aDefault.getAvatar());

        } else name = "Me";
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(new File(getApplication().getFilesDir(), "events.xml"));
            events = (Events) xstream.fromXML(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(events != null) {
            for (Data data : events.data) {
                description.add(data.getTitle());
                hour.add(dateFormatForTime.format(data.getCalendarDate()));
                date.add(dateFormatForDate.format(data.getCalendarDate()));
                if (!foundNextEvent) {
                    Date g = new Date();
                    g.setTime(data.getCalendarDate());
                    if (new Date().before(g)) {
                        nextEvent = data;
                        foundNextEvent = true;
                    }
                }
            }
        }
    }
}
