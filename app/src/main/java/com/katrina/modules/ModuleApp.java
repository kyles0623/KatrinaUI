package com.katrina.modules;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * This is a module to start APK apps on an android system.
 * Created by Alexander on 2/27/2015.
 */
public class ModuleApp implements KatrinaModule {
    private String name,label;
    private Drawable icon;

    public ModuleApp(String label, String name, Drawable icon){
        this.name = name;
        this.label= label;
        this.icon = icon;
    }

    @Override
    public Drawable getIconImage() {
        return this.icon;
    }

    @Override
    public String getName() {
        return this.label;
    }

    @Override
    public boolean onModuleClick(Context c) {
        Intent i = c.getPackageManager().getLaunchIntentForPackage(this.name);
        c.startActivity(i);
        return true;
    }

    @Override
    public boolean onModuleLongClick(Context c) {
        return true;
    }

    @Override
    public String getError() {
        return "";
    } //doesnt give an error.

    public MOD_TYPE getModuleType(){ return MOD_TYPE.APP;} //App type.

    @Override
    public void setID(int id) {}

    @Override
    public void registerEmergencyListener(EmergencyListener emergencyListener) {}

    @Override
    public void registerKMListener(KatrinaModuleListener kmListener) {}

    @Override
    public void setActive(boolean active) {
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public String getUniqueID() {
        return this.name;
    }

    @Override
    public void initialize(Context context) {

    }

    @Override
    public void stop() {

    }

    public void setContext(Context context)
    {

    }
}
