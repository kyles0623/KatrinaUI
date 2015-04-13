package com.katrina.modules.flashlight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.katrina.modules.EmergencyListener;
import com.katrina.modules.KatrinaModule;
import com.katrina.modules.KatrinaModuleListener;
import com.katrina.modules.torch.Torch;
import com.katrina.ui.R;

import java.io.IOException;

/**
 * Created by kyle on 4/8/2015.
 */
public class FlashlightModule implements KatrinaModule, SurfaceHolder.Callback {

    /**
     * Context this module is attached to
     */
    private Context mContext;

    private Camera camera;

    private boolean isFlashOn = false;

    private boolean hasFlash;

    private SurfaceHolder mHolder;

    private Parameters params;
    @Override
    public void initialize(Context context) {
        mContext = context;
        hasFlash = context.getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {

            // device doesn't support flash
            // Show alert message and close the application
            AlertDialog alert = new AlertDialog.Builder(context)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    //finish();
                }
            });
            alert.show();
            return;
        }
        getCamera();
    }

    /*
	 * Get the camera
	 */
    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                SurfaceView preview = (SurfaceView)((Activity)(mContext)).findViewById(R.id.PREVIEW);
                mHolder = preview.getHolder();
                mHolder.addCallback(this);
                camera.setPreviewDisplay(mHolder);
                params = camera.getParameters();
                //camera.setPreviewDisplay());

                Log.d("FlashlightMod","Camera Initialized");
            }
            catch (RuntimeException e) {
                Log.e("FlashlightMod", e.getMessage());
            }
            catch (IOException e) {
                Log.d("FlashlightMod","Error setting preview: "+e);
                e.printStackTrace();
            }
        }
        else
        {
            Log.d("FlashlightMod","Camera is not null");
        }
    }

    private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || params == null) {
                Log.d("FlashlightMod","Camera or params null");
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);

            camera.setParameters(params);
            camera.startPreview();
            Log.d("FlashlightMod","Turning flash on");
            isFlashOn = true;

            // changing button/switch image
            //toggleButtonImage();
        }

    }

    /*s
	 * Turning Off flash
	 */
    private void turnOffFlash() {
        if (isFlashOn) {

            if (camera == null || params == null) {
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;
            Log.d("FlashlightMod","Turning flash off");
            // changing button/switch image
            //toggleButtonImage();
        }
    }


    @Override
    public Drawable getIconImage() {
        return null;
    }

    @Override
    public String getName() {
        return "Flashlight";
    }

    @Override
    public boolean onModuleClick(Context context) {
        if(isFlashOn)
        {
            turnOffFlash();
        }
        else
        {
            turnOnFlash();
        }
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
    public void stop() {
        turnOffFlash();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        mHolder = holder;
        try {
            Log.i("SurfaceHolder", "setting preview");
            camera.setPreviewDisplay(mHolder);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        Log.i("SurfaceHolder", "stopping preview");
        camera.stopPreview();
        mHolder = null;
    }
}
