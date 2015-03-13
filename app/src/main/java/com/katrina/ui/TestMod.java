package com.katrina.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by alatnet on 3/8/2015.
 */
public class TestMod implements KatrinaModule {
    EmergencyListener emergencyListener;

    @Override
    public Drawable getIconImage() {
        return null;
    }

    @Override
    public boolean changeIconImage() { return false; }

    @Override
    public String getName() {
        return "TEST MOD";
    }

    @Override
    public boolean doAction(Context c) {
        Log.i("TESTMOD.doAction", "TEST");
        emergencyListener.onEmergency();
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
    public void registerEmergencyListener(EmergencyListener emergencyListener) {
        this.emergencyListener = emergencyListener;
    }

    @Override
    public void registerKMListener(KatrinaModuleListener kmListener) {}
}
