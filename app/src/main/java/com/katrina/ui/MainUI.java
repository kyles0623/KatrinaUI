package com.katrina.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainUI extends Activity implements View.OnClickListener, View.OnLongClickListener, EmergencyListener {
    private GridView moduleGridView;  //Grid list of modules and APK apps on home screen.
    private ModuleAdapter moduleAdapter; //adapter to manage the modules displayed on the home screen.
    /*private ListView contactsListView;
    private ContactsAdapter contactsAdapter;*/
    private AlertDialog.Builder aBuild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ui);

        aBuild = new AlertDialog.Builder(this);

        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.preference_file),Context.MODE_PRIVATE);

        //Check to see if this is the first time Katrina has started.
        // boolean firstInit = prefs.getBoolean("firstInit",true);

        //if (firstInit) {
        //Intent i = new Intent(this,ContactsUI.class);
        //i.putExtra(data);
        //startActivityForResult(i,2000);
        //i.getExtra(data);

        //SharedPreferences.Editor e = prefs.edit();
        //e.putBoolean("firstInit",false);
        //e.commit();
        //}

        //setup the module list on the home screen.
        moduleGridView = (GridView) findViewById(R.id.moduleGridView);
        moduleAdapter = new ModuleAdapter(this);
        moduleGridView.setAdapter(moduleAdapter);
        moduleGridView.setOnItemClickListener(moduleAdapter);
        moduleGridView.setOnItemLongClickListener(moduleAdapter);

        //set contact's on long click listener.
        View contact1 = findViewById(R.id.contact1);
        contact1.setOnLongClickListener(this);
        View contact2 = findViewById(R.id.contact2);
        contact2.setOnLongClickListener(this);
        View contact3 = findViewById(R.id.contact3);
        contact3.setOnLongClickListener(this);
        View contact4 = findViewById(R.id.contact4);
        contact4.setOnLongClickListener(this);
        View contact5 = findViewById(R.id.contact5);
        contact5.setOnLongClickListener(this);

        /*TESTMOD t = new TESTMOD();

        for (int i=0;i<20;i++){ moduleAdapter.addModule(t); }*/

        //Start a new activity
        /*
        Intent i = new Intent(getApplicationContext(),MainUI.class);
        i.putExtra(data);
        startActivityForResult(i,0);
        i.getExtra(data);
        */

        //pass data back
        /*
        Intent i = new Intent();
        i.putExtra("result","0");
        setResult(0,i);
        */

        addAppsToHomeScreen();
    }

    @Override
    protected void onStop(){
        super.onStop();

        //Open preference file to save data.
        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.preference_file),Context.MODE_PRIVATE);
        //TODO save contacts and apps added to homescreen to preference file.
    }

    //add a module to the home screen.
    public void addModule(KatrinaModule mod){
        moduleAdapter.addModule(mod);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                moduleAdapter.notifyDataSetChanged();
            }
        });
    }

    //Android stuff.
    //What the heck does this even do?
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_ui, menu);
        return true;
    }

    //Android stuff.
    //What the heck does this even do?
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //when parts of the ui are clicked on.
    //only effects those that do not use an adapter.
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //call contact
            case R.id.contact1:
                Log.i("onClick", "C1");
                //Intent i = new Intent(this,ContactsUI.class);
                //startActivityForResult(i,1000);
                break;
            case R.id.contact2:
                Log.i("onClick", "C2");
                break;
            case R.id.contact3:
                Log.i("onClick", "C3");
                break;
            case R.id.contact4:
                Log.i("onClick", "C4");
                break;
            case R.id.contact5:
                Log.i("onClick", "C5");
                break;
            //open apps activity
            case R.id.apps:
                Log.i("onClick", "Apps");
                break;
            case R.id.emergency:
                Log.i("onClick", "Emergency");
                this.onEmergency();
                break;
            //open settings activity
            case R.id.settings:
                Log.i("onClick", "Settings");
                break;
        }
    }

    //contact slots
    static private final int PICK_CONTACT_1=1000;
    static private final int PICK_CONTACT_2=1001;
    static private final int PICK_CONTACT_3=1002;
    static private final int PICK_CONTACT_4=1003;
    static private final int PICK_CONTACT_5=1004;

    //using this to have the ability to set a single contact.
    @Override
    public boolean onLongClick(View v) {
        //setup intent.
        Intent cIntent = null;
        switch (v.getId()){
            case R.id.contact1:
            case R.id.contact2:
            case R.id.contact3:
            case R.id.contact4:
            case R.id.contact5:
                cIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                break;
        }

        //execute intent
        if (cIntent != null) {
            switch (v.getId()) {
                case R.id.contact1:
                    Log.i("onLongClick", "C1");
                    startActivityForResult(cIntent, PICK_CONTACT_1);
                    break;
                case R.id.contact2:
                    Log.i("onLongClick", "C2");
                    startActivityForResult(cIntent, PICK_CONTACT_2);
                    break;
                case R.id.contact3:
                    Log.i("onLongClick", "C3");
                    startActivityForResult(cIntent, PICK_CONTACT_3);
                    break;
                case R.id.contact4:
                    Log.i("onLongClick", "C4");
                    startActivityForResult(cIntent, PICK_CONTACT_4);
                    break;
                case R.id.contact5:
                    Log.i("onLongClick", "C5");
                    startActivityForResult(cIntent, PICK_CONTACT_5);
                    break;
            }
        }
        return false;
    }

    //used to receive data from separate activities started by this activity.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        /*Log.i("onActivityResult","requestCode: " + requestCode);
        Log.i("onActivityResult","resultCode: " + resultCode);*/
        if (resultCode == Activity.RESULT_OK){
            //Bundle b = data.getBundleExtra("contacts");
            /*Log.i("onActivityResult", data.getStringExtra("result"));*/

            //change a contact
            if (requestCode == PICK_CONTACT_1 || requestCode == PICK_CONTACT_2 || requestCode == PICK_CONTACT_3 || requestCode == PICK_CONTACT_4 || requestCode == PICK_CONTACT_5) {
                View contact=null;
                TextView cName;
                TextView cPhone;
                ImageView cImg;

                //get contact slot.
                switch (requestCode) {
                    case PICK_CONTACT_1:
                        contact = findViewById(R.id.contact1);
                        break;
                    case PICK_CONTACT_2:
                        contact = findViewById(R.id.contact2);
                        break;
                    case PICK_CONTACT_3:
                        contact = findViewById(R.id.contact3);
                        break;
                    case PICK_CONTACT_4:
                        contact = findViewById(R.id.contact4);
                        break;
                    case PICK_CONTACT_5:
                        contact = findViewById(R.id.contact5);
                        break;
                }

                //set contact data.
                if (contact != null) {
                    cName = (TextView) contact.findViewById(R.id.cName);
                    cPhone = (TextView) contact.findViewById(R.id.cPhone);
                    cImg = (ImageView) contact.findViewById(R.id.cImg);

                    String phoneNumber = "";
                    String phoneName = "";
                    Uri phoneImage = null;

                    Uri contactData = data.getData();
                    Cursor c =  getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        phoneName = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
                        phoneImage =  Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

                        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                    null, null);
                            phones.moveToFirst();
                            phoneNumber = phones.getString(phones.getColumnIndex("data1"));
                        }else{
                            aBuild.setTitle("Contact does not have a number.")
                                    .setMessage("Im sorry but the contact you have selected does not have a number associated with it.")
                                    .setPositiveButton("OK",null);
                            aBuild.create().show();
                            return; //error out, contact does not have a phone number.
                        }

                        //set the data.
                        cName.setText(phoneName);
                        cPhone.setText(phoneNumber);
                        if (phoneImage != null) cImg.setImageURI(phoneImage);
                        else cImg.setImageResource(R.mipmap.unknown);
                    }
                    c.close();
                }
            }
        }
    }

    @Override
    public void onEmergency() {
        //TODO Text all numbers with location. Call emergency services.
        Log.i("onEmergency","Emergency function called!");
    }

    //add all applications installed to phone to homescreen.
    //NOTE this is just for testing out the gridview section.
    private void addAppsToHomeScreen(){
        /*Thread appListThread = new Thread(){
            public void run(){
                Intent i = new Intent(Intent.ACTION_MAIN, null);
                i.addCategory(Intent.CATEGORY_LAUNCHER);

                PackageManager manager = getPackageManager();

                List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
                for(ResolveInfo ri:availableActivities){
                    //Log.i("List of Apps","Label: " + ri.loadLabel(manager).toString() + "  PackageName: " + ri.activityInfo.packageName.toString());
                    ModuleApp app = new ModuleApp(ri.loadLabel(manager).toString(),ri.activityInfo.packageName.toString(),ri.activityInfo.loadIcon(manager));
                    app.registerEmergencyListener(MainUI.this);
                    moduleAdapter.addModule(app);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() { moduleAdapter.notifyDataSetChanged(); }
                });
            }
        };
        appListThread.start();*/
        new AppSyncTask(this).execute();
    }

    private class AppSyncTask extends AsyncTask<Void,Integer,Void> {
        ProgressDialog progressDialog;
        Context mContext;

        public AppSyncTask(Context c){ mContext = c; }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBarVisibility(true);
            setProgressBarIndeterminate(false);
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Loading List of Applications...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            publishProgress(0);

            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);

            PackageManager manager = getPackageManager();

            List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);

            int maxSize = availableActivities.size();
            int currPos = 0;

            for(ResolveInfo ri:availableActivities){
                //Log.i("List of Apps","Label: " + ri.loadLabel(manager).toString() + "  PackageName: " + ri.activityInfo.packageName.toString());
                ModuleApp app = new ModuleApp(ri.loadLabel(manager).toString(),ri.activityInfo.packageName.toString(),ri.activityInfo.loadIcon(manager));
                app.registerEmergencyListener(MainUI.this);
                moduleAdapter.addModule(app);

                currPos++;
                publishProgress((int) ((currPos / (float) maxSize) * 100));

                if (isCancelled()) break;
            }

            publishProgress(100);
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressDialog.dismiss();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            moduleAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            setProgress(values[0]);
            progressDialog.setProgress(values[0]);
        }
    }
}


//Using this for single pick of phone contact;
/*
 @Override
 public void onActivityResult(int reqCode, int resultCode, Intent data) {
 super.onActivityResult(reqCode, resultCode, data);

 switch (reqCode) {
 case (PICK_CONTACT) :
   if (resultCode == Activity.RESULT_OK) {

     Uri contactData = data.getData();
     Cursor c =  managedQuery(contactData, null, null, null, null);
     if (c.moveToFirst()) {


         String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

         String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

           if (hasPhone.equalsIgnoreCase("1")) {
          Cursor phones = getContentResolver().query(
                       ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                       ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                       null, null);
             phones.moveToFirst();
              cNumber = phones.getString(phones.getColumnIndex("data1"));
             System.out.println("number is:"+cNumber);
           }
         String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


     }
   }
   break;
 }
 }
*/