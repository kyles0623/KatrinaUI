package com.katrina.modules;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * The KatrinaModule interface contains the necessary
 * methods for a module to be integrated in the Katrina
 * application.
 * Created by Alexander on 2/27/2015.
 */
public interface KatrinaModule {

    //VERSION 2.0
    enum MOD_TYPE{

        //Basic Module Type
        MODULE,

        //Separate Application on PHone
        APP,

        //???
        MISC,

        //Runs in background. No view attached
        BACKGROUND }

    /**
     * Retrieve the Drawable Object for the icon associated with the Module
     * @return Drawable
     */
    public Drawable getIconImage(); //set a module's displayed image.

    /**
     * Retrieve the name associated with the Module
     * @return
     */
    public String getName();  //get the module's name.

    /**
     * This method will be called when a user
     * clicks on the module icon
     * @param c
     * @return
     */
    public boolean onModuleClick(Context context);

    /**
     * This method will be called when a user
     * long clicks on the module icon
     * @param c
     * @return
     */
    public boolean onModuleLongClick(Context c);

    /**
     *
     * @return
     */
    public String getError();

    /**
     * Retrieve the module type of this Module.
     * Modtypes:
     *      MOD_TYPE.MODULE,
     *      MOD_TYPE.APP,
     *      MOD_TYPE.MISC,
     *      MOD_TYPE.BACKGROUND
     * @return
     */
    public MOD_TYPE getModuleType(); //get a module type.

    /**
     * Register an EmergencyListener if the module
     * can call for an emergency.
     * @param emergencyListener
     */
    public void registerEmergencyListener(EmergencyListener emergencyListener); //optional.  Use this to notify an emergency situation.

    /**
     * Set the ID. This ID is to be set statically
     * for the system to remember the module.
     * @param id
     */
    public void setID(int id); //use this to set your module id.  REQUIRED FOR KatrinaModuleListener!  //MODULE and APP

    /**
     * Register a KatrinaModuleListener
     * for updating outside components
     * such as UI components through this listener.
     * @param kmListener
     */
    public void registerKMListener(KatrinaModuleListener kmListener); //use this to change your icon. MUST HAVE AN ID!  //MODULE and APP

    /**
     * Set whether the module
     * is active or not.
     * @param active
     */
    public void setActive(boolean active);

    /**
     * Retrieve the state
     * of the activeness of the
     * Module
     * @return
     */
    public boolean isActive();

    /**
     * Retrieve the unique
     * id of the Module.
     * @return
     */
    public String getUniqueID();

    /**
     * Called to initialize the state of the KatrinaModule.
     * @param context Context KatrinaModule is attached to.
     */
    public void initialize(Context context);

    /**
     * Called when the application is closed.
     */
    public void stop();
}
