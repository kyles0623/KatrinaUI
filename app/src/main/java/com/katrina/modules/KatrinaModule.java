package com.katrina.modules;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by Alexander on 2/27/2015.
 */
public interface KatrinaModule {
    enum MOD_TYPE{ MODULE, APP, MISC }
    public Drawable getIconImage(); //set a module's displayed image.
    public String getName();  //get the module's name.
    public boolean doAction(Context c); //execute custom code.
    public String getError(); //Clear error after being called.
    public MOD_TYPE getModuleType(); //get a module type.
    public void registerEmergencyListener(EmergencyListener emergencyListener); //optional.  Use this to notify an emergency situation.
    public void setID(int id); //use this to set your module id.  REQUIRED FOR KatrinaModuleListener!
    public void registerKMListener(KatrinaModuleListener kmListener); //use this to change your icon. MUST HAVE AN ID!
}
