package com.katrina.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.katrina.util.Utilities;

/**
 * Represents a Contact in the users phone.
 * Contains basic information such as
 * name, phone number, and photo.
 * Created by alatnet on 3/7/2015.
 */
public class ContactInfo{
    public String name ="";
    public String phone = "";
    public String type = "";
    public Uri photo = null;
    public Long id;

    /**
     * Call the contact using the phones
     * default call application.
     * @param c Context to call user from
     */
    public void call(Context c){
        if (Utilities.DEBUG){
            Toast.makeText(c, "DEBUG! call Executed.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(phone.isEmpty()) return;
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+phone));
        c.startActivity(callIntent);
    }

    /**
     * Send the contact in the background.
     * @param msg Message to send contact
     */
    public void text(String msg){
        if (Utilities.DEBUG) return;
        if (phone.isEmpty()) return;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone,null,msg,null,null);
    }
}
