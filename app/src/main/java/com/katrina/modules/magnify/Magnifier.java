package com.katrina.modules.magnify;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.katrina.ui.R;


public class Magnifier extends Activity {
	private static final String TAG = "CameraDemo";
	Camera camera;
	Preview preview;
	int currentZoomLevel = 0;
	int maxZoomLevel = 4;
	Button buttonClick;
	Button buttonCancel;
	private OnLongClickListener mLongClickListener = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_magnifying_glass);
		if (getResources().getConfiguration().orientation ==
            Configuration.ORIENTATION_PORTRAIT) 
        {
        	setContentView(R.layout.activity_magnifying_glass);
        	preview = new Preview(this);
        	preview.setOnLongClickListener(mLongClickListener);
    		((FrameLayout) findViewById(R.id.preview)).addView(preview);
        } 
        else 
        {
        	setContentView(R.layout.activity_magnifying_glass_landscape);
        	preview = new Preview(this);
        	preview.setOnLongClickListener(mLongClickListener);
    		((FrameLayout) findViewById(R.id.preview)).addView(preview);
        }
		
		
		//preview = new Preview(this);
		//((FrameLayout) findViewById(R.id.preview)).addView(preview);
		
		
		//ZoomControls zoomControls   =(ZoomControls)findViewById(R.id.CAMERA_ZOOM_CONTROLS);
        /*zoomControls.setIsZoomInEnabled(true);
        zoomControls.setIsZoomOutEnabled(true);   
        
        zoomControls.setOnZoomInClickListener(new OnClickListener(){
      		 public void onClick(View v){
            
                           Camera.Parameters params = preview.camera.getParameters();
                           int zoom = params.getZoom();
                           if (zoom < params.getMaxZoom())
                                   zoom++;
                           params.setZoom(zoom);
                           preview.camera.setParameters(params);

            }
    		}
   			);

          zoomControls.setOnZoomOutClickListener(new OnClickListener(){
          public void onClick(View v){
            
                           Camera.Parameters params = preview.camera.getParameters();
                           int zoom = params.getZoom();
                           if (zoom > 0)
                                   zoom--;
                           params.setZoom(zoom);
                           preview.camera.setParameters(params);

    	   }
         });
        */
        
        
		/*buttonClick = (Button) findViewById(R.id.buttonClick);
		buttonClick.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				preview.camera.autoFocus(null);

				preview.camera.takePicture(shutterCallback, rawCallback,
						jpegCallback);
			}
		});
		
		
		buttonCancel = (Button) findViewById(R.id.buttonCancel);
		buttonCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				preview.camera.stopPreview();			}
		});*/
		
		

		Log.d(TAG, "onCreate'd");
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	/** Handles data for raw picture */
	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};

	/** Handles data for jpeg picture */
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			FileOutputStream outStream = null;
			try {
				// write to local sandbox file system
				// outStream =
				// CameraDemo.this.openFileOutput(String.format("%d.jpg",
				// System.currentTimeMillis()), 0);
				// Or write to sdcard
				outStream = new FileOutputStream(String.format(
						"/sdcard/%d.jpg", System.currentTimeMillis()));
				outStream.write(data);
				outStream.close();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};
	
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
    {
        Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_magnifying_glass);
    }
     if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
    {
        Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_magnifying_glass_landscape );
    } 
  }

	 @Override
	 public void onBackPressed() {

         super.onBackPressed();
		 /*
		 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle("Preview Exit");

		// set dialog message
		alertDialogBuilder
			.setMessage("Do you want to exit the Magnifying Glass?")
			.setCancelable(false)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
		            Magnifier.super.onBackPressed();
					dialog.cancel();
				}
			  })
			.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close the dialog box and do nothing
					dialog.cancel();
				}
			});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();*/
}

	    
}