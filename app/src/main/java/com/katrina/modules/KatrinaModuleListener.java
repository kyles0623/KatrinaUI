package com.katrina.modules;

import android.graphics.drawable.Drawable;

/**
 * The KatrinaModuleListener is used
 * to update outside components of the module.
 * This includes UI components.
 * Created by alatnet on 3/12/2015.
 */
public interface KatrinaModuleListener {

    /**
     * Update the icon for the the Katrina Module.
     * @param id ID of the module icon to update
     * @param drawable New Drawable Icon
     */
    public void changeModuleIconImage(int id, Drawable drawable);
}
