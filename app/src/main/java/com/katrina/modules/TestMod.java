package com.katrina.modules;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by alatnet on 3/8/2015.
 */
public class TestMod implements KatrinaModule {
    EmergencyListener emergencyListener;
    boolean active = false;

    @Override
    public Drawable getIconImage() {
        return null;
    }

    @Override
    public String getName() {
        return "TEST MOD";
    }

    @Override
    public boolean onModuleClick(Context c) {
        Log.i("TESTMOD.doAction", "TEST");
        emergencyListener.onEmergency();
        return false;
    }

    @Override
    public boolean onModuleLongClick(Context c) {
        return false;
    }

    @Override
    public String getError() {
        return "TEST ERROR";
    }

    @Override
    public MOD_TYPE getModuleType() { return MOD_TYPE.MODULE; }

    @Override
    public void setID(int id) {}

    @Override
    public void registerEmergencyListener(EmergencyListener emergencyListener) { this.emergencyListener = emergencyListener; }

    @Override
    public void registerKMListener(KatrinaModuleListener kmListener) {}

    @Override
    public void setActive(boolean active) {
        Log.i("TestMod.setActive",Boolean.toString(active));
        this.active = active;
    }

    @Override
    public boolean isActive() {
        return !this.active;
    }

    @Override
    public String getUniqueID() {
        return "TESTMOD001";
    }

    @Override
    public void initialize(Context context) {

    }

    @Override
    public void stop() {

    }

}
