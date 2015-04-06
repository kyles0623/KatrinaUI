package com.katrina.modules.pillreminder;

import android.content.Context;
import android.graphics.Color;
import android.transition.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.katrina.ui.R;

import java.util.List;

/**
 * Helps display a list of items in Pill_Reminder_Main
 * Created by Mikyle on 3/12/2015.
 */
public class ReminderListAdapter extends BaseAdapter {

    private Context myContext;
    private List<ReminderModel> myReminders;

    public ReminderListAdapter(Context context, List<ReminderModel> reminders)
    {
        myContext = context;
        myReminders = reminders;
    }

    public void setMyReminders(List<ReminderModel> reminders) {
        myReminders = reminders;
    }

    @Override
    public int getCount() {
        if(myReminders != null) {
            return myReminders.size();
        }
        else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if(myReminders !=null) {
            return myReminders.get(position);
        }
        else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if(myReminders != null) {
            return myReminders.get(position).id;
        }
        else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.reminder_list_item, parent, false);
        }

        ReminderModel model = (ReminderModel) getItem(position);

        TextView txtName = (TextView) view.findViewById(R.id.main_medicine_name);
        txtName.setText(model.name);

        if(model.daily) {
            TextView txtRepeatTimesDaily = (TextView) view.findViewById(R.id.main_repeat_times_daily);
            txtRepeatTimesDaily.setText(model.getRepeatTimesDaily(model.repeatTimesDaily));

            updateTextColor((TextView) view.findViewById(R.id.alarm_item_sunday), model.getRepeatingDay(ReminderModel.SUNDAY));
            updateTextColor((TextView) view.findViewById(R.id.alarm_item_monday), model.getRepeatingDay(ReminderModel.MONDAY));
            updateTextColor((TextView) view.findViewById(R.id.alarm_item_tuesday), model.getRepeatingDay(ReminderModel.TUESDAY));
            updateTextColor((TextView) view.findViewById(R.id.alarm_item_wednesday), model.getRepeatingDay(ReminderModel.WEDNESDAY));
            updateTextColor((TextView) view.findViewById(R.id.alarm_item_thursday), model.getRepeatingDay(ReminderModel.THURSDAY));
            updateTextColor((TextView) view.findViewById(R.id.alarm_item_friday), model.getRepeatingDay(ReminderModel.FRIDAY));
            updateTextColor((TextView) view.findViewById(R.id.alarm_item_saturday), model.getRepeatingDay(ReminderModel.SATURDAY));
        }
        else {
            TextView txtRepeatTimesDaily = (TextView) view.findViewById(R.id.main_repeat_times_daily);
            txtRepeatTimesDaily.setText(model.getRepeatTimesMonthly(model.startDay));

            view.findViewById(R.id.alarm_item_sunday).setVisibility(View.GONE);
            view.findViewById(R.id.alarm_item_monday).setVisibility(View.GONE);
            view.findViewById(R.id.alarm_item_tuesday).setVisibility(View.GONE);
            view.findViewById(R.id.alarm_item_wednesday).setVisibility(View.GONE);
            view.findViewById(R.id.alarm_item_thursday).setVisibility(View.GONE);
            view.findViewById(R.id.alarm_item_friday).setVisibility(View.GONE);
            view.findViewById(R.id.alarm_item_saturday).setVisibility(View.GONE);
        }


        ToggleButton btnToggle = (ToggleButton) view.findViewById(R.id.main_button_toggle);
        btnToggle.setChecked(model.isEnabled);
        btnToggle.setTag(Long.valueOf(model.id));
        btnToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((Pill_Reminder_Main) myContext).setReminderEnabled(((Long)buttonView.getTag()).longValue(), isChecked);
            }
        });

        view.setTag(Long.valueOf(model.id));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Pill_Reminder_Main) myContext).startNewReminderActivity(((Long) v.getTag()).longValue());
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((Pill_Reminder_Main) myContext).deleteAlarm(((Long) v.getTag()).longValue());
                return true;
            }
        });

        return view;
    }

    private void updateTextColor(TextView view, boolean isOn) {
        if(isOn) {
            view.setTextColor(Color.BLUE);
        }
        else {
            view.setTextColor(Color.LTGRAY);
        }
    }
}
