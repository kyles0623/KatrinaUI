package com.katrina.modules.pillreminder;

import android.provider.BaseColumns;

/**
 * Created by Mikyle on 3/12/2015.
 */
public final class ReminderContract {

    public ReminderContract() {

    }

    public static abstract class Reminder implements BaseColumns {
        public static final String TABLE_NAME="REMINDER";
        public static final String COLUMN_NAME_MEDICINE_NAME = "NAME";
        public static final String COLUMN_NAME_REPEAT_DAILY = "REPEAT_DAILY";
        public static final String COLUMN_NAME_TIMES_DAILY_REPEATED = "TIMES_DAILY_REPEATED";
        public static final String COLUMN_NAME_DAYS_REPEATED = "DAYS_REPEATED";
        public static final String COLUMN_NAME_START_DAY = "START_DAY";
        public static final String COLUMN_NAME_START_MONTH = "START_MONTH";
        public static final String COLUMN_NAME_START_YEAR = "START_YEAR";
        public static final String COLUMN_NAME_ALARM_TONE = "TONE";
        public static final String COLUMN_NAME_ENABLED = "IS_ENABLED";
       // public static final String COLUMN_NAME_TEST = "IS_TEST";
    }
}
