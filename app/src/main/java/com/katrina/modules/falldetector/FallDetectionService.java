package com.katrina.modules.falldetector;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.katrina.ui.MainUI;

import java.util.Date;

/**
 * The Fall Detection Service is a background service waiting for
 * a fall to happen. The user of the class can define what happens
 * when a fall is detected.
 * @author kyle
 *
 */
public class FallDetectionService extends Service {

    /**
     * Instance of FallDetector to gather calculations from Accelerometer.
     */
	private FallDetector fallDetector;

    private long currentTime = System.currentTimeMillis();

    private static boolean isRunning = false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		fallDetector = new FallDetector(this);
		fallDetector.setCallback(new FallDetectorCallback(){

			@Override
			public void getValues(float a, float b, float c, float acceleration) {

                if(System.currentTimeMillis() - currentTime > 500) {
                    Log.d("FallDetectionService", "Values: " + a + " , " + b + " , " + c + "  = " + acceleration);

                    currentTime = System.currentTimeMillis();
                }
				
			}

			@Override
			public void onFallDetected() {
				Log.d("FallDetectionService","FALL DETECTED");

				fallDetector.pause();
                //Send Broadcast to FallDetectorModule letting know about fall detection
                sendBroadcast(new Intent(FallDetectorModule.FALL_DETECTED_ACTION));

			}
		});
		
	}
	
	@Override
	public int onStartCommand(Intent intent,int flags, int startId)
	{
		fallDetector.runDetector();
		return super.onStartCommand(intent, flags, startId);
	}

    public static boolean isRunning()
    {
        return isRunning;
    }

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		fallDetector.pause();
        isRunning = false;
	}
	
	

	

}
