package com.katrina.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

/**
 * Created by alatnet on 3/13/2015.
 */
public class Utilities {
    public static Uri checkUriExists (Context mContext,Uri mUri) {
        Drawable d = null;
        if (mUri != null) {
            if ("content".equals(mUri.getScheme())) {
                try {
                    d = Drawable.createFromStream(
                            mContext.getContentResolver().openInputStream(mUri),
                            null);
                } catch (Exception e) {
                    //Log.w("checkUriExists", "Unable to open content: " + mUri, e);
                    mUri = null;
                }
            } else {
                d = Drawable.createFromPath(mUri.toString());
            }

            if (d == null) {
                // Invalid uri
                mUri = null;
            }
        }

        return mUri;
    }

    public static final boolean DEBUG = true;
}
