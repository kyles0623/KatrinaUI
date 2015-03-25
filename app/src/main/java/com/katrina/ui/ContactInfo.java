package com.katrina.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.katrina.util.Utilities;

/**
 * Created by alatnet on 3/7/2015.
 */
public class ContactInfo{
    public String name ="";
    public String phone = "";
    public String type = "";
    public Uri photo = null;
    public Long id;

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

    public void text(/*Context c, */String msg){
        /*Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"+phone));
        sendIntent.putExtra("sms_body", msg);
        c.startActivity(sendIntent);*/

        if (Utilities.DEBUG) return;
        if (phone.isEmpty()) return;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone,null,msg,null,null);
    }
}
