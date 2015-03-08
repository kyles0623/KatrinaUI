package com.katrina.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
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
    public boolean doAction(Context c) {
        Intent i = c.getPackageManager().getLaunchIntentForPackage(this.name);
        c.startActivity(i);
        return true;
    }

    @Override
    public String getError() {
        return "";
    }

    public MOD_TYPE getModuleType(){ return MOD_TYPE.APP;}
}
