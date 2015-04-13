package com.katrina.modules.falldetector;

/**
 * Defines the methods to be called when
 * values are wanted for updates and
 * for fall detection.
 */
public interface FallDetectorCallback {

    /**
     * Retrieve the current accelerometer values of the fall detection system.
     * @param a X-direction acceleration
     * @param b Y-direction acceleration
     * @param c Z-direction acceleration
     * @param acceleration Total acceleration
     */
	void getValues(float a, float b, float c, float acceleration);

    /**
     * To be called when the fall detector has detected.
     */
	void onFallDetected();
}
