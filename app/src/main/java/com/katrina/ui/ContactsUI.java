package com.katrina.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class ContactsUI extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contacts_ui);
    }

    @Override
    public void onClick(View v) {
        //pass data back
        /*
        Intent i = new Intent();
        i.putExtra("result","0");
        setResult(0,i);
        */
        Intent i = getIntent();
        i.putExtra("result","TEST RESULT!!!!");
        setResult(Activity.RESULT_OK,i);
        ContactsUI.this.finish();
    }
}
