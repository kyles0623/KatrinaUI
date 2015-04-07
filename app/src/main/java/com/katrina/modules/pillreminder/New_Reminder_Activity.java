package com.katrina.modules.pillreminder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.katrina.ui.R;

/**
 * Displays a form to create a new reminder
 * Created by Mikyle on 3/11/2015.
 */
public class New_Reminder_Activity extends Activity {

    private ReminderModel reminderDetails;
    private TextView medicineName;
    private RadioButton dailyRadioButton;
    private RadioButton monthlyRadioButton;
    private Spinner spinnerDaily;
    private TextView txtChooseDays;
    private RelativeLayout checkboxWeekly;
    private CheckBox bxSunday;
    private CheckBox bxMonday;
    private CheckBox bxTuesday;
    private CheckBox bxWednesday;
    private CheckBox bxThursday;
    private CheckBox bxFriday;
    private CheckBox bxSaturday;
    private DatePicker startDate;
    private TextView txtToneSelection;

    private ReminderDBHelper dbHelper = new ReminderDBHelper(this);


    /**
     * This method is called on creation of the activity and sets up some private fields
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);

        //Init some variables
        reminderDetails = new ReminderModel();
        medicineName = (TextView) findViewById(R.id.new_reminder_name);
        dailyRadioButton = (RadioButton) findViewById(R.id.new_reminder_radio_daily);
        monthlyRadioButton = (RadioButton) findViewById(R.id.new_reminder_radio_monthly);
        spinnerDaily = (Spinner) findViewById(R.id.new_reminder_daily_spinner);
        txtChooseDays = (TextView) findViewById(R.id.new_reminder_choose_days_label);
        checkboxWeekly = (RelativeLayout) findViewById(R.id.new_reminder_checkbox_container);
        bxSunday = (CheckBox) findViewById(R.id.new_reminder_sunday);
        bxMonday = (CheckBox) findViewById(R.id.new_reminder_monday);
        bxTuesday = (CheckBox) findViewById(R.id.new_reminder_tuesday);
        bxWednesday = (CheckBox) findViewById(R.id.new_reminder_wednesday);
        bxThursday = (CheckBox) findViewById(R.id.new_reminder_thursday);
        bxFriday = (CheckBox) findViewById(R.id.new_reminder_friday);
        bxSaturday = (CheckBox) findViewById(R.id.new_reminder_saturday);
        txtToneSelection = (TextView) findViewById(R.id.alarm_label_tone_selection);
        startDate = (DatePicker) findViewById(R.id.new_reminder_start_date_picker);

        //Hide keyboard unless user explicitly brings it up
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        //Check to see if an existing model was passed in.
        long id = getIntent().getExtras().getLong("id");

        //Set up a new model
        if(id == -1) {
            reminderDetails = new ReminderModel();
            reminderDetails.id = id;
        }
        //A model has been passed in, so initialize all fields
        else {
            reminderDetails = dbHelper.getReminder(id);
            medicineName.setText(reminderDetails.name);
            //If true then the model repeats daily.
            if(reminderDetails.daily){
                dailyRadioButton.setChecked(true);
                spinnerDaily.setVisibility(View.VISIBLE);
                txtChooseDays.setVisibility(View.VISIBLE);
                checkboxWeekly.setVisibility(View.VISIBLE);
                spinnerDaily.setSelection(reminderDetails.repeatTimesDaily);

                bxSunday.setChecked(reminderDetails.getRepeatingDay(ReminderModel.SUNDAY));
                bxMonday.setChecked(reminderDetails.getRepeatingDay(ReminderModel.MONDAY));
                bxTuesday.setChecked(reminderDetails.getRepeatingDay(ReminderModel.TUESDAY));
                bxWednesday.setChecked(reminderDetails.getRepeatingDay(ReminderModel.WEDNESDAY));
                bxThursday.setChecked(reminderDetails.getRepeatingDay(ReminderModel.THURSDAY));
                bxFriday.setChecked(reminderDetails.getRepeatingDay(ReminderModel.FRIDAY));
                bxSaturday.setChecked(reminderDetails.getRepeatingDay(ReminderModel.SATURDAY));
            }
            //Model repeats monthly
            else {
                monthlyRadioButton.setChecked(true);
                spinnerDaily.setVisibility(View.GONE);
                txtChooseDays.setVisibility(View.GONE);
                checkboxWeekly.setVisibility(View.GONE);
            }

            startDate.init(reminderDetails.startYear, reminderDetails.startMonth, reminderDetails.startDay, null);
            txtToneSelection.setText(RingtoneManager.getRingtone(this, reminderDetails.alarmTone).getTitle(this));
        }


        //Set up onClickListener for ring tone selection
        final LinearLayout ringToneContainer = (LinearLayout) findViewById(R.id.alarm_ringtone_container);
        ringToneContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK) {
            switch (requestCode) {
                case 1: {
                    //Save tone to model and then set text in view
                    reminderDetails.alarmTone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    txtToneSelection.setText(RingtoneManager.getRingtone(this, reminderDetails.alarmTone).getTitle(this));
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_reminder, menu);
        return true;
    }

    /**
     * This method listens for the Save button to be pressed in the view
     * If it is pressed, then we want to save data to ReminderModel
     * @param item the item that was pressed
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Context context;

        switch(item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            case R.id.action_save_reminder_details: {
                //Validate Form
                CharSequence validateMessage = validateForm();

                //If empty string then form is validated
                if(validateMessage.equals(""))
                {
                    AlarmManagerHelper alarmManagerHelper = new AlarmManagerHelper();
                    //Save data to model
                    updateModelFromLayout();

                    //Cancel alarms as to not have conflicting alarms
                    alarmManagerHelper.cancelAlarms(this);

                    //Either create a new row in DB or update an existing based on id
                    ReminderDBHelper dbHelper = new ReminderDBHelper(this);
                    if(reminderDetails.id < 0) {
                        dbHelper.createReminder(reminderDetails);
                    }
                    else {
                        dbHelper.updateReminder(reminderDetails);
                    }
                    alarmManagerHelper.setAlarms(this);

                    //Show toast confirmation
                    validateMessage = "Reminder Saved";
                    context  = getApplicationContext();
                    Toast toast = Toast.makeText(context, validateMessage, Toast.LENGTH_SHORT);
                    toast.show();
                    setResult(RESULT_OK);
                    finish();
                    return true;
                }
                //Display toast with an 'error message': validateMessage
                else
                {
                    context  = getApplicationContext();
                    Toast toast = Toast.makeText(context, validateMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    /**
     * This method listens for any radio buttons pressed in the view
     * If Daily is selected, then a combobox that determines how often the pill is taken
     * and a checkbox list that displays what days of the week the user wants to take the pill
     * @param view
     */
    public void onIntervalRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        //check which one is clicked and display the associated options
        switch(view.getId()) {
            case R.id.new_reminder_radio_daily: {
                if(checked) {
                    //set visible and others invisible
                    spinnerDaily.setVisibility(View.VISIBLE);
                    txtChooseDays.setVisibility(View.VISIBLE);
                    checkboxWeekly.setVisibility(View.VISIBLE);
                    //Update daily, weekly, monthly bools in model
                    reminderDetails.daily = true;
                    reminderDetails.monthly = false;
                    break;
                }
            }
            case R.id.new_reminder_radio_monthly: {
                if(checked) {
                    //set visible and others invisible

                    spinnerDaily.setVisibility(View.GONE);
                    txtChooseDays.setVisibility(View.GONE);
                    checkboxWeekly.setVisibility(View.GONE);
                    //Update daily, weekly, monthly bools in model
                    reminderDetails.daily = false;
                    reminderDetails.monthly = true;
                    break;
                }
            }
        }
    }

    /**
     * Attempts to validate the form. If the form is validated then return a null value
     * @return A message to display to the user
     */
    public CharSequence validateForm() {
        CharSequence message;

        EditText medicineName = (EditText) findViewById(R.id.new_reminder_name);
        //Hide soft keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromInputMethod(medicineName.getWindowToken(),0);

        //Check if medicine name is empty
        if(medicineName.getText().toString().equals(""))
        {
            message = "Enter a medicine name!";
            return message;
        }

        //Check if any radio buttons have been pressed
        RadioButton dailyRadio = (RadioButton) findViewById(R.id.new_reminder_radio_daily);
        RadioButton monthlyRadio = (RadioButton) findViewById(R.id.new_reminder_radio_monthly);
        if(!dailyRadio.isChecked() && !monthlyRadio.isChecked())
        {
            message = "Check Daily or Monthly!";
            return message;
        }

        return "";
    }


    /**
     * Reads all fields of the form and updates the model
     */
    private void updateModelFromLayout() {

        EditText edtName = (EditText) findViewById(R.id.new_reminder_name);
        reminderDetails.name = edtName.getText().toString();

        RadioButton chkDaily = (RadioButton) findViewById(R.id.new_reminder_radio_daily);


        //If Daily is checked then see how many times a day and what days to repeat reminder
        if(chkDaily.isChecked())
        {
            reminderDetails.daily = true;
            reminderDetails.monthly = false;

            Spinner chkTimesDaily = (Spinner) findViewById(R.id.new_reminder_daily_spinner);
            reminderDetails.repeatTimesDaily = chkTimesDaily.getSelectedItemPosition();

            CheckBox chkSunday = (CheckBox) findViewById(R.id.new_reminder_sunday);
            reminderDetails.setRepeatingDay(ReminderModel.SUNDAY, chkSunday.isChecked());

            CheckBox chkMonday = (CheckBox) findViewById(R.id.new_reminder_monday);
            reminderDetails.setRepeatingDay(ReminderModel.MONDAY, chkMonday.isChecked());

            CheckBox chkTuesday = (CheckBox) findViewById(R.id.new_reminder_tuesday);
            reminderDetails.setRepeatingDay(ReminderModel.TUESDAY, chkTuesday.isChecked());

            CheckBox chkWednesday = (CheckBox) findViewById(R.id.new_reminder_wednesday);
            reminderDetails.setRepeatingDay(ReminderModel.WEDNESDAY, chkWednesday.isChecked());

            CheckBox chkThursday = (CheckBox) findViewById(R.id.new_reminder_thursday);
            reminderDetails.setRepeatingDay(ReminderModel.THURSDAY, chkThursday.isChecked());

            CheckBox chkFriday = (CheckBox) findViewById(R.id.new_reminder_friday);
            reminderDetails.setRepeatingDay(ReminderModel.FRIDAY, chkFriday.isChecked());

            CheckBox chkSaturday = (CheckBox) findViewById(R.id.new_reminder_saturday);
            reminderDetails.setRepeatingDay(ReminderModel.SATURDAY, chkSaturday.isChecked());
        }
        else
        {
            reminderDetails.daily = false;
            reminderDetails.monthly = true;
        }

        DatePicker chkDate = (DatePicker) findViewById(R.id.new_reminder_start_date_picker);
        reminderDetails.startDay = chkDate.getDayOfMonth();
        reminderDetails.startMonth = chkDate.getMonth();
        reminderDetails.startYear = chkDate.getYear();


        reminderDetails.isEnabled = true;
    }
}
