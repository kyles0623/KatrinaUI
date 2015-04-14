package com.katrina.modules.magnify;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.widget.Button;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.OnZoomChangeListener;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.ConditionVariable;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ZoomControls;



public class Preview extends SurfaceView implements SurfaceHolder.Callback {
public static String TAG = "camera";
private SurfaceHolder mHolder;
public Camera camera;

public Preview(Context context) 
{
	super(context);

	mHolder = getHolder();
	mHolder.addCallback(this);
	mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

}

public void surfaceCreated(SurfaceHolder holder) 
{

	camera = getCameraInstance();
	try {
			if (camera != null) 
			{
                camera.setPreviewDisplay(holder);
                camera.startPreview();
			}
		 }
	catch (IOException e) 
	{
        Log.d(TAG, "Error..Cannot start preview: " + e.getMessage());
	}

}

public void surfaceDestroyed(SurfaceHolder holder) 
{

	camera.stopPreview();
	camera.release();
	camera = null;

}

public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) 
{
	// Should handle preview orientation(rotation) etc.
	// Preview will stop and restart before format or orientation changes.

	if (mHolder.getSurface() == null) 
	{
        // preview does not exist
        return;
	}

	// stop preview before making changes
	try 
	{
        camera.stopPreview();
	} 
	catch (Exception e) 
	{
        // ignore: tried to stop a non-existent preview
	}

	// make any resize, rotate or reformatting changes here
	if(camera!=null)
	{
		Camera.Parameters parameters = camera.getParameters();
		List<Size> previewSizes = parameters.getSupportedPreviewSizes();
	    // You need to choose the most appropriate previewSize for your app
		Size previewSize = previewSizes.get(0);
		Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		if(display.getRotation() == Surface.ROTATION_0)
		{
			//parameters.setPreviewSize(previewSize.height, previewSize.width);
			camera.setDisplayOrientation(90);
		}

		if(display.getRotation() == Surface.ROTATION_90)
		{
			//parameters.setPreviewSize(previewSize.width, previewSize.height);
		}

		if(display.getRotation() == Surface.ROTATION_180)
		{
			//parameters.setPreviewSize(previewSize.height, previewSize.width);
		}

		if(display.getRotation() == Surface.ROTATION_270)
		{
			//parameters.setPreviewSize(previewSize.width, previewSize.height);
			camera.setDisplayOrientation(180);
		}

        parameters.setZoom(parameters.getMaxZoom());
		camera.setParameters(parameters);
		// start preview with new settings
		try 
		{
			camera.setPreviewDisplay(mHolder);
			camera.startPreview();

		} 
		catch (Exception e) 
		{
			Log.d(TAG, "Error while starting preview with new settings: " + e.getMessage());
		}
	}
}

/** A safe way to get an instance of the Camera object. */
public Camera getCameraInstance() 
{
	Camera c = null;
	try 
	{
        c = Camera.open(); // attempt to get a Camera instance

        // initial camera
        c.getParameters().setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        c.autoFocus(autofocus);
        c.setZoomChangeListener(zoomchange);
        c.startSmoothZoom(c.getParameters().getZoom());
	} 
	catch (Exception e) 
	{
        // Camera is not available (in use or does not exist)
        Log.d(TAG,"Camera Doesn't exist");
	}
	return c; // returns null if camera is unavailable
}

//camera autofocus callback
public AutoFocusCallback autofocus = new AutoFocusCallback() 
{

        @Override
        public void onAutoFocus(boolean success, Camera camera) 
        {
                // TODO Auto-generated method stub

        }
};


public OnZoomChangeListener zoomchange = new OnZoomChangeListener() 
{

	@Override
	public void onZoomChange(int zoomValue, boolean stopped, Camera camera) 
	{
        // TODO Auto-generated method stub
        Log.i("camera", "zoom change");
        camera.startSmoothZoom(zoomValue);
	}
};

}
