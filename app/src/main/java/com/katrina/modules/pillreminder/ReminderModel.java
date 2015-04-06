package com.katrina.modules.pillreminder;

import android.net.Uri;

/**
 * A model class that defines what data a reminder should hold
 * Created by Mikyle on 3/11/2015.
 */
public class ReminderModel {

    //The unique id of each reminder
    public long id;
    //Name of the reminder
    public String name;
    /**
     * if repeatTimesDaily == 0 then take medicine once daily
     * 1 = twice
     * 2 = thrice
     * ...
     * 5 = six times daily
     */
    public int repeatTimesDaily;
    //To set repeating specific days
    private boolean repeatingDays[];
    public static final int SUNDAY = 0;
    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;
    public static final int SATURDAY = 6;

    //To set if reminder goes off once, repeating multiple times daily, weekly, or monthly
    public boolean once;
    public boolean daily;
    public boolean monthly;

    //To store the first date the reminder should start
    public int startDay;
    public int startMonth;
    public int startYear;

    //Store the alarm tone associated with reminder
    public Uri alarmTone;

    //If the alarm is enabled
    public boolean isEnabled;

    /**
     * Constructor
     */
    public ReminderModel() {
        repeatingDays = new boolean[7];
    }

    public void setRepeatingDay(int dayOfWeek, boolean value) {
        repeatingDays[dayOfWeek] = value;
    }

    public boolean getRepeatingDay(int dayOfWeek) {
        return repeatingDays[dayOfWeek];
    }

    public String getRepeatTimesDaily(int repeattimedaily) {
        String result = "Repeat ";

        switch (repeattimedaily) {
            case 0: {
                result += "One Time";
                break;
            }
            case 1: {
                result += "Two Times";
                break;
            }
            case 2: {
                result += "Three Times";
                break;
            }
            case 3: {
                result += "Four Times";
                break;
            }
            case 4: {
                result += "Five Times";
                break;
            }
            case 5: {
                result += "Six Times";
                break;
            }
            default: {
                result += "No Times";
            }
        }
        result += " Daily";
        return result;
    }
    public String getRepeatTimesMonthly(int date) {
        String result = "Repeat every month on the ";
        switch (date) {
            case 1: {
                result += "1st";
            }
            case 2: {
                result += "2nd";
            }
            case 3: {
                result += "3rd";
            }
            default: {
                result += date;
                result += "th";
            }
        }

        return result;
    }
}
