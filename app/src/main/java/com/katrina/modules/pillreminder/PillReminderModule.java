package com.katrina.modules.pillreminder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.katrina.modules.EmergencyListener;
import com.katrina.modules.KatrinaModule;
import com.katrina.modules.KatrinaModuleListener;
import com.katrina.ui.R;

import java.util.UUID;

/**
 * This Module is used for the Katrina Pill Reminder.
 * All this module does is point the icon to the Pill_Reminder_Main Activity.
 * Created by kyle on 4/5/2015.
 */
public class PillReminderModule implements KatrinaModule {

    /**
     * ID to be set.
     */
    private int id;

    /**
     * Unique ID
     */
    private UUID uniqueID = UUID.randomUUID();

    /**
     * Context this module is attached to.
     */
    private Context context;

    @Override
    public Drawable getIconImage() {
        return null;
    }

    @Override
    public String getName() {
        return context.getString(R.string.pill_reminder_name);
    }

    @Override
    public boolean onModuleClick(Context c) {

        c.startActivity(new Intent(c,Pill_Reminder_Main.class));

        return true;
    }

    @Override
    public boolean onModuleLongClick(Context c) {
        return false;
    }

    @Override
    public String getError() {
        return null;
    }

    @Override
    public MOD_TYPE getModuleType() {
        return MOD_TYPE.MODULE;
    }

    @Override
    public void registerEmergencyListener(EmergencyListener emergencyListener) {

    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public void registerKMListener(KatrinaModuleListener kmListener) {

    }

    @Override
    public void setActive(boolean active) {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public String getUniqueID() {
        return uniqueID.toString();
    }

    @Override
    public void initialize(Context context) {
        this.context = context;
    }

    @Override
    public void stop() {

    }
}
