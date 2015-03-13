package com.katrina.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

public class ContactsUI extends Activity implements View.OnClickListener {
    ContactsAdapter contactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_ui);

        ExpandableListView elView = (ExpandableListView) findViewById(R.id.contactsUI_ListView);
        contactsAdapter = new ContactsAdapter(this, getIntent().getIntExtra("maxSelection", -1));
        elView.setAdapter(contactsAdapter);
        //elView.setOnChildClickListener(contactsAdapter);
    }

    @Override
    public void onClick(View v) {
        Intent i = getIntent();
        i.putExtra("Contacts",contactsAdapter.getSelectedContacts());
        setResult(Activity.RESULT_OK,i);
        ContactsUI.this.finish();
    }
}
