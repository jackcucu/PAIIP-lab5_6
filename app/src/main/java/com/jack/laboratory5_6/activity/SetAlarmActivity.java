package com.jack.laboratory5_6.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.*;
import com.eftimoff.patternview.PatternView;
import com.jack.laboratory5_6.R;
import com.jack.laboratory5_6.data.Data;
import com.jack.laboratory5_6.data.Default;
import com.jack.laboratory5_6.data.Events;
import com.jack.laboratory5_6.receiver.NotificationBroadcastReceiver;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class SetAlarmActivity extends AppCompatActivity {
    private static SimpleDateFormat dateFormatForDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy | HH:mm", Locale.getDefault());
    private static SimpleDateFormat dateFormatForTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private Events events;
    private Default aDefault;
    private XStream xstream = new XStream(new DomDriver());
    private XStream xstreamDef = new XStream(new DomDriver());
    private long repeating = 0;
    private String patternString;
    private String stringPattern;
    private PatternView patternView;
    private Date parsedDate;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
        ImageButton desriptionButton = (ImageButton) findViewById(R.id.description_button_add);
        ImageButton timeButton = (ImageButton) findViewById(R.id.time_button_add);
        ImageButton dateButton = (ImageButton) findViewById(R.id.date_button_add);
        final ImageButton patternButton = (ImageButton) findViewById(R.id.pattern_button_add);
        ImageButton repetitionButton = (ImageButton) findViewById(R.id.repetition_button_add);
        ImageButton submitButton = (ImageButton) findViewById(R.id.submit_button_add);
        ImageButton mainButton = (ImageButton) findViewById(R.id.main_menu_button_add);
        TextView timeDateText = (TextView) findViewById(R.id.time_date_text_add);
        final TextView descriptionBottomText = (TextView) findViewById(R.id.description_text_add_bottom);
        final TextView descriptionText = (TextView) findViewById(R.id.description_text_add);
        final TextView repetitionText = (TextView) findViewById(R.id.repetition_text_add);
        TextView timeText = (TextView) findViewById(R.id.time_text_add);
        TextView dateText = (TextView) findViewById(R.id.date_text_add);
        final TextView patternText = (TextView) findViewById(R.id.pattern);
        timeDateText.setText(dateFormat.format(calendar.getTime()));
        dateText.setText(dateFormatForDate.format(calendar.getTime()));
        timeText.setText(dateFormatForTime.format(calendar.getTime()));
        xstreamDef.processAnnotations(Default.class);
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(new File(getApplication().getFilesDir(), "defaults.xml"));
            aDefault = (Default) xstreamDef.fromXML(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(aDefault != null) patternString = aDefault.getDefPattern();
        if (getIntent().getAction() != null) {
            if (getIntent().getAction().equals("modify")) {
                parsedDate = new Date(getIntent().getExtras().getLong("time"));
                timeDateText.setText(dateFormat.format(parsedDate.getTime()));
                dateText.setText(dateFormatForDate.format(parsedDate.getTime()));
                timeText.setText(dateFormatForTime.format(parsedDate.getTime()));
                repeating = getIntent().getExtras().getLong("repeating");
                patternString = getIntent().getExtras().getString("pattern");
                descriptionText.setText(getIntent().getExtras().getString("title"));
                descriptionBottomText.setText(getIntent().getExtras().getString("title"));
            }
        }
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        repetitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] value = new String[]{
                        "Does not repeat", "Every minute", "Every hour", "Every day", "Every week", "Every mouth", "Every Year"
                };
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SetAlarmActivity.this);
                alertDialogBuilder.setTitle("Choose interval");
                alertDialogBuilder.setItems(value, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                repeating = 0;
                                repetitionText.setText("Does not repeat");
                                break;
                            case 1:
                                repeating = NotificationBroadcastReceiver.INTERVAL_MINUTE;
                                repetitionText.setText("Every minute");
                                break;
                            case 2:
                                repeating = NotificationBroadcastReceiver.INTERVAL_HOUR;
                                repetitionText.setText("Every hour");
                                break;
                            case 3:
                                repeating = NotificationBroadcastReceiver.INTERVAL_DAY;
                                repetitionText.setText("Every day");
                                break;
                            case 4:
                                repeating = NotificationBroadcastReceiver.INTERVAL_WEEK;
                                repetitionText.setText("Every week");
                                break;
                            case 5:
                                repeating = NotificationBroadcastReceiver.INTERVAL_MONTH;
                                repetitionText.setText("Every month");
                                break;
                            case 6:
                                repeating = NotificationBroadcastReceiver.INTERVAL_YEAR;
                                repetitionText.setText("Every year");
                                break;
                        }
                    }
                });
                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();
            }
        });
        desriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SetAlarmActivity.this);
                builder.setTitle("Set description");
                final EditText input = new EditText(getApplicationContext());
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        descriptionText.setText(input.getText().toString());
                        descriptionBottomText.setText(input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        patternButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patternView = new PatternView(getApplicationContext());
                final AlertDialog.Builder builder = new AlertDialog.Builder(SetAlarmActivity.this);
                builder.setTitle("Set pattern");
                patternView.setPathColor(Color.WHITE);
                patternView.setDotColor(Color.WHITE);
                patternView.setCircleColor(Color.WHITE);
                patternView.setOnPatternDetectedListener(new PatternView.OnPatternDetectedListener() {
                    @Override
                    public void onPatternDetected() {
                        stringPattern = patternView.getPatternString();
                    }
                });
                builder.setView(patternView);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        patternString = stringPattern;
                        patternText.setText("Pattern Changed");
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        patternString = null;
                    }
                });
                builder.show();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parsedDate != null
                        && !TextUtils.isEmpty(descriptionText.getText().toString())
                        && !patternString.isEmpty()) {
                    xstream.processAnnotations(Events.class);
                    xstream.processAnnotations(Data.class);
                    InputStream inputStream;
                    try {
                        inputStream = new FileInputStream(new File(getApplicationContext().getFilesDir(), "events.xml"));
                        events = (Events) xstream.fromXML(inputStream);
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Data event = new Data();
                    event
                            .setTitle(descriptionText.getText().toString())
                            .setId(new Random().nextInt(543254))
                            .setCalendarDate(parsedDate.getTime())
                            .setRepeating(repeating)
                            .setPattern(patternString);
                    updateData(event);
                } else Toast.makeText(SetAlarmActivity.this, "Enter Dates", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getAction() != null) {
                    if (getIntent().getAction().equals("modify")) {
                        Toast.makeText(getApplicationContext(), "Please confirm your modifications", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void saveToXML() {
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

    private void updateData(Data info) {
        if (events == null) events = new Events();
        ArrayList<Data> data = new ArrayList<>();
        if (events.getData() == null) {
            events.data = data;
        }
        events.data.add(info);
        Collections.sort(events.data);
        saveToXML();
        Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(getIntent().getAction() != null) {
            if (getIntent().getAction().equals("modify")) {
                Toast.makeText(this, "Please confirm your modifications", Toast.LENGTH_SHORT).show();
            }
        }else {
            Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            super.onBackPressed();
        }
    }

    private class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            TextView viewById = (TextView) getActivity().findViewById(R.id.time_text_add);
            TextView timeDateText = (TextView) getActivity().findViewById(R.id.time_date_text_add);
            if (minute < 10) {
                viewById.setText(hourOfDay + ":0" + minute);
            } else {
                viewById.setText(hourOfDay + ":" + minute);
            }
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            parsedDate = calendar.getTime();
            timeDateText.setText(dateFormat.format(parsedDate));
        }
    }

    private class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            TextView dateText = (TextView) getActivity().findViewById(R.id.date_text_add);
            TextView timeDateText = (TextView) getActivity().findViewById(R.id.time_date_text_add);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            parsedDate = calendar.getTime();
            dateText.setText(dateFormatForDate.format(parsedDate));
            timeDateText.setText(dateFormat.format(parsedDate));
        }
    }
}
