package com.katrina.modules.magnify;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.katrina.modules.EmergencyListener;
import com.katrina.modules.KatrinaModule;
import com.katrina.modules.KatrinaModuleListener;
import com.katrina.ui.R;

/**
 * Created by kyle on 4/13/2015.
 */
public class MagnifyModule implements KatrinaModule {

    private Context mContext;

    @Override
    public Drawable getIconImage() {

        return mContext.getResources().getDrawable(R.mipmap.ic_magnifier);

    }

    @Override
    public String getName() {
        return "Magnifier";
    }

    @Override
    public boolean onModuleClick(Context context) {
        Intent intent = new Intent(context,Magnifier.class);
        context.startActivity(intent);
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
        return null;
    }

    @Override
    public void initialize(Context context) {
        this.mContext = context;
    }

    @Override
    public void stop() {

    }
}
