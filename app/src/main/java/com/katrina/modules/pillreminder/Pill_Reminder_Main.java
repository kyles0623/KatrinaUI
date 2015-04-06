package com.katrina.modules.pillreminder;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.katrina.ui.R;

/**
 * Displays a list of reminders in the database with a toggle on/off switch
 * Also displays days reminder should go off
 *
 * Created by Mikyle on 3/11/2015.
 */
public class Pill_Reminder_Main extends ListActivity {

    private ReminderDBHelper dbHelper = new ReminderDBHelper(this);
    private ReminderListAdapter myAdapter;
    private Context myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        myContext = this;
        myAdapter = new ReminderListAdapter(this,dbHelper.getReminders());

        setContentView(R.layout.activity_pill__reminder_main);
        setListAdapter(myAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pill__reminder__main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Get the id of the menu item selected.
        // Start the activity specified by the menu item click
        switch(item.getItemId()) {
            case R.id.action_add_new_reminder: {
                startNewReminderActivity(-1);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            myAdapter.setMyReminders(dbHelper.getReminders());
            myAdapter.notifyDataSetChanged();
        }
    }

    public void setReminderEnabled(long id, boolean isChecked) {
        AlarmManagerHelper alarmManagerHelper = new AlarmManagerHelper();
        alarmManagerHelper.cancelAlarms(this);

        ReminderModel model = dbHelper.getReminder(id);
        model.isEnabled = isChecked;
        dbHelper.updateReminder(model);

        alarmManagerHelper.setAlarms(this);
        myAdapter.setMyReminders(dbHelper.getReminders());
        myAdapter.notifyDataSetChanged();
    }

    public void startNewReminderActivity(long id) {
        Intent intent = new Intent(this, New_Reminder_Activity.class);
        intent.putExtra("id", id);
        startActivityForResult(intent, 0);
    }

    public void deleteAlarm(long id) {
        final long reminderId = id;
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setMessage("Delete Reminder?")
                .setTitle("Confirm Delete")
                .setCancelable(true)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlarmManagerHelper alarmManagerHelper = new AlarmManagerHelper();
                        alarmManagerHelper.cancelAlarms(myContext);
                        //delete from db
                        dbHelper.deleteReminder(reminderId);
                        myAdapter.setMyReminders(dbHelper.getReminders());
                        myAdapter.notifyDataSetChanged();
                        alarmManagerHelper.setAlarms(myContext);
                    }
                }).show();

    }
}
