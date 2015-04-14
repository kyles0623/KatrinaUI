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
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.katrina.modules.EmergencyListener;
import com.katrina.modules.KatrinaModule;
import com.katrina.modules.KatrinaModuleListener;
import com.katrina.ui.R;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * This Flashlight Module allows the user to activate the cameras flashlight
 * when clicking on the FlashLight Button
 * Created by kyle on 4/8/2015.
 */
public class FlashlightModule implements KatrinaModule, SurfaceHolder.Callback, Runnable {

    /**
     * Context this module is attached to
     */
    private Context mContext;

    /**
     * Camera object to access flash
     */
    private Camera camera;

    /**
     * Indicates whether the flashlight is currently on or off
     */
    private boolean isFlashOn = false;

    /**
     * Inidicates whether flashlight use is even possible given the hardware
     */
    private boolean hasFlash;

    /**
     * Controller of the Surface View. Needed
     * to activate flashlight
     */
    private SurfaceHolder mHolder;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private KatrinaModuleListener katrinaModuleListener;

    /**
     * Camera Parameters to set the flashlight on.
     */
    private Parameters params;

    private int id;


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
    }

    /**
     * Retrieve the camera if needed.
     */
    private boolean getCamera() {
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
                return true;
            }
            catch (RuntimeException e) {
                Log.e("FlashlightMod", e.getMessage());
                return false;
            }
            catch (IOException e) {
                Log.d("FlashlightMod","Error setting preview: "+e);

                e.printStackTrace();
                return false;
            }
        }
        else
        {
            Log.d("FlashlightMod","Camera is not null");
            return true;
        }
    }

    /**
     * Activate the flashlight.
     * @precondition getCamera() has been called
     */
    private synchronized void turnOnFlash() {
        if (!isFlashOn) {
            if(!getCamera())
            {
                return;
            }
            isFlashOn = true;
            updateIcon();
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            Log.d("FlashlightMod","Turning flash on");

            // changing button/switch image
            //toggleButtonImage();
        }

    }

    /*
	 * Turning Off flash
	 * @precondition getCamera() has been called.
	 */
    private synchronized void turnOffFlash() {
        if (isFlashOn) {


            if (camera == null || params == null) {
                return;
            }
            isFlashOn = false;
            updateIcon();
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            camera.release();
            camera = null;

            Log.d("FlashlightMod","Turning flash off");
            // changing button/switch image
            //toggleButtonImage();
        }
    }

    private synchronized void updateIcon()
    {
        if(katrinaModuleListener != null)
        {
            Handler handler = new Handler(mContext.getMainLooper());

            handler.post(new Runnable(){


                @Override
                public void run() {
                    katrinaModuleListener.changeModuleIconImage(id,getIconImage());
                }
            });

        }

    }



    @Override
    public Drawable getIconImage() {

        if(isFlashOn) {
            return mContext.getResources().getDrawable(R.mipmap.ic_flashlight_on);
        }
        else
        {
            return mContext.getResources().getDrawable(R.mipmap.ic_flashlight_off);
        }

    }

    @Override
    public String getName() {
        return "Flashlight";
    }

    @Override
    public boolean onModuleClick(Context context) {


        executor.execute(this);
        return true;
    }

    @Override
    public void run()
    {
        if(isFlashOn)
        {
            turnOffFlash();
        }
        else
        {
            turnOnFlash();
        }
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
        this.id = id;
    }

    @Override
    public void registerKMListener(KatrinaModuleListener kmListener) {
        katrinaModuleListener = kmListener;
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
    /**
     * Needed to continually update SurfaceHolder
     */
    public void surfaceCreated(SurfaceHolder holder){
        mHolder = holder;
        try {
            Log.i("SurfaceHolder", "setting preview");
            camera.setPreviewDisplay(mHolder);
        } catch (IOException e){
            e.printStackTrace();
        }
        catch(RuntimeException e)
        {

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        Log.i("SurfaceHolder", "stopping preview");
        if(camera != null)
        {
            turnOffFlash();
        }
        mHolder = null;
    }


}
