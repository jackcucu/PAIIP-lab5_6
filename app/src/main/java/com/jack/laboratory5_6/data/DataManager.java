package com.jack.laboratory5_6.data;

import android.app.Activity;
import android.content.Context;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.*;
import java.util.Collections;

public class DataManager {
    public static <T>  T readXML(Context context, T object) {
        XStream xStream = new XStream(new DomDriver());
        InputStream inputStream;
        if(object instanceof Events) {
            xStream.processAnnotations(Events.class);
            xStream.processAnnotations(Data.class);
            Collections.sort(((Events) object).data);
            try {
                inputStream = new FileInputStream(new File(context.getFilesDir(), "events.xml"));
                object = (T) xStream.fromXML(inputStream);
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return object;
        } else if(object instanceof Default) {
            xStream.processAnnotations(Default.class);
            try {
                inputStream = new FileInputStream(new File(context.getFilesDir(), "defaults.xml"));
                object = (T) xStream.fromXML(inputStream);
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return object;
        }
        return null;
    }

    public static void saveToXML(Context context, Object object) {
        XStream xStream = new XStream(new DomDriver());
        FileOutputStream outputStream;
        if(object instanceof Events) {
            xStream.processAnnotations(Events.class);
            xStream.processAnnotations(Data.class);
            try {
                outputStream = new FileOutputStream(new File(context.getFilesDir(), "events.xml"));
                outputStream.write(xStream.toXML(object).getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(object instanceof Default) {
            xStream.processAnnotations(Default.class);
            try {
                outputStream = new FileOutputStream(new File(context.getFilesDir(), "defaults.xml"));
                outputStream.write(xStream.toXML(object).getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
