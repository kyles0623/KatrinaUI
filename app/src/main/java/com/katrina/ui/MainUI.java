package com.katrina.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.katrina.modules.EmergencyListener;
import com.katrina.modules.KatrinaModule;
import com.katrina.modules.ModuleApp;
import com.katrina.util.Utilities;

import java.util.List;

public class MainUI extends Activity implements View.OnClickListener, View.OnLongClickListener, EmergencyListener {
    private GridView moduleGridView;  //Grid list of modules and APK apps on home screen.
    private ModuleAdapter moduleAdapter; //adapter to manage the modules displayed on the home screen.
    private ListView appsListView;
    private AppsAdapter appsAdapter;
    private AlertDialog.Builder aBuild;
    private ContactInfo[] contactInfo = new ContactInfo[5];
    private boolean firstInit = true;

    private View mainUI = null;
    private View appUI = null;

    public MainUI(){
        super();
        for(int i=0;i<contactInfo.length;i++) contactInfo[i] = new ContactInfo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater layoutInflater = getLayoutInflater();
        mainUI = layoutInflater.inflate(R.layout.activity_main_ui,null);
        appUI = layoutInflater.inflate(R.layout.activity_main_ui_appslist,null);
        setContentView(mainUI);

        aBuild = new AlertDialog.Builder(this);

        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.preference_file),Context.MODE_PRIVATE);

        //Check to see if this is the first time Katrina has started.
        firstInit = prefs.getBoolean("firstInit",true);

        if (firstInit) {
            AlertDialog.Builder aBuild = new AlertDialog.Builder(this);
            aBuild.setTitle("Setup")
                    .setMessage("Welcome to Katrina.\nWe would like to set up your main contacts that you will use either for emergency or for normal calls.")
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(MainUI.this,ContactsUI.class);
                            i.putExtra("maxSelection",5);
                            startActivityForResult(i, R.id.ContactSelection);
                        }
                    });
            aBuild.create().show();

            SharedPreferences.Editor e = prefs.edit();
            e.putBoolean("firstInit",false);
            e.commit();
        }else{
            firstInit = false;
            clearContacts();
            loadContacts(prefs);
        }

        //setup the module list on the home screen.
        moduleGridView = (GridView) mainUI.findViewById(R.id.moduleGridView);
        moduleAdapter = new ModuleAdapter(this);
        moduleGridView.setAdapter(moduleAdapter);
        moduleGridView.setOnItemClickListener(moduleAdapter);
        moduleGridView.setOnItemLongClickListener(moduleAdapter);

        //setup the app list.
        appsListView = (ListView) appUI.findViewById(R.id.appListView);
        appsAdapter = new AppsAdapter(this,moduleAdapter);
        appsListView.setAdapter(appsAdapter);
        appsListView.setOnItemClickListener(appsAdapter);
        appsListView.setOnItemLongClickListener(appsAdapter);

        //set contact's on long click listener.
        View contact1 = mainUI.findViewById(R.id.contact1);
        View contact2 = mainUI.findViewById(R.id.contact2);
        View contact3 = mainUI.findViewById(R.id.contact3);
        View contact4 = mainUI.findViewById(R.id.contact4);
        View contact5 = mainUI.findViewById(R.id.contact5);
        contact1.setOnLongClickListener(this);
        contact2.setOnLongClickListener(this);
        contact3.setOnLongClickListener(this);
        contact4.setOnLongClickListener(this);
        contact5.setOnLongClickListener(this);

        Button emergency = (Button)mainUI.findViewById(R.id.action_emergency);
        emergency.setOnLongClickListener(this);

        //add apps to app list
        new AppSyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //Load Modules
        String[] modules = getResources().getStringArray(R.array.Modules); //get module list
        try {
            for (String module : modules) { //for each module
                Class cls = Class.forName(module); //get the class from the module list
                KatrinaModule moduleInstance = (KatrinaModule) cls.newInstance(); //crete a new instance (i.e. new Class)
                moduleInstance.registerKMListener(moduleAdapter); //register KMListener
                moduleInstance.registerEmergencyListener(this); //register EmergencyListener
                moduleAdapter.addModule(moduleInstance); //add module to home screen
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.i("ClassNotFoundException",e.toString());
        } catch (InstantiationException e) {
            e.printStackTrace();
            Log.i("InstantiationException",e.toString());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.i("IllegalAccessException",e.toString());
        }
    }

    @Override
    protected void onStop(){
        super.onStop();

        if (!firstInit) {
            //Open preference file to save data.
            SharedPreferences prefs = this.getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);

            SharedPreferences.Editor e = prefs.edit();

            for (int i = 0; i < contactInfo.length; i++) {
                e.putString("Contact" + i + "-name", contactInfo[i].name);
                e.putString("Contact" + i + "-number", contactInfo[i].phone);
                e.putString("Contact" + i + "-type", contactInfo[i].type);
                if (contactInfo[i].photo != null) e.putString("Contact" + i + "-photo", contactInfo[i].photo.toString());
                else e.putString("Contact" + i + "-photo", "null");
            }

            e.commit();
        }else{
            firstInit = false;
        }
    }

    private void loadContacts(SharedPreferences sP){
        for (int i=0;i<contactInfo.length;i++){
            View contact=null;
            TextView cName;
            TextView cPhone;
            ImageView cImg;

            contactInfo[i].name = sP.getString("Contact"+i+"-name","Set Contact");
            contactInfo[i].phone = sP.getString("Contact"+i+"-number","(777)-777-7777");
            contactInfo[i].type = sP.getString("Contact"+i+"-type","");
            contactInfo[i].photo = Utilities.checkUriExists(this,Uri.parse(sP.getString("Contact"+i+"-photo","null")));

            switch(i+1){
                case 1:
                    contact=mainUI.findViewById(R.id.contact1);
                    break;
                case 2:
                    contact=mainUI.findViewById(R.id.contact2);
                    break;
                case 3:
                    contact=mainUI.findViewById(R.id.contact3);
                    break;
                case 4:
                    contact=mainUI.findViewById(R.id.contact4);
                    break;
                case 5:
                    contact=mainUI.findViewById(R.id.contact5);
                    break;
            }

            if (contact != null) {
                cName = (TextView) contact.findViewById(R.id.cName);
                cPhone = (TextView) contact.findViewById(R.id.cPhone);
                cImg = (ImageView) contact.findViewById(R.id.cImg);

                if (contactInfo[i].name.isEmpty()){
                    contactInfo[i].name = "";
                    contactInfo[i].phone = "";
                    contactInfo[i].type = "";
                    contactInfo[i].photo = null;

                    cName.setText("Set Contact");
                    cPhone.setText("(777)-777-7777");
                    cImg.setImageResource(R.mipmap.unknown);
                }else {
                    cName.setText(contactInfo[i].name);
                    if (!contactInfo[i].type.isEmpty())
                        cPhone.setText(contactInfo[i].phone + " (" + contactInfo[i].type + ")");
                    else cPhone.setText(contactInfo[i].phone);

                    if (contactInfo[i].photo != null) cImg.setImageURI(contactInfo[i].photo);
                    else cImg.setImageResource(R.mipmap.unknown);
                }
            }
        }
    }

    private void clearContact(int slot){
        contactInfo[slot].phone = "";
        contactInfo[slot].name = "";
        contactInfo[slot].type = "";
        contactInfo[slot].photo = null;

        View contact=null;
        TextView cName;
        TextView cPhone;
        ImageView cImg;

        switch(slot+1){
            case 1:
                contact=mainUI.findViewById(R.id.contact1);
                break;
            case 2:
                contact=mainUI.findViewById(R.id.contact2);
                break;
            case 3:
                contact=mainUI.findViewById(R.id.contact3);
                break;
            case 4:
                contact=mainUI.findViewById(R.id.contact4);
                break;
            case 5:
                contact=mainUI.findViewById(R.id.contact5);
                break;
        }

        if (contact != null) {
            cName = (TextView) contact.findViewById(R.id.cName);
            cPhone = (TextView) contact.findViewById(R.id.cPhone);
            cImg = (ImageView) contact.findViewById(R.id.cImg);

            cName.setText("Set Contact");
            cPhone.setText("(777)-777-7777");
            cImg.setImageResource(R.mipmap.unknown);
        }
    }

    private void clearContacts(){ for (int i=0;i<contactInfo.length;i++) clearContact(i); }

    //add a module to the home screen.
    /*public void addModule(KatrinaModule mod){
        moduleAdapter.addModule(mod);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                moduleAdapter.notifyDataSetChanged();
            }
        });
    }*/

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
        switch (item.getItemId()){
            case R.id.action_settings:
                return true;
            case R.id.action_set_contacts:
                Intent i = new Intent(MainUI.this,ContactsUI.class);
                i.putExtra("maxSelection",5);
                startActivityForResult(i, R.id.ContactSelection);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //when parts of the ui are clicked on.
    //only effects those that do not use an adapter.
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.apps:
                setContentView(appUI);
                break;
            case R.id.backToMain:
                setContentView(mainUI);
                break;
            /*//call contact
            case R.id.contact1:
                Log.i("onClick", "C1");
                //Intent i = new Intent(this,ContactsUI.class);
                //startActivityForResult(i,2000);
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
                break;*/
        }
    }

    //using this to call a contact.
    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.contact1:
                contactInfo[0].call(this);
                break;
            case R.id.contact2:
                contactInfo[1].call(this);
                break;
            case R.id.contact3:
                contactInfo[2].call(this);
                break;
            case R.id.contact4:
                contactInfo[3].call(this);
                break;
            case R.id.contact5:
                contactInfo[4].call(this);
                break;
            case R.id.action_emergency:
                this.onEmergency();
                break;
        }

        /*
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
                    startActivityForResult(cIntent, R.id.contact1);
                    break;
                case R.id.contact2:
                    Log.i("onLongClick", "C2");
                    startActivityForResult(cIntent, R.id.contact2);
                    break;
                case R.id.contact3:
                    Log.i("onLongClick", "C3");
                    startActivityForResult(cIntent, R.id.contact3);
                    break;
                case R.id.contact4:
                    Log.i("onLongClick", "C4");
                    startActivityForResult(cIntent, R.id.contact4);
                    break;
                case R.id.contact5:
                    Log.i("onLongClick", "C5");
                    startActivityForResult(cIntent, R.id.contact5);
                    break;
            }
        }*/
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

            switch (requestCode){
                case R.id.ContactSelection:
                    clearContacts();
                    Bundle contacts = data.getBundleExtra("Contacts");
                    if (contacts != null){
                        int currContact = 1;
                        for (String key : contacts.keySet()){
                            View contact=null;
                            TextView cName;
                            TextView cPhone;
                            ImageView cImg;

                            Bundle contactData = contacts.getBundle(key);
                            switch(currContact){
                                case 1:
                                    contact=mainUI.findViewById(R.id.contact1);
                                    Log.i("onActivityResult","Setting contact 1");
                                    break;
                                case 2:
                                    contact=mainUI.findViewById(R.id.contact2);
                                    Log.i("onActivityResult","Setting contact 2");
                                    break;
                                case 3:
                                    contact=mainUI.findViewById(R.id.contact3);
                                    Log.i("onActivityResult","Setting contact 3");
                                    break;
                                case 4:
                                    contact=mainUI.findViewById(R.id.contact4);
                                    Log.i("onActivityResult","Setting contact 4");
                                    break;
                                case 5:
                                    contact=mainUI.findViewById(R.id.contact5);
                                    Log.i("onActivityResult","Setting contact 5");
                                    break;
                            }

                            if (contact != null) {
                                cName = (TextView) contact.findViewById(R.id.cName);
                                cPhone = (TextView) contact.findViewById(R.id.cPhone);
                                cImg = (ImageView) contact.findViewById(R.id.cImg);

                                String type = contactData.getString("Type");
                                cName.setText(contactData.getString("Name"));

                                if (!type.isEmpty()) cPhone.setText(contactData.getString("Number") + " ("+type+")");
                                else cPhone.setText(contactData.getString("Number"));

                                String photo = contactData.getString("Photo");
                                if (photo.equals("null")) cImg.setImageResource(R.mipmap.unknown);
                                else cImg.setImageURI(Uri.parse(photo));

                                contactInfo[currContact-1].name = contactData.getString("Name");
                                contactInfo[currContact-1].phone = contactData.getString("Number");
                                contactInfo[currContact-1].type = type;
                                if (photo.equals("null")) contactInfo[currContact-1].photo = null;
                                else contactInfo[currContact-1].photo = Uri.parse(photo);
                            }

                            currContact++;
                        }
                    }
                    break;
                //change contact
                //may just not use this.
                case R.id.contact1:
                case R.id.contact2:
                case R.id.contact3:
                case R.id.contact4:
                case R.id.contact5:
                    View contact=mainUI.findViewById(requestCode);
                    TextView cName;
                    TextView cPhone;
                    ImageView cImg;

                    //set contact data.
                    if (contact != null) {
                        cName = (TextView) contact.findViewById(R.id.cName);
                        cPhone = (TextView) contact.findViewById(R.id.cPhone);
                        cImg = (ImageView) contact.findViewById(R.id.cImg);

                        String phoneNumber;
                        String phoneName;
                        Uri phoneImage;

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
                    break;
            }
        }
    }

    @Override
    public void onEmergency() {
        if (Utilities.DEBUG) return;
        //TODO Text all numbers with location. Call emergency services.

        for (ContactInfo c : contactInfo) c.text("EMERGENCY!");

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:911"));
        startActivity(callIntent);
    }

    //apparently it works really well.
    private class AppSyncTask extends AsyncTask<Void,Integer,Void> {
        ProgressDialog progressDialog;
        Context mContext;

        public AppSyncTask(Context c){ mContext = c; }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Loading List of Applications...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);

            PackageManager manager = getPackageManager();

            List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);

            int maxSize = availableActivities.size();
            int currPos = 0;
            publishProgress(currPos,maxSize);

            for(ResolveInfo ri:availableActivities){
                ModuleApp app = new ModuleApp(ri.loadLabel(manager).toString(),ri.activityInfo.packageName,ri.activityInfo.loadIcon(manager));
                appsAdapter.addModule(app);

                currPos++;
                publishProgress(currPos,maxSize);

                if (isCancelled()) break;
            }

            publishProgress(maxSize,maxSize);
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
            appsAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
            progressDialog.setMax(values[1]);
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