package com.jack.laboratory5_6.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.eftimoff.patternview.PatternView;
import com.jack.laboratory5_6.R;
import com.jack.laboratory5_6.data.Data;
import com.jack.laboratory5_6.data.Default;
import com.jack.laboratory5_6.data.Events;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.*;

public class ProfileActivity extends AppCompatActivity {
    String patternString;
    PatternView patternView;
    String stringPattern;
    String picturePath;
    Events events;
    Default aDefault = new Default();
    XStream xstream = new XStream(new DomDriver());
    XStream xstreamDef = new XStream(new DomDriver());
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ImageButton mainButton = (ImageButton) findViewById(R.id.main_menu_button_profile);
        ImageButton addButton = (ImageButton) findViewById(R.id.add_button_profile);
        ImageButton avatarButton = (ImageButton) findViewById(R.id.avatar_button_profile);
        ImageButton profileButton = (ImageButton) findViewById(R.id.user_name_button_profile);
        ImageButton patternButton = (ImageButton) findViewById(R.id.pattern_button_profile);
        TextView numberText = (TextView) findViewById(R.id.number_schedule_text_profile);
        final TextView statusText = (TextView) findViewById(R.id.status_text_profile);
        final TextView userText = (TextView) findViewById(R.id.user_text_profile);
        final TextView userNameText = (TextView) findViewById(R.id.user_name_text_profile);
        int number = 0;
        xstream.processAnnotations(Events.class);
        xstreamDef.processAnnotations(Default.class);
        xstream.processAnnotations(Data.class);
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(new File(getApplication().getFilesDir(), "events.xml"));
            events = (Events) xstream.fromXML(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream inputStream2;
        try {
            inputStream2 = new FileInputStream(new File(getApplication().getFilesDir(), "defaults.xml"));
            aDefault = (Default) xstreamDef.fromXML(inputStream2);
            inputStream2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        userNameText.setText(aDefault.getName());
        userText.setText(aDefault.getName());
        statusText.setText(aDefault.getStatus());
        picturePath = aDefault.getAvatar();
        CircleImageView avatarCircleImageView = (CircleImageView) findViewById(R.id.profile_image);
        if(aDefault != null) {
            String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE"};
            ActivityCompat.requestPermissions(this, permissions, 0x11);
            avatarCircleImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
        if (events != null) {
            number = events.data.size();
        }
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetAlarmActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Set name");
                final EditText input = new EditText(getApplicationContext());
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userNameText.setText(input.getText().toString());
                        userText.setText(input.getText().toString());
                        aDefault.setName(input.getText().toString());
                        saveToXML();
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
        avatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent avatarIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(avatarIntent, 777);
            }
        });
        statusText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Set status");
                final EditText input = new EditText(getApplicationContext());
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        statusText.setText(input.getText().toString());
                        aDefault.setStatus(input.getText().toString());
                        saveToXML();
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
                final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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
                        aDefault.setDefPattern(stringPattern);
                        saveToXML();
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
        if(number == 0) numberText.setText("No Schedule");
        else numberText.setText(number + " Schedule");
    }
    private void saveToXML() {
        xstreamDef.processAnnotations(Default.class);
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(new File(getApplicationContext().getFilesDir(), "defaults.xml"));
            outputStream.write(xstreamDef.toXML(aDefault).getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 777 && resultCode == RESULT_OK
                    && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();
                aDefault.setAvatar(picturePath);
                saveToXML();
                CircleImageView avatarCircleImageView = (CircleImageView) findViewById(R.id.profile_image);
                avatarCircleImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG)
                    .show();
        }
    }
}
