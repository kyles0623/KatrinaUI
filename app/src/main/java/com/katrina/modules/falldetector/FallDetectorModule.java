package com.katrina.modules.falldetector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.LocalBroadcastManager;

import com.katrina.modules.EmergencyListener;
import com.katrina.modules.KatrinaModule;
import com.katrina.modules.KatrinaModuleListener;

import java.util.UUID;

/**
 * The FallDetectorModule is a KatrinaModule that is to remain hidden.
 * The module is used to run a background service to listen for fall detections.
 * If a fall is detected, the EmergencyListener, if set, will be notified.
 * Created by kyle on 4/5/2015.
 */
public class FallDetectorModule implements KatrinaModule {

    /**
     * Used by the FallDetectionService to communicate that a fall has been detected.
     */
    public static final String FALL_DETECTED_ACTION = "FALL_DETECTED_ACTION";

    /**
     * Context module is running in
     */
    private Context mainContext;

    /**
     * FallDetectorModule is active or not
     */
    private boolean active = false;

    /**
     * Random ID given
     */
    private UUID id = UUID.randomUUID();

    /**
     * EmergencyListener attached to module
     */
    private EmergencyListener emergencyListener;


    @Override
    public void initialize(Context context) {
        this.mainContext = context;
        Intent intent = new Intent(context,FallDetectionService.class);
        IntentFilter filter = new IntentFilter(FALL_DETECTED_ACTION);
        mainContext.registerReceiver(new FallDetectorBroadcastReceiver(),filter);
        mainContext.startService(intent);
    }

    @Override
    public void stop() {

    }

    @Override
    public Drawable getIconImage() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean onModuleClick(Context c) {
        return false;
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
        return MOD_TYPE.BACKGROUND;
    }

    @Override
    public void registerEmergencyListener(EmergencyListener emergencyListener) {
        this.emergencyListener = emergencyListener;
    }

    @Override
    public void setID(int id) {
    }

    @Override
    public void registerKMListener(KatrinaModuleListener kmListener) {

    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public String getUniqueID() {
        return id.toString();
    }

    /**
     * This BroadcastReceiver allows communication between the
     * FallDetectorModule and the FallDetectorService. When
     * Values are updated, the service sends and intent to this.
     * When a fall is detected, the service sends an intent to this.
     */
    private class FallDetectorBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(FALL_DETECTED_ACTION))
            {
                if(emergencyListener != null) {

                    emergencyListener.onEmergency();
                }
            }
        }
    }

}
