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
import android.widget.ExpandableListView;

public class ContactsUI extends Activity implements View.OnClickListener {
    ContactsAdapter contactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_ui);

        ExpandableListView elView = (ExpandableListView)findViewById(R.id.contactsUI_ListView);
        contactsAdapter = new ContactsAdapter(this);
        elView.setAdapter(contactsAdapter);
        elView.setOnChildClickListener(contactsAdapter);
    }

    @Override
    public void onClick(View v) {
        //pass data back
        /*
        Intent i = new Intent();
        i.putExtra("result","0");
        setResult(0,i);
        */

        //TODO return all selected contacts

        Intent i = getIntent();
        i.putExtra("result","TEST RESULT!!!!");
        setResult(Activity.RESULT_OK,i);
        ContactsUI.this.finish();
    }
}
