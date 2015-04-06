package com.katrina.modules.pillreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
* Created by Mikyle on 3/19/2015.
*/
public class AlarmManagerHelper extends BroadcastReceiver{
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String TIME_HOUR = "timeHour";
    public static final String TIME_MINUTE = "timeMinute";
    public static final String TONE = "alarmTone";

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.  During this time you can use the other methods on
     * BroadcastReceiver to view/modify the current result values.  This method
     * is always called within the main thread of its process, unless you
     * explicitly asked for it to be scheduled on a different thread using
     * . When it runs on the main
     * thread you should
     * never perform long-running operations in it (there is a timeout of
     * 10 seconds that the system allows before considering the receiver to
     * be blocked and a candidate to be killed). You cannot launch a popup dialog
     * in your implementation of onReceive().
     * <p/>
     * <p><b>If this BroadcastReceiver was launched through a &lt;receiver&gt; tag,
     * then the object is no longer alive after returning from this
     * function.</b>  This means you should not perform any operations that
     * return a result to you asynchronously -- in particular, for interacting
     * with services, you should use
     * {@link android.content.Context#startService(android.content.Intent)} instead of
     * .  If you wish
     * to interact with a service that is already running, you can use
     * {@link #peekService}.
     * <p/>
     * <p>The Intent filters used in {@link android.content.Context#registerReceiver}
     * and in application manifests are <em>not</em> guaranteed to be exclusive. They
     * are hints to the operating system about how to find suitable recipients. It is
     * possible for senders to force delivery to specific recipients, bypassing filter
     * resolution.  For this reason, {@link #onReceive(android.content.Context, android.content.Intent) onReceive()}
     * implementations should respond only to known actions, ignoring any unexpected
     * Intents that they may receive.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        setAlarms(context);
    }

    /**
     * This method firsts cancels all alarms in order to prevent alarm setting conflict
     * Then we attempt to find the next time an alarm should go off
     * @param context
     */
    public void setAlarms(Context context) {
        cancelAlarms(context);

        ReminderDBHelper dbHelper = new ReminderDBHelper(context);
        //Get a list of all reminders
        List<ReminderModel> reminders = dbHelper.getReminders();

        //Iterate through all objects in the list
        for(ReminderModel reminder : reminders) {
            if (reminder.isEnabled) {
                PendingIntent pendingIntent = createPendingIntent(context, reminder);
                //This calendar is used to set the alarm. For now we aren't worrying about minutes or seconds
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MINUTE, 00);
                calendar.set(Calendar.SECOND, 00);

                //Init some variables for checking for the next instance of an alarm
                int nowDay = Calendar.getInstance().get((Calendar.DAY_OF_WEEK));
                int nowHour = Calendar.getInstance().get((Calendar.HOUR_OF_DAY));
                int nowMinute = Calendar.getInstance().get((Calendar.MINUTE));
                boolean alarmSet = false;


                if(reminder.daily) {
                    ArrayList<Integer> nextInstances = findNextDailyInstance(reminder);
                    //Find out the next day the alarm must go off
                    alarmloop: {
                    for(int dayOfWeek = nowDay; dayOfWeek <= Calendar.SATURDAY; ++dayOfWeek) {
                        if(reminder.getRepeatingDay(dayOfWeek - 1)) {
                            if(dayOfWeek == nowDay) {
                                //Iterate through all the times the alarm must go off today
                                //If we find one that is greater than the current hour, then set
                                //it for that time.
                                for(int i = 0; i < nextInstances.size(); i++) {
                                    if(nextInstances.get(i) > nowHour) {
                                        calendar.set(Calendar.HOUR_OF_DAY, nextInstances.get(i));
                                        alarmSet = true;
                                        break alarmloop;
                                    }
                                }
                            }
                            else {
                                //We assume we have passed the hour at which we can set another alarm for today
                                //Find the first time on the next day the alarm must go off
                                calendar.set(Calendar.HOUR_OF_DAY, nextInstances.get(0));
                                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                                alarmSet = true;
                                break alarmloop;
                            }
                        }
                    }} //end of named block
                    //If the alarm was not set, then we can assume that the next alarm must go off
                    //on the next week
                    if(!alarmSet) {
                        for (int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= nowDay; ++dayOfWeek) {
                            if (reminder.getRepeatingDay(dayOfWeek - 1)) {
                                calendar.set(Calendar.HOUR_OF_DAY, nextInstances.get(0));
                                calendar.add(Calendar.WEEK_OF_YEAR, 1);
                                alarmSet = true;
                                break;
                            }
                        }
                    }
                }
                else { //reminder is set to go off monthly, find next instance
                    calendar.set(Calendar.DAY_OF_MONTH, reminder.startDay);
                    calendar.set(Calendar.HOUR_OF_DAY, 8);
                    if(reminder.startDay > nowDay) { //the day has already passed, so set it for next month
                        calendar.add(Calendar.MONTH, 1);
                    }
                    alarmSet = true;
                }


                if(alarmSet) {
                    setAlarm(context, calendar, pendingIntent);
                }


            }
        }
    }

    /**
     * Sets an alarm using Android's AlarmManager
     * @param context
     * @param calendar date/time information for the alarm
     * @param pendingIntent
     */
    private static void setAlarm(Context context, Calendar calendar, PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }


    /**
     * Cancels all alarms
     * @param context
     */
    public static void cancelAlarms(Context context) {
        ReminderDBHelper dbHelper = new ReminderDBHelper(context);

        List<ReminderModel> reminders = dbHelper.getReminders();

        if(reminders != null) {
            for(ReminderModel reminder : reminders) {
                if (reminder.isEnabled) {
                    PendingIntent pendingIntent = createPendingIntent(context, reminder);

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                }
            }
        }

    }

    private static PendingIntent createPendingIntent(Context context, ReminderModel model) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.putExtra(ID, model.id);
        intent.putExtra(NAME, model.name);
        intent.putExtra(TIME_HOUR, model.startMonth);
        intent.putExtra(TIME_MINUTE, model.startDay);
        intent.putExtra(TONE, model.alarmTone.toString());

        return PendingIntent.getService(context, (int) model.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Takes a model's repeatTimesDaily field and converts it into a set
     * of times that the alarm should go off
     *
     * EX: repeatTimesDaily = 1, ArrayList should contain 8 (8 = 8:00)
     * EX repeatTimesDaily = 3, ArrayList should contain 8, 12, 16, 20 (16 = 16:00)
     * NOTE: users are not able to set when the alarm goes off. This could be a future feature
     * @param r reference model
     * @return ArrayList containing repeat times in 24hr format
     */
    private ArrayList<Integer> findNextDailyInstance(ReminderModel r) {
        ArrayList<Integer> repeatList = new ArrayList<Integer>();
        int repeat = r.repeatTimesDaily;

        switch(repeat) {
            case 0: { //Once Daily
                repeatList.add(8);
                break;
            }
            case 1: { //Twice Daily
                repeatList.add(8);
                repeatList.add(15);
                break;
            }
            case 2: { //Three Times Daily
                repeatList.add(8);
                repeatList.add(13);
                repeatList.add(18);
                break;
            }
            case 3: { //Four Times Daily
                repeatList.add(8);
                repeatList.add(12);
                repeatList.add(16);
                repeatList.add(20);
                break;
            }
            case 4: { //Five Times Daily
                repeatList.add(8);
                repeatList.add(11);
                repeatList.add(14);
                repeatList.add(17);
                repeatList.add(20);
                break;
            }
            case 5: { //Six Times Daily
                repeatList.add(8);
                repeatList.add(11);
                repeatList.add(13);
                repeatList.add(15);
                repeatList.add(17);
                repeatList.add(20);
                break;
            }
            default: {
                repeatList.add(0);
            }
        }

        return repeatList;
    }
}
