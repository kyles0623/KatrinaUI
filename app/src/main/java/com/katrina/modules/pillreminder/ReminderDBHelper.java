package com.katrina.modules.pillreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.katrina.modules.pillreminder.ReminderContract.Reminder;

import java.util.ArrayList;
import java.util.List;

/**
 * Helps with database operations including create table, insert, delete, select
 * Also includes methods to translate ReminderModel to values and vice versa
 *
 * Created by Mikyle on 3/12/2015.
 */
public class ReminderDBHelper extends SQLiteOpenHelper {
    ///DB version and name
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "reminder.db";

    private static final String SQL_CREATE_REMINDER =
            "CREATE TABLE " + Reminder.TABLE_NAME + " (" +
            Reminder._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Reminder.COLUMN_NAME_MEDICINE_NAME + " TEXT, " +
            Reminder.COLUMN_NAME_REPEAT_DAILY + " BOOLEAN, " +
            Reminder.COLUMN_NAME_TIMES_DAILY_REPEATED + " INTEGER, " +
            Reminder.COLUMN_NAME_DAYS_REPEATED + " TEXT, " +
            Reminder.COLUMN_NAME_START_DAY + " INTEGER, " +
            Reminder.COLUMN_NAME_START_MONTH + " INTEGER, " +
            Reminder.COLUMN_NAME_START_YEAR + " INTEGER, " +
            Reminder.COLUMN_NAME_ALARM_TONE + " TEXT, " +
            Reminder.COLUMN_NAME_ENABLED + " BOOLEAN)";

    private static final String SQL_DELETE_REMINDER =
            "DROP TABLE IF EXISTS " + Reminder.TABLE_NAME;


    public ReminderDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_REMINDER);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_REMINDER);
        onCreate(db);
    }


    /**
     * Inputs a cursor containing values to be translated to a ReminderModel object
     * Useful for reading data from database
     * @param c cursor containing values
     * @return ReminderModel that has been populated with values
     */
    private ReminderModel populateModel(Cursor c) {
        ReminderModel model = new ReminderModel();
        model.id = c.getLong(c.getColumnIndex(Reminder._ID));
        model.name = c.getString(c.getColumnIndex(Reminder.COLUMN_NAME_MEDICINE_NAME));
        model.daily = c.getInt(c.getColumnIndex(Reminder.COLUMN_NAME_REPEAT_DAILY)) == 0 ? false : true;
        model.repeatTimesDaily = c.getInt(c.getColumnIndex(Reminder.COLUMN_NAME_TIMES_DAILY_REPEATED));
        model.startDay = c.getInt(c.getColumnIndex(Reminder.COLUMN_NAME_START_DAY));
        model.startMonth = c.getInt(c.getColumnIndex(Reminder.COLUMN_NAME_START_MONTH));
        model.startYear = c.getInt(c.getColumnIndex(Reminder.COLUMN_NAME_START_YEAR));
        model.alarmTone = c.getString(c.getColumnIndex(Reminder.COLUMN_NAME_ALARM_TONE)) != "" ? Uri.parse(c.getString(c.getColumnIndex((Reminder.COLUMN_NAME_ALARM_TONE)))) : null;
        model.isEnabled = c.getInt(c.getColumnIndex(Reminder.COLUMN_NAME_ENABLED)) == 0 ? false : true;
        //model.test = c.getInt(c.getColumnIndex(Reminder.COLUMN_NAME_TEST)) == 0 ? false : true;

        String[] repeatingDays = c.getString(c.getColumnIndex(Reminder.COLUMN_NAME_DAYS_REPEATED)).split(",");
        for (int i = 0; i < repeatingDays.length; ++i) {
            model.setRepeatingDay(i, repeatingDays[i].equals("false") ? false : true);
        }

        return model;
    }

    /**
     * Inputs a ReminderModel object and translates the data in the object to values
     * Useful for inserting or updating the database
     * @param model model to be translated
     * @return ContentValues of the ReminderModel
     */
    private ContentValues populateContent(ReminderModel model) {
        ContentValues values = new ContentValues();
        values.put(Reminder.COLUMN_NAME_MEDICINE_NAME, model.name);
        values.put(Reminder.COLUMN_NAME_REPEAT_DAILY, model.daily);
        values.put(Reminder.COLUMN_NAME_TIMES_DAILY_REPEATED, model.repeatTimesDaily);
        values.put(Reminder.COLUMN_NAME_START_DAY, model.startDay);
        values.put(Reminder.COLUMN_NAME_START_MONTH, model.startMonth);
        values.put(Reminder.COLUMN_NAME_START_YEAR, model.startYear);
        values.put(Reminder.COLUMN_NAME_ALARM_TONE, model.alarmTone != null ? model.alarmTone.toString() : "");
        values.put(Reminder.COLUMN_NAME_ENABLED, model.isEnabled);
        //values.put(Reminder.COLUMN_NAME_TEST, model.test);

        String repeatingDays = "";
        for (int i = 0; i < 7; ++i) {
            repeatingDays += model.getRepeatingDay(i) + ",";
        }
        values.put(Reminder.COLUMN_NAME_DAYS_REPEATED, repeatingDays);

        return values;
    }

    /**
     * Creates a reminder record in the database
     * @param model the ReminderModel object that needs to be inserted
     * @return
     */
    public long createReminder(ReminderModel model) {
        ContentValues values = populateContent(model);
        return getWritableDatabase().insert(Reminder.TABLE_NAME, null, values);
    }

    /**
     * Gets a reminder from the database. Only one result should be returned
     * @param id the id of the reminder row
     * @return a model object that has values equal to that record in the database
     */
    public ReminderModel getReminder(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + Reminder.TABLE_NAME + " WHERE " + Reminder._ID + " = " + id;

        Cursor c = db.rawQuery(select, null);

        if(c.moveToNext()) {
            return populateModel(c);
        }
        else {
            return null;
        }
    }

    /**
     * Updates a reminder in the database
     * @param model the ReminderModel object that needs to be updated
     * @return
     */
    public long updateReminder(ReminderModel model) {
        //Translates the data in model to values
        ContentValues values = populateContent(model);
        return getWritableDatabase().update(Reminder.TABLE_NAME, values, Reminder._ID + " = ?", new String[] { String.valueOf(model.id) });
    }

    /**
     * Deletes a reminder from the database based on the id
     * @param id the reminder to be deleted
     * @return
     */
    public int deleteReminder(long id) {
        return getWritableDatabase().delete(Reminder.TABLE_NAME, Reminder._ID + " = ?", new String[] { String.valueOf(id) });
    }

    /**
     * Queries the database for ALL reminders
     * @return a list of reminders in the database
     */
    public List<ReminderModel> getReminders() {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + Reminder.TABLE_NAME;

        Cursor c = db.rawQuery(select, null);

        List<ReminderModel> alarmList = new ArrayList<ReminderModel>();

        while (c.moveToNext()) {
            alarmList.add(populateModel(c));
        }

        if (!alarmList.isEmpty()) {
            return alarmList;
        }

        return alarmList;
    }
}
