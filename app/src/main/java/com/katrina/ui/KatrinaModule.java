package com.katrina.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by Alexander on 2/27/2015.
 */
public interface KatrinaModule {
    public Drawable getIconImage();
    public String getName();
    public boolean doAction(Context c);
    public String getError();
}
