package com.jack.laboratory5_6.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.*;
public class Data implements Comparable<Data>{
    @XStreamAlias("ID")
    private int id;
    @XStreamAlias("Title")
    private String title;
    @XStreamAlias("CalendarDate")
    private long calendarDate;
    @XStreamAlias("Repeating")
    private long repeating;
    @XStreamAlias("Pattern")
    private String pattern;

    public String getPattern() {
        return pattern;
    }

    public Data setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public long getRepeating() {
        return repeating;
    }

    public Data setRepeating(long repeating) {
        this.repeating = repeating;
        return this;
    }

    public long getCalendarDate() {
        return calendarDate;
    }

    public Data setCalendarDate(long calendarDate) {
        this.calendarDate = calendarDate;
        return this;
    }

    public int getId() {
        return id;
    }

    public Data setId(int id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Data setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public int compareTo(Data another) {
        Date date1 = new Date(getCalendarDate());
        Date date2 = new Date(another.getCalendarDate());
        return date1.compareTo(date2);
    }
}
